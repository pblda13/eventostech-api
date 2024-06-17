package com.eventostech.api.repositories;

import com.eventostech.api.domain.address.Address;
import com.eventostech.api.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
