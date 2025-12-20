package lp.edu.fstats.integration.dto.email;

import lombok.Builder;
import lombok.Data;

@Data
public class Email {
    private String name;
    private String email;

    public Email(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
