package com.healthstore.service;

import com.healthstore.dto.UserRoleUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.Role;
import com.healthstore.model.User;
import com.healthstore.repository.RoleRepository;
import com.healthstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CartService cartService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test data
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");
        
        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setMobile("1234567890");
        testUser.getRoles().add(userRole);
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setLastName("Name");
        updateDTO.setMobile("0987654321");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.updateUser(1L, updateDTO);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updateDTO.getFirstName(), updatedUser.getFirstName());
        assertEquals(updateDTO.getLastName(), updatedUser.getLastName());
        assertEquals(updateDTO.getMobile(), updatedUser.getMobile());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUser(999L, updateDTO));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserRole_WithValidRole_ShouldUpdateUserRole() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.updateUserRole(1L, "ADMIN");

        // Assert
        assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getRoles().size());
        assertTrue(updatedUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName())));
        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserRole_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUserRole(999L, "ADMIN"));
        verify(userRepository, times(1)).findById(999L);
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserRole_WithNonExistingRole_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUserRole(1L, "INVALID_ROLE"));
        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByName("INVALID_ROLE");
        verify(userRepository, never()).save(any(User.class));
    }
}
