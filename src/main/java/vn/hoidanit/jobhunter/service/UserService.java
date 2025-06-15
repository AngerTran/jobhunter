package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;

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

}
