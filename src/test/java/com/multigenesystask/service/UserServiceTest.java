package com.multigenesystask.service;

import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImplementation userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFirstName("Pratik");
    }

    @Test
    void registerUser_savesAndReturnsUser() throws UserException {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(testUser);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void findUserByEmail_exists_returnsUser() throws UserException {
        when(userRepository.findByEmail("test@test.com")).thenReturn(testUser);

        User result = userService.findUserByEmail("test@test.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findUserByEmail_notFound_throwsUserException() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(null);

        assertThrows(UserException.class, () -> userService.findUserByEmail("missing@test.com"));
    }

    @Test
    void findUserById_exists_returnsUser() throws UserException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findUserById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findUserById_notFound_throwsUserException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.findUserById(99L));
    }
}
