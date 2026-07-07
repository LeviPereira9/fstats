package lp.edu.fstats.service;

import lp.edu.fstats.dto.favorites.FavoriteAddRequest;
import lp.edu.fstats.dto.favorites.FavoriteRemoveRequest;
import lp.edu.fstats.dto.favorites.FavoritesResponse;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.factory.entity.CodeTestFactory;
import lp.edu.fstats.factory.entity.UserTestFactory;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.favorites.Favorite;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.code.CodeRepository;
import lp.edu.fstats.repository.favorites.FavoriteRepository;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.service.favorites.FavoriteServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FavoriteServiceImplTest {

   @Mock
    private FavoriteRepository favoriteRepository;

   @Mock
    private UserRepository userRepository;

   @Mock
    CodeRepository codeRepository;

   @InjectMocks
    private FavoriteServiceImpl favoriteService;

   //helpers
   @AfterEach
    void clearSecurityContext() {
       SecurityContextHolder.clearContext();
   }

   private Favorite buildFavorite(Long id, Code code, User user){
       Favorite favorite = new Favorite();
       favorite.setId(id);
       favorite.setCompetition(code);
       favorite.setUser(user);

       return favorite;
   }

   // getAllFavorites

    @Test
    void getAllFavorites_shouldReturnFavoritesResponse_whenSelfRequest(){
       User user = UserTestFactory.buildUser("joao");
       UserTestFactory.mockAuthenticatedUser(user);

       Code code = CodeTestFactory.buildCode(1, "PL", "Premier League");

       List<Favorite> favorites = List.of(this.buildFavorite(1L, code, user));

       when(favoriteRepository.findAllByUser_Username("joao")).thenReturn(favorites);

        FavoritesResponse response = favoriteService.getAllFavorites("joao");

        assertNotNull(response);
        assertEquals(1, response.favorites().size());
        assertEquals("PL", response.favorites().get(0).competitionCode());

        verify(favoriteRepository).findAllByUser_Username("joao");
    }

    @Test
    void getAllFavorites_shouldThrowForbidden_whenNotSelfRequest(){
       User other = UserTestFactory.buildUser("outro");
       UserTestFactory.mockAuthenticatedUser(other);

       assertThrows(CustomForbiddenActionException.class,
               ()-> favoriteService.getAllFavorites("joao"));

       verifyNoInteractions(favoriteRepository);
    }

    // addFavorite
    @Test
    void addFavorite_shouldSaveFavorite_whenRequestIsValid(){
       User user = UserTestFactory.buildUser("joao");
       UserTestFactory.mockAuthenticatedUser(user);

       Code code = CodeTestFactory.buildCode(1, "PL", "Premier League");
        FavoriteAddRequest request = new FavoriteAddRequest(1);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        when(codeRepository.findById(1)).thenReturn(Optional.of(code));

        when(favoriteRepository.existsByCompetition_IdAndUser_Username(1, "joao")).thenReturn(false);

        favoriteService.addFavorite("joao", request);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addFavorite_shouldThrowForbidden_whenNotSelfRequest(){
       User other = UserTestFactory.buildUser("outro");
       UserTestFactory.mockAuthenticatedUser(other);

       FavoriteAddRequest request = new FavoriteAddRequest(1);

       assertThrows(CustomForbiddenActionException.class,
               ()-> favoriteService.addFavorite("joao", request));

       verifyNoInteractions(userRepository);
       verifyNoInteractions(codeRepository);
       verifyNoInteractions(favoriteRepository);

    }

    @Test
    void addFavorite_shouldThrowNotFound_whenUserDoesNotExists(){
       User user = UserTestFactory.buildUser("joao");
       UserTestFactory.mockAuthenticatedUser(user);

       FavoriteAddRequest request = new FavoriteAddRequest(1);

       when(userRepository.findByUsername("joao")).thenReturn(Optional.empty());

       assertThrows(CustomNotFoundException.class,
               ()-> favoriteService.addFavorite("joao", request));

       verifyNoInteractions(codeRepository);
       verify(favoriteRepository, never()).save((any()));
    }

    @Test
    void addFavorite_shouldThrowNotFound_whenCompetitionDoesNotExist(){
       User user = UserTestFactory.buildUser("joao");
       UserTestFactory.mockAuthenticatedUser(user);

        FavoriteAddRequest request = new FavoriteAddRequest(1);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        when(codeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> favoriteService.addFavorite("joao", request));

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_shouldThrowDuplicateField_whenAlreadyFavorited(){
        User user = UserTestFactory.buildUser("joao");
        UserTestFactory.mockAuthenticatedUser(user);

        Code code = CodeTestFactory.buildCode(1, "PL", "Premier League");

        FavoriteAddRequest request = new FavoriteAddRequest(1);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        when(codeRepository.findById(1)).thenReturn(Optional.of(code));

        when(favoriteRepository.existsByCompetition_IdAndUser_Username(1, "joao")).thenReturn(true);

        assertThrows(CustomDuplicateFieldException.class,
                ()-> favoriteService.addFavorite("joao", request));

        verify(favoriteRepository, never()).save(any());
    }

    //removeFavorite
    @Test
    void removeFavorite_shouldDeleteFavorite_whenFavoriteExists(){
       User user = UserTestFactory.buildUser("joao");
       UserTestFactory.mockAuthenticatedUser(user);

        FavoriteRemoveRequest request = new FavoriteRemoveRequest(1L);

        when(favoriteRepository.existsById(1L)).thenReturn(true);

        favoriteService.removeFavorite("joao", request);

        verify(favoriteRepository).deleteById(1L);
    }

    @Test
    void removeFavorite_shouldThrowForbidden_whenNotSelfRequest(){
       User other = UserTestFactory.buildUser("outro");
       UserTestFactory.mockAuthenticatedUser(other);

       FavoriteRemoveRequest request = new FavoriteRemoveRequest(1L);

       assertThrows(CustomForbiddenActionException.class,
               ()-> favoriteService.removeFavorite("joao", request));

       verifyNoInteractions(favoriteRepository);
    }

    @Test
    void removeFavorite_shouldThrowNotFound_whenFavoriteDoesNotExist(){
       User user = UserTestFactory.buildUser("joao");
       UserTestFactory.mockAuthenticatedUser(user);

       FavoriteRemoveRequest request = new FavoriteRemoveRequest(1L);

       when(favoriteRepository.existsById(1L)).thenReturn(false);

       assertThrows(CustomNotFoundException.class,
               ()-> favoriteService.removeFavorite("joao", request));

       verify(favoriteRepository, never()).deleteById(any());
    }

}
