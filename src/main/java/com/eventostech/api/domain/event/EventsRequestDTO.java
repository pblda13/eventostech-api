package com.eventostech.api.domain.event;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public record EventsRequestDTO(String title, String description, Long date, String city, String state, Boolean remote, String eventUrl, MultipartFile image) {
}
