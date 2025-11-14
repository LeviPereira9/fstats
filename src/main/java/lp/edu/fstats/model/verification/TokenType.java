package lp.edu.fstats.model.verification;

import lombok.Getter;

@Getter
public enum TokenType {

    CONFIRMATION("CONFIRMATION"),
    PASSWORD("PASSWORD"),
    EMAIL_CHANGE("EMAIL_CHANGE");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

}
