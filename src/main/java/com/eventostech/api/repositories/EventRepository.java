package com.eventostech.api.repositories;

import com.eventostech.api.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Busca eventos futuros a partir da data atual paginados.
     *
     * @param currentDate Data atual a partir da qual os eventos são considerados futuros.
     * @param pageable    Objeto de paginação para controlar a paginação dos resultados.
     * @return Página de eventos futuros com endereço carregado de forma antecipada (fetch join).
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.address a WHERE e.date >= :currentDate")
    public Page<Event> findUpcomingEvents(@Param("currentDate") Date currentDate, Pageable pageable);

    /**
     * Busca eventos filtrados por cidade, estado, data de início e data de fim.
     *
     * @param city      Cidade para filtrar os eventos (pode ser vazio para não aplicar o filtro de cidade).
     * @param uf        Estado para filtrar os eventos (pode ser vazio para não aplicar o filtro de estado).
     * @param startDate Data mínima de início para filtrar os eventos.
     * @param endDate   Data máxima de fim para filtrar os eventos.
     * @param pageable  Objeto de paginação para controlar a paginação dos resultados.
     * @return Página de eventos filtrados com endereço carregado de forma antecipada (fetch join).
     */
    @Query("SELECT e FROM Event e LEFT JOIN e.address a " +
            "WHERE (:city = '' OR a.city LIKE %:city%) " +
            "AND (:uf = '' OR a.uf LIKE %:uf%) " +
            "AND (e.date >= :startDate AND e.date <= :endDate)")
    Page<Event> findFilteredEvents(@Param("city") String city,
                                   @Param("uf") String uf,
                                   @Param("startDate") Date startDate,
                                   @Param("endDate") Date endDate,
                                   Pageable pageable);

    /**
     * Busca eventos por título.
     *
     * @param title Título do evento para buscar (pode ser vazio para buscar todos os eventos).
     * @return Lista de eventos encontrados que correspondem ao título fornecido.
     */
    @Query("SELECT e FROM Event e LEFT JOIN e.address a " +
            "WHERE (:title = '' OR e.title LIKE %:title%) ")
    List<Event> findEventsByTitle(@Param("title") String title);
}
