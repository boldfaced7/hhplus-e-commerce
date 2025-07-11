package kr.hhplus.be.server.adapter.out.cache.product;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ProductRankingKeys {

    private static final String KEY_PREFIX = "product:order:ranking";
    private static final String KEY_SEPARATOR = ":";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static final Duration TTL = Duration.ofDays(1L);

    private ProductRankingKeys() {
    }

    public static String keyForNow() {
        return keyOf(LocalDateTime.now());
    }

    public static String keyOf(LocalDateTime dateTime) {
        return KEY_PREFIX + KEY_SEPARATOR + dateTime.format(DATE_FORMATTER);
    }
}