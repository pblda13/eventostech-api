package com.eventostech.api.service;

import com.eventostech.api.domain.address.Address;
import com.eventostech.api.domain.event.Event;
import com.eventostech.api.domain.event.EventsRequestDTO;
import com.eventostech.api.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository; // Repositório para operações de endereço

    /**
     * Cria um novo endereço com base nos dados do evento fornecidos.
     *
     * @param data  Dados do evento contendo informações de cidade e estado.
     * @param event Evento ao qual o endereço está associado.
     * @return O endereço criado e salvo no banco de dados.
     */
    public Address createAddress(EventsRequestDTO data, Event event) {
        Address address = new Address();
        address.setCity(data.city());
        address.setUf(data.state());
        address.setEvent(event);

        return addressRepository.save(address);
    }
}
