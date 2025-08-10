package com.healthstore.controller.admin;

import com.healthstore.dto.UserRoleUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.Role;
import com.healthstore.model.User;
import com.healthstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

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
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.getRoles().add(userRole);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userService.findAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = adminController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser.getEmail(), response.getBody().get(0).getEmail());
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setLastName("Name");
        updateDTO.setMobile("1234567890");

        User updatedUser = new User();
        updatedUser.setId(testUser.getId());
        updatedUser.setFirstName(updateDTO.getFirstName());
        updatedUser.setLastName(updateDTO.getLastName());
        updatedUser.setMobile(updateDTO.getMobile());
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.getRoles().addAll(testUser.getRoles());

        when(userService.updateUser(anyLong(), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = adminController.updateUser(1L, updateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        User responseUser = (User) response.getBody();
        assertEquals(updateDTO.getFirstName(), responseUser.getFirstName());
        assertEquals(updateDTO.getLastName(), responseUser.getLastName());
    }

    @Test
    void updateUser_WithNonExistingUser_ShouldReturnNotFound() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        when(userService.updateUser(anyLong(), any(UserUpdateDTO.class)))
                .thenThrow(new RuntimeException("User not found with ID: 999"));

        // Act
        ResponseEntity<?> response = adminController.updateUser(999L, updateDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: 999", response.getBody());
    }

    @Test
    void updateUserRole_WithValidRole_ShouldUpdateUserRole() {
        // Arrange
        UserRoleUpdateDTO roleUpdateDTO = new UserRoleUpdateDTO();
        roleUpdateDTO.setRoleName("ADMIN");
        
        User updatedUser = new User();
        updatedUser.setId(testUser.getId());
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.getRoles().clear();
        updatedUser.getRoles().add(adminRole);

        when(userService.updateUserRole(anyLong(), anyString())).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = adminController.updateUserRole(1L, roleUpdateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        User responseUser = (User) response.getBody();
        assertEquals(1, responseUser.getRoles().size());
        assertTrue(responseUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName())));
    }

    @Test
    void updateUserRole_WithNonExistingUser_ShouldReturnNotFound() {
        // Arrange
        UserRoleUpdateDTO roleUpdateDTO = new UserRoleUpdateDTO();
        roleUpdateDTO.setRoleName("ADMIN");
        
        when(userService.updateUserRole(anyLong(), anyString()))
                .thenThrow(new RuntimeException("User not found with ID: 999"));

        // Act
        ResponseEntity<?> response = adminController.updateUserRole(999L, roleUpdateDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: 999", response.getBody());
    }
}
