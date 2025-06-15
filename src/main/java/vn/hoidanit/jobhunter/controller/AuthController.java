package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.SecurityUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create a token

        String token = this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        res.setAccessToken(token);
        User u = userService.getUserByEmail(loginDTO.getEmail());
        if (u != null) {
            res.setUser(new ResLoginDTO.UserInfo(
                    u.getId(),
                    u.getEmail(),
                    u.getName()));
        }

        return ResponseEntity.ok(res);
    }

}

// @RestController
// @RequestMapping("/api/v1")
// public class AuthController {

// private final AuthenticationManagerBuilder authManagerBuilder;
// private final SecurityUtil securityUtil;
// private final UserService userService;

// public AuthController(AuthenticationManagerBuilder authManagerBuilder,
// SecurityUtil securityUtil,
// UserService userService) {
// this.authManagerBuilder = authManagerBuilder;
// this.securityUtil = securityUtil;
// this.userService = userService;
// }

// @PostMapping("/login")
// public ResLoginDTO login(@Valid @RequestBody LoginDTO loginDTO) {

// /* 1. Xác thực */
// Authentication auth = authManagerBuilder.getObject().authenticate(
// new UsernamePasswordAuthenticationToken(
// loginDTO.getEmail(), loginDTO.getPassword()));
// SecurityContextHolder.getContext().setAuthentication(auth);

// /* 2. Tạo JWT */
// String token = securityUtil.createToken(auth);

// /* 3. Lấy thông tin user */
// User u = userService.getUserByEmail(loginDTO.getEmail());

// /* 4. Map ra DTO */
// ResLoginDTO dto = new ResLoginDTO();
// dto.setAccessToken(token);
// dto.setUser(new ResLoginDTO.UserInfo(
// u.getId(),
// u.getEmail(),
// u.getName()));

// /* 5. Trả DTO – FormatApiResponse sẽ tự bọc */
// return dto;
// }
// }
