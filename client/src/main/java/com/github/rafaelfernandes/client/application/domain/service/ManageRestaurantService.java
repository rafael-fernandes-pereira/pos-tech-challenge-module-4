package com.github.rafaelfernandes.client.application.domain.service;

import com.github.rafaelfernandes.common.annotations.UseCase;
import com.github.rafaelfernandes.common.enums.Cuisine;
import com.github.rafaelfernandes.client.exception.RestaurantDuplicateException;
import com.github.rafaelfernandes.client.exception.RestaurantNotFoundException;
import com.github.rafaelfernandes.client.application.domain.model.Client;
import com.github.rafaelfernandes.client.application.port.in.ManageRestaurantUseCase;
import com.github.rafaelfernandes.client.application.port.out.ManageRestaurantPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ManageRestaurantService implements ManageRestaurantUseCase {

    private final ManageRestaurantPort manageRestaurantPort;

    @Override
    @Transactional
    public Client.RestaurantId create(Client restaurant) {

        if (Boolean.TRUE.equals(manageRestaurantPort.existsName(restaurant.getName()))) throw new RestaurantDuplicateException();

        var restaurantNew = manageRestaurantPort.save(restaurant);

        return restaurantNew.getRestaurantId();

    }

    @Override
    public Client findById(Client.RestaurantId restaurantId) {

        return manageRestaurantPort.findById(restaurantId)
                .orElseThrow(RestaurantNotFoundException::new);

    }

    @Override
    public List<Client> findAllBy(String name, String location, List<Cuisine> cuisines) {

        if (ObjectUtils.isEmpty(name) && ObjectUtils.isEmpty(location) && (cuisines == null || cuisines.isEmpty())) {
            throw new IllegalArgumentException("Pelo menos um dos parâmetros deve ser fornecido.");
        }

        var list = manageRestaurantPort.findAllBy(name, location, cuisines);

        if (list.isEmpty()) throw new RestaurantNotFoundException();

        return list;

    }
}
