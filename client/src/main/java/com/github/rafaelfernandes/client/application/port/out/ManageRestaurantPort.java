package com.github.rafaelfernandes.client.application.port.out;

import com.github.rafaelfernandes.common.enums.Cuisine;
import com.github.rafaelfernandes.client.application.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ManageRestaurantPort {

    Boolean existsName(String name);

    Client save(Client restaurant);

    Optional<Client> findById(Client.RestaurantId id);

    List<Client> findAllBy(String name, String location, List<Cuisine> cuisines);
}
