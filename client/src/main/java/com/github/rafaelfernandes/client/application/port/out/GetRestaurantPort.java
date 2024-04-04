package com.github.rafaelfernandes.client.application.port.out;

import com.github.rafaelfernandes.client.application.domain.model.Client;
import com.github.rafaelfernandes.common.enums.Cuisine;

import java.util.List;

public interface GetRestaurantPort {

    List<Client> findAllBy(String name,
                           String location,
                           List<Cuisine> cuisines);

}
