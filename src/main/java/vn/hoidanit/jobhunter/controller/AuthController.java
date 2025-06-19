package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.req.ReLoginDTO;
import vn.hoidanit.jobhunter.domain.res.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.SecurityUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final SecurityUtil securityUtil;
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final UserService userService;
        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReLoginDTO ReLoginDTO) {

                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                ReLoginDTO.getEmail(), ReLoginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // create a token

                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();
                User u = userService.getUserByEmail(ReLoginDTO.getEmail());
                if (u != null) {
                        res.setUser(new ResLoginDTO.UserInfo(
                                        u.getId(),
                                        u.getEmail(),
                                        u.getName()));
                }
                String token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
                res.setAccessToken(token);

                String refreshToken = this.securityUtil.createRefreshToken(ReLoginDTO.getEmail(), res);

                // update refresh token
                userService.updateUserToken(ReLoginDTO.getEmail(), refreshToken);

                // set cookie
                ResponseCookie springCookie = ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                // .domain("example.com")
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                                .body(res);
        }

        @GetMapping("auth/account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : null;

                // ResLoginDTO.UserInfo userLogin = new ResLoginDTO.UserInfo();
                ResLoginDTO res = new ResLoginDTO();
                User u = userService.getUserByEmail(email);
                ResLoginDTO.UserGetAccount userAccount = new ResLoginDTO.UserGetAccount();
                if (u != null) {
                        res.setUser(new ResLoginDTO.UserInfo(
                                        u.getId(),
                                        u.getEmail(),
                                        u.getName()));
                        userAccount.setUser(res.getUser());
                }
                return ResponseEntity.ok().body(userAccount);
        }

        @GetMapping("auth/refresh")
        public ResponseEntity<ResLoginDTO> getrefreshToken(
                        @CookieValue(value = "refreshToken") String refreshToken) throws IdInvalidException {
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
                String email = decodedToken.getSubject();

                // check user by token + email
                User user = userService.getUserByEmailAndRefreshToken(email, refreshToken);
                if (user == null) {
                        throw new IdInvalidException("Refresh token is invalid or user does not exist");
                }
                // issue new access token and set refresh token
                ResLoginDTO res = new ResLoginDTO();
                User u = userService.getUserByEmail(email);
                if (u != null) {
                        res.setUser(new ResLoginDTO.UserInfo(
                                        u.getId(),
                                        u.getEmail(),
                                        u.getName()));
                }
                String token = this.securityUtil.createAccessToken(email, res.getUser());
                res.setAccessToken(token);

                String newrefreshToken = this.securityUtil.createRefreshToken(email, res);

                // update refresh token
                userService.updateUserToken(email, refreshToken);

                // set cookie
                ResponseCookie springCookie = ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                // .domain("example.com")
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                                .body(res);

        }

        @PostMapping("auth/logout")
        public ResponseEntity<Void> logOut() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : null;

                if (email == null) {
                        throw new IdInvalidException("User is not logged in");
                }

                userService.updateUserToken(email, null);

                // set cookie
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refreshToken", null)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .build();
        }

}
