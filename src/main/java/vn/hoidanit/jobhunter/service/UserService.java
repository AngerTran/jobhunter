package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;

public interface UserService {

    User handelCreateUser(User user);

    ResultPaginationDTO getAllUsers(Pageable pageable);

    User getUserById(Long id);

    User handelUpdateUser(User user);

    User getUserByEmail(String email);

    void handelDeleteUser(Long id);
}
