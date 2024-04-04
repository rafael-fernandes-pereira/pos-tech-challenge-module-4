package com.github.rafaelfernandes.client.adapter.out.persistence;

import com.github.rafaelfernandes.client.application.domain.model.Client;
import com.github.rafaelfernandes.common.annotations.PersistenceAdapter;
import com.github.rafaelfernandes.common.enums.Cuisine;
import com.github.rafaelfernandes.client.exception.RestaurantDuplicateException;
import com.github.rafaelfernandes.client.application.port.out.ManageRestaurantPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class RestaurantPersistenceAdapter implements ManageRestaurantPort {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Transactional
    public Client.RestaurantId create(Client restaurant) throws RestaurantDuplicateException {

        if (restaurantRepository.existsByName(restaurant.getName())) throw new RestaurantDuplicateException();

        var restaurantToSave = restaurantMapper.toCreateEntity(restaurant);

        var restautarantSaved = restaurantRepository.save(restaurantToSave);

        return new Client.RestaurantId(restautarantSaved.getId().toString());

    }

    @Override
    public List<Client> findAllBy(String name, String location, List<Cuisine> cuisines) {

        String cusinesString = (cuisines == null || cuisines.isEmpty()) ? null :
                cuisines.stream()
                .map(Enum::toString)
                .collect(Collectors.joining("_"));

        var restaurantRepositoryPage = restaurantRepository.findRestaurantsByCriteria(
            name, location, cusinesString
        );

        if (restaurantRepositoryPage.isEmpty()) return new ArrayList<>();

        return restaurantRepositoryPage.stream()
                .map(restaurantMapper::toModel)
                .toList();



    }

    @Override
    public Boolean existsName(String name) {
        return restaurantRepository.existsByName(name);
    }

    @Override
    @Transactional
    public Client save(Client restaurant) {
        var restaurantToSave = restaurantMapper.toCreateEntity(restaurant);

        var restaurantSaved = restaurantRepository.save(restaurantToSave);

        return restaurantMapper.toModel(restaurantSaved);
    }

    @Override
    @Transactional
    public Optional<Client> findById(Client.RestaurantId id) {

        var idUUid = UUID.fromString(id.id());

        var restaurantData = restaurantRepository.findById(idUUid);

        if (restaurantData.isEmpty()) return Optional.empty();

        var restaraunt = restaurantMapper.toModel(restaurantData.get());

        return Optional.ofNullable(restaraunt);

    }
}
