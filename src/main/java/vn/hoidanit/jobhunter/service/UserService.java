package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.res.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.res.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.res.ResUserDTO;
import vn.hoidanit.jobhunter.domain.res.ResultPaginationDTO;

public interface UserService {

    User handelCreateUser(User user);

    // ResultPaginationDTO getAllUsers(Pageable pageable);

    User getUserById(Long id);

    ResUpdateUserDTO handelUpdateUser(ResUpdateUserDTO user);

    User getUserByEmail(String email);

    void handelDeleteUser(Long id);

    ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable);

    boolean isEmailExist(String email);

    ResCreateUserDTO convertToResDTO(User user);

    ResUpdateUserDTO convertToResUpdateDTO(User user);

    ResUserDTO convertToResUserDTO(User user);

    void updateUserToken(String email, String token);

    User getUserByEmailAndRefreshToken(String email, String refreshToken);
}
