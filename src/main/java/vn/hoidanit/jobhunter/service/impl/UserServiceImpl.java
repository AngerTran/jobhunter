package vn.hoidanit.jobhunter.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handelCreateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void handelDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public ResUpdateUserDTO handelUpdateUser(ResUpdateUserDTO incoming) {

        User user = this.getUserById(incoming.getId());
        if (user == null) {
            throw new IdInvalidException("User với id " + incoming.getId() + " không tồn tại");
        }
        user.setName(incoming.getName());
        user.setAge(incoming.getAge());
        user.setGender(incoming.getGender());
        user.setAddress(incoming.getAddress());
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);
        return convertToResUpdateDTO(saved);
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        return null; // or throw an exception if user not found
    }

    @Override
    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUsers.getTotalPages());
        mt.setTotal((int) pageUsers.getTotalElements());

        List<ResUserDTO> listUser = pageUsers.getContent()
                .stream()
                .map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setMeta(mt);
        rs.setResult(listUser);

        return rs;
    }

    @Override
    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public ResCreateUserDTO convertToResDTO(User user) {
        ResCreateUserDTO dto = new ResCreateUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    @Override
    public ResUpdateUserDTO convertToResUpdateDTO(User user) {
        ResUpdateUserDTO dto = new ResUpdateUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setGender(user.getGender());
        dto.setAge(user.getAge());
        dto.setAddress(user.getAddress());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @Override
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO dto = new ResUserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setAge(user.getAge());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    @Override
    public void updateUserToken(String email, String token) {
        User userOptional = this.getUserByEmail(email);
        if (userOptional != null) {
            userOptional.setRefreshToken(token);
            userOptional.setUpdatedAt(Instant.now());
            this.userRepository.save(userOptional);
        } else {
            throw new IdInvalidException("User with email " + email + " does not exist");
        }
    }

    @Override
    public User getUserByEmailAndRefreshToken(String email, String refreshToken) {
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }
}
