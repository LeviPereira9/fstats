package lp.edu.fstats.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

public class UserRepositoryTest extends RepositoryTestBase {

    @Autowired
    private UserRepository userRepository;

    //helpers

    private User buildUser(String username, String email, boolean deleted){
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("senha123");
        user.setRole(Role.USER);
        user.setDeleted(deleted);
        user.setVerified(false);
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));

        return user;
    }

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    // findByUsernameOrEmail
    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenSearchingByUsername(){
        userRepository.save(this.buildUser(
                "joao",
                "joao@email.com",
                false));

        Optional<User> result = userRepository.findByUsernameOrEmail("joao");

        assertTrue(result.isPresent());
        assertEquals("joao@email.com", result.get().getEmail());
    }

    @Test
    void findByUsernameOrEmail_shouldReturnEmpty_whenUserDoesNotExist(){
        Optional<User> result = userRepository.findByUsernameOrEmail("ninguem");

        assertFalse(result.isPresent());
    }

    //findUsersBySearch
    @Test
    void findUsersBySearch_shouldReturnMatchingUsers_whenSearchTermMatches(){
        userRepository.save(this.buildUser(
                "joaosilva",
                "joao@email.com",
                false
        ));

        userRepository.save(this.buildUser(
                "joaosouza",
                "souza@email.com",
                false
        ));

        userRepository.save(this.buildUser(
                "pedro",
                "pedro@email.com",
                false
        ));

        Page<User> result = userRepository.findUsersBySearch("joao", PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(u -> u.getUsername().contains("joao")));
    }

    @Test
    void findUsersBySearch_shouldNotReturnDeletedUsers_whenUserIsDeleted(){
        userRepository.save(this.buildUser(
                "joao",
                "joao@email.com",
                false
        ));

        userRepository.save(this.buildUser(
                "joaodeletado",
                "deletado@email.com",
                true
        ));

        Page<User> result = userRepository.findUsersBySearch("joao", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("joao", result.getContent().get(0).getUsername());
    }

    @Test
    void findUsersBySearch_shouldReturnEmpty_whenNoUserMatches(){
        userRepository.save(this.buildUser(
                "pedro",
                "pedro@email.com",
                false
        ));

        Page<User> result = userRepository.findUsersBySearch(
                "joao",
                PageRequest.of(0, 10)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findUsersBySearch_shouldRespectPagination_whenResultsExceedPageSize(){
        for(int i = 1; i <= 15; i++){
            userRepository.save(this.buildUser(
                    "joao" + i,
                    "joao" + i + "@email.com",
                    false
            ));
        }

        Page<User> firstPage = userRepository.findUsersBySearch(
                "joao",
                PageRequest.of(0, 10)
        );

        Page<User> secondPage = userRepository.findUsersBySearch(
                "joao",
                PageRequest.of(1, 10)
        );

        assertEquals(15, firstPage.getTotalElements());
        assertEquals(10, firstPage.getContent().size());
        assertEquals(5, secondPage.getContent().size());

        assertTrue(firstPage.hasNext());
        assertFalse(secondPage.hasNext());
    }
}
