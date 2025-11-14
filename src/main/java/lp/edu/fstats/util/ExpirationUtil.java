package lp.edu.fstats.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ExpirationUtil {

    private static final int JWT_DAYS = 31;
    private static final int VERIFICATION_MINUTES = 15;

    private ExpirationUtil(){}

    public static Instant defaultJwtExpirationTime(){

        return BrazilTimeUtil.nowDateTime().plusDays(JWT_DAYS)
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toInstant();
    }

    public static LocalDateTime defaultVerificationTime(){
        return BrazilTimeUtil.nowDateTime().plusMinutes(VERIFICATION_MINUTES);
    }
}
