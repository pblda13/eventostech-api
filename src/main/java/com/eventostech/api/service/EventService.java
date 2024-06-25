package com.eventostech.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.eventostech.api.domain.coupon.Coupon;
import com.eventostech.api.domain.event.Event;
import com.eventostech.api.domain.event.EventDetailsDTO;
import com.eventostech.api.domain.event.EventResponseDTO;
import com.eventostech.api.domain.event.EventsRequestDTO;
import com.eventostech.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private AmazonS3 S3Client; // Cliente para interação com o serviço Amazon S3
    @Autowired
    private CouponService couponService; // Serviço para operações relacionadas a cupons
    @Autowired
    private AddressService addressService; // Serviço para operações relacionadas a endereços

    @Autowired
    private EventRepository repository; // Repositório para operações de persistência de eventos

    @Value("${aws.bucket.name}")
    private String bucketName; // Nome do bucket no Amazon S3

    /**
     * Cria um novo evento com base nos dados fornecidos.
     * Faz o upload de uma imagem para o Amazon S3, se fornecida.
     * Cria um endereço associado se o evento não for remoto.
     *
     * @param data Dados do evento a serem utilizados para criar o evento.
     * @return O evento recém-criado.
     */
    public Event createEvent(EventsRequestDTO data){
        String imgUrl = null;

        if(data.image() != null){
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        repository.save(newEvent);

        if(!data.remote()){
            this.addressService.createAddress(data,newEvent);
        }


        return newEvent;
    }


    /**
     * Faz o upload de um arquivo (imagem) para o Amazon S3.
     *
     * @param multipartFile Arquivo a ser enviado para o Amazon S3.
     * @return URL pública do arquivo após o upload.
     */
    private String uploadImg(MultipartFile multipartFile){
        String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try{
            File file = this.convertMultipartToFile(multipartFile);
            S3Client.putObject(bucketName, filename, file);
            file.delete();
            return S3Client.getUrl(bucketName, filename).toString();
        } catch (Exception e){
            System.out.println("Erro ao subir arquivo");
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Obtém uma lista paginada de eventos futuros.
     *
     * @param page Número da página requisitada.
     * @param size Tamanho da página.
     * @return Lista de eventos futuros no formato DTO.
     */
    public List<EventResponseDTO> getUpcomingEvents(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl())
                )
                .toList();
    }

    /**
     * Obtém uma lista paginada de eventos filtrados por cidade, estado, data de início e data de fim.
     *
     * @param page Número da página requisitada.
     * @param size Tamanho da página.
     * @param city Cidade para filtro (pode ser vazio para não filtrar por cidade).
     * @param uf Estado para filtro (pode ser vazio para não filtrar por estado).
     * @param startDate Data mínima de início para filtro.
     * @param endDate Data máxima de fim para filtro.
     * @return Lista de eventos filtrados no formato DTO.
     */
    public List<EventResponseDTO> getFilteredEvents(int page, int size, String city, String uf, Date startDate, Date endDate){
        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = this.repository.findFilteredEvents(city, uf, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl())
                )
                .toList();
    }

    /**
     * Converte um MultipartFile em um File.
     *
     * @param multipartFile Arquivo multipart a ser convertido.
     * @return Arquivo convertido.
     * @throws IOException Se houver erro durante a conversão.
     */
    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

    /**
     * Obtém os detalhes de um evento específico.
     *
     * @param eventId ID do evento a ser consultado.
     * @return Detalhes do evento no formato DTO.
     * @throws IllegalArgumentException Se o evento não for encontrado.
     */
    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        List<EventDetailsDTO.CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> new EventDetailsDTO.CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .collect(Collectors.toList());

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getImgUrl(),
                event.getEventUrl(),
                couponDTOs);
    }

    /**
     * Busca eventos por título.
     *
     * @param title Título do evento para busca.
     * @return Lista de eventos encontrados no formato DTO.
     */
    public List<EventResponseDTO> searchEvents(String title){
        title = (title != null) ? title : "";

        List<Event> eventsList = this.repository.findEventsByTitle(title);
        return eventsList.stream().map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl())
                )
                .toList();
    }
}
