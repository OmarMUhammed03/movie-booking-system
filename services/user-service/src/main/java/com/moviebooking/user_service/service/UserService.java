package com.moviebooking.user_service.service;

import com.moviebooking.shared.event.UserRegisteredEvent;
import com.moviebooking.user_service.dto.UserDto;
import com.moviebooking.user_service.dto.UserUpdateDto;
import com.moviebooking.user_service.exception.ResourceNotFoundException;
import com.moviebooking.user_service.mapper.UserMapper;
import com.moviebooking.user_service.model.User;
import com.moviebooking.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByAuthUserId(UUID authUserId) {
        User user = userRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with authUserId: " + authUserId));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, @Valid UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFirstName(updateDto.getFirstName());
        user.setLastName(updateDto.getLastName());
        user.setPhone(updateDto.getPhone());

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
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
