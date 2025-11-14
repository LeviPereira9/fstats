package lp.edu.fstats.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;

public class TokenGeneratorUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DIGITS = "0123456789";
    private static final int EMAIL_TOKEN_LENGTH = 6;
    private static final int JWT_VERSION_TOKEN_LENGTH = 12; // 12b = 16c

    public static String generateJwtVersion(){
        byte[] bytes = new byte[JWT_VERSION_TOKEN_LENGTH];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateVerificationToken(){
        return RANDOM.ints(EMAIL_TOKEN_LENGTH, 0, DIGITS.length())
                .mapToObj(DIGITS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
