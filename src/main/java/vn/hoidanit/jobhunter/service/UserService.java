package vn.hoidanit.jobhunter.service;

import java.util.List;

import vn.hoidanit.jobhunter.domain.User;

public interface UserService {

    User handelCreateUser(User user);

    void handelDeleteUser(Long id);

    List<User> getAllUsers();

    User getUserById(Long id);

    User handelUpdateUser(User user);

    User getUserByEmail(String email);
}
