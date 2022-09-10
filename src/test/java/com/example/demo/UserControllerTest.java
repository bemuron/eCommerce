package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private UserController userController = new UserController(userRepository,
            cartRepository, bCryptPasswordEncoder);

    private static final String ENCODED_PASSWORD = "myEncodedPassword";

    // helper
    private CreateUserRequest createAUserRequest(String username, String password, String confirmedPassword){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(confirmedPassword);
        return createUserRequest;
    }
    // test create user failure
    @Test
    public void createUserFailure(){
        ResponseEntity<User> responseEntity = userController.createUser(
                createAUserRequest("testUser",
                        "password",
                        "password1")
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    // test create user success
    @Test
    public void createUserSuccess(){
        when(bCryptPasswordEncoder.encode("password")).thenReturn("passwordHash");

        ResponseEntity<User> responseEntity = userController.createUser(
                createAUserRequest("testUser", "password", "password")
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        User newUser = responseEntity.getBody();
        assertNotNull(newUser);
        assertEquals("testUser", newUser.getUsername());
        assertEquals("passwordHash", newUser.getPassword());
    }

    @Test
    public void findByUsernameSuccess(){
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        ResponseEntity<User> responseEntity = userController.findByUserName(user.getUsername());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        User userFound = responseEntity.getBody();
        assertNotNull(userFound);
        assertEquals("testUser", userFound.getUsername());
    }

}
