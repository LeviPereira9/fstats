package lp.edu.fstats.security.jwt.dto;

public record TokenPayload(
        String username,
        String tokenVersion
) {}
