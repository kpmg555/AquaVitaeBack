package com.aquavitae.infrastructure.config;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonbAdapter<LocalDateTime, String> {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public String adaptToJson(LocalDateTime d) {
        return d.format(FMT);
    }

    @Override
    public LocalDateTime adaptFromJson(String s) {
        return LocalDateTime.parse(s, FMT);
    }
}