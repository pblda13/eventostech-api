package com.eventostech.api.service;

import com.eventostech.api.domain.coupon.Coupon;
import com.eventostech.api.domain.coupon.CouponRequestDTO;
import com.eventostech.api.domain.event.Event;
import com.eventostech.api.repositories.CouponRepository;
import com.eventostech.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private EventRepository eventRepository; // Repositório para operações de eventos

    @Autowired
    private CouponRepository couponRepository; // Repositório para operações de cupons

    /**
     * Adiciona um cupom a um evento específico.
     *
     * @param eventId    ID do evento ao qual o cupom será adicionado.
     * @param couponData Dados do cupom a serem adicionados.
     * @return O cupom adicionado.
     * @throws IllegalArgumentException Se o evento não for encontrado.
     */
    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponData) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        Coupon coupon = new Coupon();
        coupon.setCode(couponData.code());
        coupon.setDiscount(couponData.discount());
        coupon.setValid(new Date(couponData.valid()));
        coupon.setEvent(event);

        return couponRepository.save(coupon);
    }

    /**
     * Consulta cupons válidos para um evento específico e uma data atual.
     *
     * @param eventId     ID do evento para o qual os cupons serão consultados.
     * @param currentDate Data atual para verificar a validade dos cupons.
     * @return Lista de cupons válidos para o evento e data fornecidos.
     */
    public List<Coupon> consultCoupons(UUID eventId, Date currentDate) {
        return couponRepository.findByEventIdAndValidAfter(eventId, currentDate);
    }
}

