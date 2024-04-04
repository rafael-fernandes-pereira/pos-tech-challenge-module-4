package com.github.rafaelfernandes.client.application.port.in;

import com.github.rafaelfernandes.common.enums.Cuisine;
import com.github.rafaelfernandes.client.application.domain.model.Client;

import java.util.List;

public interface ManageRestaurantUseCase {

    Client.RestaurantId create(Client command);

    Client findById(Client.RestaurantId restaurantId);

    List<Client> findAllBy(String name, String location, List<Cuisine> cuisines);


}
