package com.pover.Library.service;

import com.pover.Library.JWT.JwtUtil;
import com.pover.Library.dto.*;
import com.pover.Library.model.User;
import com.pover.Library.model.enums.Role;
import com.pover.Library.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> authenticateUser(String memberNumber, String password) {
        Optional<User> existingUser = userRepository.findByMemberNumber(memberNumber);

        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            String token = jwtUtil.generateToken(existingUser.get().getUser_id(), existingUser.get().getRole(), null, existingUser.get().getMemberNumber());
            return Optional.of(token);
        }
        return Optional.empty();
    }


    public UserResponseDto getUserById(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("User not found"));
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

        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        System.out.println("Encoded password: " + encodedPassword);
        user.setPassword(encodedPassword);

        user.setRole(Role.USER);

        userRepository.save(user);

        return convertToDto(user);
    }


    // konverterar User till UserResponseDto
    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(user);
    }


    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(user-> new UserResponseDto(user))
                .collect(Collectors.toList());

    }

    // USER PROFILE
    // to check and update info about user
    public BasicUserProfileResponseDto getUserProfile(String token) {
        String memberNumber = jwtUtil.extractMemberNumber(token);

        User user = userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<LoanResponseDto> activeLoans = user.getLoans().stream()
                .filter(loan -> loan.getReturnedDate() == null)
                .map(LoanResponseDto::new)
                .collect(Collectors.toList());

        return new BasicUserProfileResponseDto(user.getFirst_name(), user.getLast_name(), user.getEmail(), activeLoans);
    }

    @Transactional
    public BasicUserProfileResponseDto updateUserProfile(String token, BasicUserProfileRequestDto basicUserProfileRequestDto) {
        String memberNumber = jwtUtil.extractMemberNumber(token);

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

        List<LoanResponseDto> activeLoans = user.getLoans().stream()
                .filter(loan -> loan.getReturnedDate() == null)
                .map(LoanResponseDto::new)
                .collect(Collectors.toList());

        return new BasicUserProfileResponseDto(user.getFirst_name(), user.getLast_name(), user.getEmail(), activeLoans);
    }

    // USER PROFILE CHECK AND UPDATE BY ADMIN
    // can be accessed only by admin
    public ExtendedUserProfileResponseDto getUserProfileByMemberNumber(String memberNumber) {
        User user = userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<LoanResponseDto> activeLoans = user.getLoans().stream()
                .filter(loan -> loan.getReturnedDate() == null)
                .map(LoanResponseDto::new)
                .collect(Collectors.toList());

        return new ExtendedUserProfileResponseDto(user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getPassword(), user.getMemberNumber(), activeLoans);
    }

    @Transactional
    public ExtendedUserProfileResponseDto updateUserProfileByMemberNumber(ExtendedUserProfileRequestDto extendedUserProfileRequestDto) {


        String memberNumber = extendedUserProfileRequestDto.getMember_number();

        User user = userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!memberNumber.equals(user.getMemberNumber())) {
            throw new IllegalArgumentException("Member number cannot be changed.");
        }


        if (extendedUserProfileRequestDto.getFirst_name() != null && !extendedUserProfileRequestDto.getFirst_name().isBlank()) {
            user.setFirst_name(extendedUserProfileRequestDto.getFirst_name());
        } else if (extendedUserProfileRequestDto.getFirst_name() != null) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (extendedUserProfileRequestDto.getLast_name() != null && !extendedUserProfileRequestDto.getLast_name().isBlank()) {
            user.setLast_name(extendedUserProfileRequestDto.getLast_name());
        } else if (extendedUserProfileRequestDto.getLast_name() != null) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }

        if (extendedUserProfileRequestDto.getEmail() != null && !extendedUserProfileRequestDto.getEmail().isBlank()) {
            user.setEmail(extendedUserProfileRequestDto.getEmail());
        } else if (extendedUserProfileRequestDto.getEmail() != null) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        if (extendedUserProfileRequestDto.getPassword() != null && !extendedUserProfileRequestDto.getPassword().isBlank()) {
            if (!extendedUserProfileRequestDto.getPassword().matches("^\\d{4}$")) {
                throw new IllegalArgumentException("Password must be 4 digits");
            }
            String hashedPassword = passwordEncoder.encode(extendedUserProfileRequestDto.getPassword());
            user.setPassword(hashedPassword);
        }


        userRepository.save(user);

        List<LoanResponseDto> activeLoans = user.getLoans().stream()
                .filter(loan -> loan.getReturnedDate() == null)
                .map(LoanResponseDto::new)
                .collect(Collectors.toList());

        return new ExtendedUserProfileResponseDto(user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getPassword(), user.getMemberNumber(), activeLoans);
    }

    // LOGOUT
    public boolean logout(String token) {
        return token != null && !token.isEmpty();
    }
}
