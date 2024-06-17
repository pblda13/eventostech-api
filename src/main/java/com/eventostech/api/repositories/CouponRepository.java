package com.eventostech.api.repositories;

import com.eventostech.api.domain.coupon.Coupon;
import com.eventostech.api.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
}
