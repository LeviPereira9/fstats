package lp.edu.fstats.snippets.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {
    private int minAge;

    @Override
    public void initialize(Adult constraintAnnotation) {
        this.minAge = constraintAnnotation.age();
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if(dateOfBirth == null){
            return false;
        }

        LocalDate today = LocalDate.now();
        Period age = Period.between(dateOfBirth, today);

        return age.getYears() >= minAge;
    }
}
