package edu.tamu.app.model.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { WebServerInit.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class UserTest {

    @Autowired
    private UserRepo userRepo;

    private static final Credentials TEST_CREDENTIALS = new Credentials();
    static {
        TEST_CREDENTIALS.setUin("123456789");
        TEST_CREDENTIALS.setEmail("aggieJack@tamu.edu");
        TEST_CREDENTIALS.setFirstName("Aggie");
        TEST_CREDENTIALS.setLastName("Jack");
        TEST_CREDENTIALS.setRole("ROLE_USER");
    }

    @BeforeEach
    public void setUp() {
        userRepo.deleteAll();
    }

    @Test
    public void testMethod() {

        // Test create user
        User testUser1 = userRepo.create(new User(TEST_CREDENTIALS));
        Optional<User> assertUser = userRepo.findByUsername("123456789");

        assertEquals("Test User1 was not added.", testUser1.getUsername(), assertUser.get().getUsername());

        // Test disallow duplicate UINs
        userRepo.create(new User(TEST_CREDENTIALS));
        List<User> allUsers = (List<User>) userRepo.findAll();
        assertEquals("Duplicate UIN found.", 1, allUsers.size());

        // Test delete user
        userRepo.delete(testUser1);
        allUsers = (List<User>) userRepo.findAll();
        assertEquals("Test User1 was not removed.", 0, allUsers.size());

    }

    @Test
    public void testGetAuthorities() {
        User testUser1 = userRepo.create(new User(TEST_CREDENTIALS));
        Collection<? extends GrantedAuthority> authorities = testUser1.getAuthorities();
        assertNotNull(authorities);
    }

    @Test
    public void testStaticUtilityMethods() {
        User testUser1 = userRepo.create(new User(TEST_CREDENTIALS));
        assertEquals("Value was not false", false, testUser1.isAccountNonExpired());
        assertEquals("Value was not false", false, testUser1.isAccountNonLocked());
        assertEquals("Value was not false", false, testUser1.isCredentialsNonExpired());
        assertEquals("Value was not true", true, testUser1.isEnabled());
        assertEquals("Value was not null", null, testUser1.getPassword());
    }

    @AfterEach
    public void cleanUp() {
        userRepo.deleteAll();
    }

}