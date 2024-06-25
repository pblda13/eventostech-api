package com.eventostech.api.controller;

import com.eventostech.api.domain.coupon.Coupon;
import com.eventostech.api.domain.coupon.CouponRequestDTO;
import com.eventostech.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService; // Serviço responsável por operações relacionadas a cupons

    /**
     * Endpoint para adicionar um cupom a um evento específico.
     *
     * @param eventId ID do evento ao qual o cupom será adicionado (no path da URL).
     * @param data    Dados do cupom a serem adicionados (no corpo da requisição).
     * @return ResponseEntity com o cupom adicionado.
     */
    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> addCouponsToEvent(@PathVariable UUID eventId, @RequestBody CouponRequestDTO data) {
        Coupon coupon = couponService.addCouponToEvent(eventId, data);
        return ResponseEntity.ok(coupon);
    }
}
