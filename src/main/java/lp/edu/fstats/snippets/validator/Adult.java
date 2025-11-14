package lp.edu.fstats.snippets.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AdultValidator.class})
public @interface Adult {
    String message() default "O usuário deve ter pelo menos 18 anos de idade.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int age() default 18;
}