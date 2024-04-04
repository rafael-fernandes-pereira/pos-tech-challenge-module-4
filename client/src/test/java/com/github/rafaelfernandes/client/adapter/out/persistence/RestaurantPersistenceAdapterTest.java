package com.github.rafaelfernandes.client.adapter.out.persistence;

import com.github.rafaelfernandes.common.enums.Cuisine;
import com.github.rafaelfernandes.client.application.domain.model.Client;
import org.junit.jupiter.api.BeforeEach;
import util.GenerateData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({RestaurantPersistenceAdapter.class, RestaurantMapper.class})
class RestaurantPersistenceAdapterTest {

    @Autowired
    private RestaurantPersistenceAdapter restaurantPersistenceAdapter;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @AfterEach
    void tearDown(){
        restaurantRepository.deleteAll();
    }


    @Nested
    class ExistsName {

        @Test
        void validateTrue(){
            // Arrange
            var restaurant = GenerateData.createRestaurant();
            var entity = restaurantMapper.toCreateEntity(restaurant);
            restaurantRepository.save(entity);

            // Act

            var isExists = restaurantPersistenceAdapter.existsName(restaurant.getName());

            // Assert

            assertThat(isExists).isTrue();


        }

        @Test
        void validateFalse() {

            // Act

            var isExists = restaurantPersistenceAdapter.existsName("Banana");

            // Assert

            assertThat(isExists).isFalse();

        }

    }

    @Nested
    class Save {

        @Test
        void validateSuccessSave(){

            var restaurant = GenerateData.createRestaurant();
            var restaurantSaved = restaurantPersistenceAdapter.save(restaurant);

            assertThat(restaurant.getRestaurantId()).isEqualTo(restaurantSaved.getRestaurantId());

            var restaurantIdUUID = UUID.fromString(restaurantSaved.getRestaurantId().id());

            var restaurantFound = restaurantRepository.findById(restaurantIdUUID);

            assertThat(restaurantFound).isPresent();
            assertThat(restaurantFound.get().getFullSearch()).isNotEmpty();

        }

    }

    @Nested
    class FindById {

        @Test
        void findSuccess(){

            var restaurantRequest = GenerateData.createRestaurant();
            var restaurant = restaurantPersistenceAdapter.save(restaurantRequest);

            var restaurantGet = restaurantPersistenceAdapter.findById(restaurant.getRestaurantId());

            assertThat(restaurantGet).isPresent();
            assertThat(restaurant.getRestaurantId()).isEqualTo(restaurantGet.get().getRestaurantId());
            assertThat(restaurantRequest.getName()).isEqualTo(restaurantGet.get().getName());
            assertThat(restaurantRequest.getOpeningHours()).isEqualTo(restaurantGet.get().getOpeningHours());
            assertThat(restaurantRequest.getTables()).isEqualTo(restaurantGet.get().getTables());
            assertThat(restaurantRequest.getCuisines()).isEqualTo(restaurantGet.get().getCuisines());
        }

        @Test
        void notFound(){

            var restaurantRequest = GenerateData.createRestaurant();

            var restaurantGet = restaurantPersistenceAdapter.findById(restaurantRequest.getRestaurantId());

            assertThat(restaurantGet).isEmpty();

        }



    }

    @Nested
    class FindAllBy {

        Client restaurant;

        @BeforeEach
        void setUp(){
            restaurant = GenerateData.createRestaurant();
            restaurantPersistenceAdapter.create(restaurant);
        }

        @Test
        void findByAllByNameSucess(){

            var restaurants = restaurantPersistenceAdapter.findAllBy(restaurant.getName(), "", null);

            assertThat(restaurants)
                    .hasSize(1)
                    .extracting(Client::getName)
                    .contains(restaurant.getName());


        }

        @Test
        void findByAllByLocationSucess(){

            var location = restaurant.getAddress().getStreet();

            var restaurants = restaurantPersistenceAdapter.findAllBy(null, location, null);

            assertThat(restaurants)
                    .hasSize(1)
                    .extracting(Client::getName)
                    .contains(restaurant.getName());


        }

        @Test
        void findByAllByCuisinesSucess(){

            var cuisines = new ArrayList<Cuisine>(){{
                add(Cuisine.valueOf(restaurant.getCuisines().get(0).getCuisine()));
            }};

            var restaurants = restaurantPersistenceAdapter.findAllBy(null, null, cuisines);

            assertThat(restaurants)
                    .hasSize(1)
                    .extracting(Client::getName)
                    .contains(restaurant.getName());


        }

        @Test
        void findAllReturnEmpty(){

            var restaurants = restaurantPersistenceAdapter.findAllBy("SAO_TOME", null, null);

            assertThat(restaurants).isEmpty();

        }

    }



}