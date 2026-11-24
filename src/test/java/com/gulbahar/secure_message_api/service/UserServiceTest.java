package com.gulbahar.secure_message_api.service;

import com.gulbahar.secure_message_api.dto.LoginRequest;
import com.gulbahar.secure_message_api.dto.RegisterRequest;
import com.gulbahar.secure_message_api.exception.ConflictException;
import com.gulbahar.secure_message_api.exception.InvalidCredentialsException;
import com.gulbahar.secure_message_api.model.User;
import com.gulbahar.secure_message_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesUserWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("bahar");
        request.setPassword("123456");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashed-password");

        userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("bahar", captor.getValue().getUsername());
        assertEquals("hashed-password", captor.getValue().getPassword());
    }

    @Test
    void register_throwsConflict_whenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("bahar");
        request.setPassword("123456");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("bahar");
        request.setPassword("123456");

        User user = new User();
        user.setUsername("bahar");
        user.setPassword("hashed-password");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("bahar")).thenReturn("fake-token");

        String token = userService.login(request);

        assertEquals("fake-token", token);
    }

    @Test
    void login_throwsInvalidCredentials_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("yok");
        request.setPassword("123456");

        when(userRepository.findByUsername("yok")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void login_throwsInvalidCredentials_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest();
        request.setUsername("bahar");
        request.setPassword("yanlis");

        User user = new User();
        user.setUsername("bahar");
        user.setPassword("hashed-password");

        when(userRepository.findByUsername("bahar")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("yanlis", "hashed-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
        verify(jwtService, never()).generateToken(any());
    }
}