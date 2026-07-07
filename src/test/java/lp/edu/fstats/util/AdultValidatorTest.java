package lp.edu.fstats.util;

import lp.edu.fstats.util.snippets.validator.Adult;
import lp.edu.fstats.util.snippets.validator.AdultValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdultValidatorTest {

    private AdultValidator validator;

    @BeforeEach
    void setUp(){
        validator = new AdultValidator();

        //simula o @Adult com age = 18 (valor padrão)
        Adult annotation = mock(Adult.class);
        when(annotation.age())
                .thenReturn(18);

        validator.initialize(annotation);
    }

    // isValid
    @Test
    void isValid_shouldReturnTrue_whenUserIsExactly18(){
        LocalDate dateOfBirth = LocalDate.now().minusYears(18);

        assertTrue(validator.isValid(dateOfBirth, null));
    }

    @Test
    void isValid_shouldReturnFalse_whenUserIsYoungerThan18(){
        LocalDate dateOfBirth = LocalDate.now().minusYears(17);

        assertFalse(validator.isValid(dateOfBirth, null));
    }

    @Test
    void isValid_shouldReturnFalse_whenUserIsAlmostExactly18(){
        LocalDate dateOfBirth = LocalDate.now().minusYears(18).plusDays(1);

        assertFalse(validator.isValid(dateOfBirth, null));
    }

    @Test
    void isValid_shouldReturnFalse_whenDateOfBirthIsNull(){
        assertFalse(validator.isValid(null, null));
    }

    // initialize - age customizado
    @Test
    void isValid_shouldRespectCustomAge_whenAnnotationDefinesMinAge21(){

        Adult customAnnotation = mock(Adult.class);

        when(customAnnotation.age()).thenReturn(21);

        validator.initialize(customAnnotation);

        // 20 anos - válido para 18, inválido para 21
        LocalDate dateOfBirth = LocalDate.now().minusYears(20);

        assertFalse(validator.isValid(dateOfBirth, null));

    }

}
