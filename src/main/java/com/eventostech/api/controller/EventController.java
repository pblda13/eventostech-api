package com.eventostech.api.controller;

import com.eventostech.api.domain.event.Event;
import com.eventostech.api.domain.event.EventDetailsDTO;
import com.eventostech.api.domain.event.EventResponseDTO;
import com.eventostech.api.domain.event.EventsRequestDTO;
import com.eventostech.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService; // Serviço responsável por operações relacionadas a eventos

    /**
     * Endpoint para criar um novo evento.
     *
     * @param title       Título do evento.
     * @param description Descrição do evento (opcional).
     * @param date        Data do evento em formato Unix timestamp.
     * @param city        Cidade onde ocorrerá o evento.
     * @param state       Estado onde ocorrerá o evento.
     * @param remote      Indica se o evento é remoto ou não.
     * @param eventUrl    URL relacionada ao evento.
     * @param image       Imagem associada ao evento (opcional).
     * @return ResponseEntity com o novo evento criado.
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> create(@RequestParam("title") String title,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam("date") Long date,
                                        @RequestParam("city") String city,
                                        @RequestParam("state") String state,
                                        @RequestParam("remote") Boolean remote,
                                        @RequestParam("eventUrl") String eventUrl,
                                        @RequestParam(value = "image", required = false) MultipartFile image) {
        EventsRequestDTO eventRequestDTO = new EventsRequestDTO(title, description, date, city, state, remote, eventUrl, image);
        Event newEvent = this.eventService.createEvent(eventRequestDTO);
        return ResponseEntity.ok(newEvent);
    }

    /**
     * Endpoint para obter todos os eventos paginados.
     *
     * @param page Número da página solicitada (padrão é 0).
     * @param size Tamanho da página (padrão é 10).
     * @return ResponseEntity com a lista de eventos encontrados.
     */
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getEvents(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        List<EventResponseDTO> allEvents = this.eventService.getUpcomingEvents(page, size);
        return ResponseEntity.ok(allEvents);
    }

    /**
     * Endpoint para obter eventos filtrados por cidade, estado, data de início e data de fim.
     *
     * @param page      Número da página solicitada (padrão é 0).
     * @param size      Tamanho da página (padrão é 10).
     * @param city      Cidade para filtro.
     * @param uf        Estado para filtro.
     * @param startDate Data de início para filtro (formato ISO Date).
     * @param endDate   Data de fim para filtro (formato ISO Date).
     * @return ResponseEntity com a lista de eventos filtrados.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<EventResponseDTO>> getFilteredEvents(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam String city,
                                                                    @RequestParam String uf,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<EventResponseDTO> events = eventService.getFilteredEvents(page, size, city, uf, startDate, endDate);
        return ResponseEntity.ok(events);
    }

    /**
     * Endpoint para buscar eventos por título.
     *
     * @param title Título do evento para busca.
     * @return ResponseEntity com a lista de eventos encontrados.
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDTO>> getSearchEvents(@RequestParam String title) {
        List<EventResponseDTO> events = eventService.searchEvents(title);
        return ResponseEntity.ok(events);
    }
}
