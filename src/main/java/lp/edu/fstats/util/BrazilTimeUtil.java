package lp.edu.fstats.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class BrazilTimeUtil {
    public static LocalDateTime nowDateTime(){
        return LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    public static LocalDate nowDate(){
        return LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    }
}
