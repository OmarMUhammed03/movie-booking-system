package com.moviebooking.user_service.service;

import com.moviebooking.shared.dto.PaginationRequest;
import com.moviebooking.shared.dto.PaginationResponse;
import com.moviebooking.shared.event.UserRegisteredEvent;
import com.moviebooking.user_service.dto.UserDto;
import com.moviebooking.user_service.dto.UserPatchDto;
import com.moviebooking.user_service.dto.UserUpdateDto;
import com.moviebooking.user_service.exception.ResourceNotFoundException;
import com.moviebooking.user_service.mapper.UserMapper;
import com.moviebooking.user_service.model.User;
import com.moviebooking.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public PaginationResponse<UserDto> getAllUsers(PaginationRequest paginationRequest) {
        log.info("Fetching all users from repository with pagination: page={}, size={}, name={}", 
                paginationRequest.getPage(), paginationRequest.getSize(), paginationRequest.getName());
        
        Pageable pageable = PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize());
        Page<User> userPage = userRepository.findUsersByName(paginationRequest.getName(), pageable);
        
        List<UserDto> users = userPage.getContent().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        
        log.info("Returning {} users out of {} total", users.size(), userPage.getTotalElements());
        
        return PaginationResponse.<UserDto>builder()
                .content(users)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        log.info("Found user with id: {}", id);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByAuthUserId(UUID authUserId) {
        log.info("Fetching user by authUserId: {}", authUserId);
        User user = userRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with authUserId: " + authUserId));
        log.info("Found user with authUserId: {}", authUserId);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, @Valid UserUpdateDto updateDto) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFirstName(updateDto.getFirstName());
        user.setLastName(updateDto.getLastName());
        user.setPhone(updateDto.getPhone());

        UserDto updatedUser = userMapper.toDto(userRepository.save(user));
        log.info("Successfully updated user with id: {}", id);
        return updatedUser;
    }

    @Transactional
    public UserDto patchUser(UUID id, UserPatchDto patchDto) {
        log.info("Patching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (patchDto.getFirstName() != null) {
            user.setFirstName(patchDto.getFirstName());
        }
        if (patchDto.getLastName() != null) {
            user.setLastName(patchDto.getLastName());
        }
        if (patchDto.getPhone() != null) {
            user.setPhone(patchDto.getPhone());
        }

        UserDto patchedUser = userMapper.toDto(userRepository.save(user));
        log.info("Successfully patched user with id: {}", id);
        return patchedUser;
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Successfully deleted user with id: {}", id);
    }

    @Transactional
    public void createUserFromEvent(UserRegisteredEvent event) {
        log.info("Creating user profile for authUserId: {}", event.getAuthUserId());
        
        if (userRepository.findByAuthUserId(event.getAuthUserId()).isPresent()) {
            log.warn("User profile already exists for authUserId: {}", event.getAuthUserId());
            return;
        }

        User user = User.builder()
                .authUserId(event.getAuthUserId()).firstName(event.getFirstName())
                .lastName(event.getLastName()).phone(event.getPhone())
                .build();

        userRepository.save(user);
    }
}
