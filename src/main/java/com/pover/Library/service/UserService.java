package com.pover.Library.service;

import com.pover.Library.dto.*;
import com.pover.Library.model.User;
import com.pover.Library.model.enums.Role;
import com.pover.Library.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticateUser(String memberNumber, String password) {
        Optional<User> existingUser = userRepository.findByMemberNumber(memberNumber);
        if (existingUser.isPresent() && password.equals(existingUser.get().getPassword())) {
            return existingUser;
        }
        return Optional.empty();
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByMemberNumber(userRequestDto.getMember_number())) {
            throw new IllegalArgumentException("Entered personal number is already in the system");
        }

        User user = new User();
        user.setFirst_name(userRequestDto.getFirst_name());
        user.setLast_name(userRequestDto.getLast_name());
        user.setEmail(userRequestDto.getEmail());
        user.setMemberNumber(userRequestDto.getMember_number());
        user.setPassword(userRequestDto.getPassword());
        user.setRole(Role.USER);

        userRepository.save(user);
        return convertToDto(user);
    }

    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(user);
    }

    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public BasicUserProfileResponseDto updateUserProfile(String memberNumber, BasicUserProfileRequestDto basicUserProfileRequestDto) {
        User user = userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (basicUserProfileRequestDto.getFirst_name() != null) {
            user.setFirst_name(basicUserProfileRequestDto.getFirst_name());
        }
        if (basicUserProfileRequestDto.getLast_name() != null) {
            user.setLast_name(basicUserProfileRequestDto.getLast_name());
        }
        if (basicUserProfileRequestDto.getEmail() != null) {
            user.setEmail(basicUserProfileRequestDto.getEmail());
        }

        userRepository.save(user);

        return new BasicUserProfileResponseDto(user.getFirst_name(), user.getLast_name(), user.getEmail(), List.of());
    }

    public ExtendedUserProfileResponseDto getUserProfileByMemberNumber(String memberNumber) {
        User user = userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new ExtendedUserProfileResponseDto(
                user.getFirst_name(),
                user.getLast_name(),
                user.getEmail(),
                user.getPassword(),
                user.getMemberNumber(),
                List.of()
        );
    }

    @Transactional
    public ExtendedUserProfileResponseDto updateUserProfileByMemberNumber(ExtendedUserProfileRequestDto extendedUserProfileRequestDto) {
        String memberNumber = extendedUserProfileRequestDto.getMember_number();
        User user = userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (extendedUserProfileRequestDto.getFirst_name() != null && !extendedUserProfileRequestDto.getFirst_name().isBlank()) {
            user.setFirst_name(extendedUserProfileRequestDto.getFirst_name());
        }

        if (extendedUserProfileRequestDto.getLast_name() != null && !extendedUserProfileRequestDto.getLast_name().isBlank()) {
            user.setLast_name(extendedUserProfileRequestDto.getLast_name());
        }

        if (extendedUserProfileRequestDto.getEmail() != null && !extendedUserProfileRequestDto.getEmail().isBlank()) {
            user.setEmail(extendedUserProfileRequestDto.getEmail());
        }

        if (extendedUserProfileRequestDto.getPassword() != null && !extendedUserProfileRequestDto.getPassword().isBlank()) {
            user.setPassword(extendedUserProfileRequestDto.getPassword());
        }

        userRepository.save(user);

        return new ExtendedUserProfileResponseDto(
                user.getFirst_name(),
                user.getLast_name(),
                user.getEmail(),
                user.getPassword(),
                user.getMemberNumber(),
                List.of()
        );
    }
}
