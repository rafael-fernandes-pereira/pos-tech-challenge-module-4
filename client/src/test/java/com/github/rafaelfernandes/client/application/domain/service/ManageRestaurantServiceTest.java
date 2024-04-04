package com.github.rafaelfernandes.client.application.domain.service;

import com.github.rafaelfernandes.common.enums.Cuisine;
import com.github.rafaelfernandes.client.exception.RestaurantDuplicateException;
import com.github.rafaelfernandes.client.exception.RestaurantNotFoundException;
import com.github.rafaelfernandes.client.application.domain.model.Client;
import com.github.rafaelfernandes.client.application.port.out.ManageRestaurantPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import util.GenerateData;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManageRestaurantServiceTest {

    private final ManageRestaurantPort port = Mockito.mock(ManageRestaurantPort.class);
    private final ManageRestaurantService service = new ManageRestaurantService(port);

    @Nested
    class Create {

        @Test
        void validateCreateSuccessRestaurant(){

            var restaurantRequest = GenerateData.createRestaurant();

            when(port.existsName(any(String.class))).thenReturn(Boolean.FALSE);

            when(port.save(any(Client.class))).thenReturn(restaurantRequest);

            var saved = service.create(restaurantRequest);

            assertThat(saved).isNotNull();
            assertThat(saved.id()).isNotNull();

            verify(port, times(1)).existsName(any());
            verify(port, times(1)).save(any());

        }

        @Test
        void validateCreateDuplicateRestaurant(){

            var restaurantRequest = GenerateData.createRestaurant();

            when(port.existsName(any(String.class))).thenReturn(Boolean.TRUE);

            assertThatThrownBy(() -> {
                service.create(restaurantRequest);
            })
                    .isInstanceOf(RestaurantDuplicateException.class)
                    .hasMessage("Nome já cadastrado!")
            ;

            verify(port, times(1)).existsName(any());
            verify(port, times(0)).save(any());

        }

    }

    @Nested
    class FindById {

        @Test
        void validateSuccessFindById(){

            // Arrange

            var restaurantRequest = GenerateData.createRestaurant();

            when(port.findById(any(Client.RestaurantId.class))).thenReturn(Optional.of(restaurantRequest));

            // Act

            var restaurantData = service.findById(restaurantRequest.getRestaurantId());

            // Assert

            assertThat(restaurantData).isNotNull();
            assertThat(restaurantData.getRestaurantId()).isEqualTo(restaurantRequest.getRestaurantId());
            assertThat(restaurantData.getName()).isEqualTo(restaurantRequest.getName());

            verify(port, times(1)).findById(any());

        }

        @Test
        void validateNotFound(){

            // Arrange

            var restaurantRequest = GenerateData.createRestaurant();

            when(port.findById(any(Client.RestaurantId.class))).thenReturn(Optional.empty());

            // Act | Assert

            assertThatThrownBy(() -> {
                service.findById(restaurantRequest.getRestaurantId());
            })
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .hasMessage("Restaurante(s) não existe!")
            ;

            verify(port, times(1)).findById(any());

        }

    }

    @Nested
    class FindAllBy {


        @Test
        void validateAllParamNull(){

            // Act

            assertThatThrownBy(() -> {
                service.findAllBy(null, null, null);
            })
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pelo menos um dos parâmetros deve ser fornecido.")
            ;

            // Assert

            verify(port, times(0)).findAllBy(isNull(), isNull(), isNull());

        }

        @Test
        void validateAllParamEmpty(){

            // Act

            assertThatThrownBy(() -> {
                service.findAllBy("", "", new ArrayList<>());
            })
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pelo menos um dos parâmetros deve ser fornecido.")
            ;

            // Assert

            verify(port, times(0)).findAllBy(anyString(), anyString(), anyList());

        }

        @Test
        void findAllByName(){

            // Arrange

            var restaurant = GenerateData.createRestaurant();
            var restaurants = new ArrayList<Client>(){{
               add(restaurant);
            }};

            when(port.findAllBy(anyString(), isNull(), isNull())).thenReturn(restaurants);

            // Act

            var list = service.findAllBy(restaurant.getName(), null, null);

            // Assert

            assertThat(list).hasSize(1).contains(restaurant);
            verify(port, times(1)).findAllBy(any(), isNull(), isNull());

        }

        @Test
        void findAllByLocation() {

            // Arrange

            var restaurant = GenerateData.createRestaurant();
            var restaurants = new ArrayList<Client>(){{
                add(restaurant);
            }};

            when(port.findAllBy(isNull(), anyString(), isNull())).thenReturn(restaurants);

            // Act

            var list = service.findAllBy(null, "Rua JoseDaCosta", null);

            // Assert

            assertThat(list).hasSize(1).contains(restaurant);
            verify(port, times(1)).findAllBy(isNull(), anyString(), isNull());
        }

        @Test
        void findAllByCuisines() {

            // Arrange

            var restaurant = GenerateData.createRestaurant();
            var restaurants = new ArrayList<Client>(){{
                add(restaurant);
            }};

            var cuisines = new ArrayList<Cuisine>(){{
                add(Cuisine.BRAZILIAN);
            }};

            when(port.findAllBy(isNull(), isNull(), anyList())).thenReturn(restaurants);

            // Act

            var list = service.findAllBy(null, null, cuisines);

            // Assert

            assertThat(list).hasSize(1).contains(restaurant);
            verify(port, times(1)).findAllBy(isNull(), isNull(), anyList());
        }

        @Test
        void validateEmpty(){

            // Arrange

            when(port.findAllBy(anyString(), isNull(), isNull())).thenReturn(new ArrayList<Client>());

            // Act

            assertThatThrownBy(() -> {
                service.findAllBy("Oba Oba", null, null);
            })
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .hasMessage("Restaurante(s) não existe!")
            ;

            // Assert

            verify(port, times(1)).findAllBy(anyString(), isNull(), isNull());

        }

    }
}