package com.github.rafaelfernandes.client.adapter.in.web;

import com.github.rafaelfernandes.client.adapter.in.web.request.AddressRequest;
import com.github.rafaelfernandes.client.adapter.in.web.request.ClientRequest;
import com.github.rafaelfernandes.client.adapter.in.web.response.ClientResponse;
import com.github.rafaelfernandes.client.adapter.out.persistence.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import util.GenerateData;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setup(){
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                    return execution.execute(request, body);
                }));
    }

    @AfterEach
    void tearDown(){
        restaurantRepository.deleteAll();
    }

    List<OpeningHourRequest> openingHours = GenerateData.generateOpeningHoursRequest();

    List<CuisineRequest> cuisines = GenerateData.generateCuisinesRequest();

    @Nested
    class Create {

        @Test
        void createSuccess() {

            ClientRequest request = GenerateData.gerenRestaurantRequest();

            ResponseEntity<String> create = createRestaurantPost(request);

            assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            URI location = create.getHeaders().getLocation();

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            location,
                            String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        }

        @Test
        void duplicateNameError() {

            AddressRequest addressRequest = GenerateData.generateAddressRequest();
            String name = "COMIDA BOA";

            ClientRequest request = new ClientRequest(name, addressRequest, 10, openingHours, cuisines);

            ResponseEntity<String> response = createRestaurantPost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            ResponseEntity<String> responseDuplicate = createRestaurantPost(request);

            assertThat(responseDuplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

            DocumentContext documentContext = JsonPath.parse(responseDuplicate.getBody());

            String error = documentContext.read("$.errors");

            assertThat(error).contains("Nome já cadastrado!");

        }

        @Test
        void createInvalidRequest() {

            AddressRequest addressRequest = GenerateData.generateAddressRequest();
            String name = "";

            ClientRequest request = new ClientRequest(name, addressRequest, 10, openingHours, cuisines);

            ResponseEntity<String> response = createRestaurantPost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String error = documentContext.read("$.errors");

            assertThat(error).contains("name: O campo deve estar preenchido");

        }

    }

    @Nested
    class FindById {

        @Test
        void validateCommandError() {

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                    "/restaurants/uuid-invalid",
                        String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String error = documentContext.read("$.errors");

            assertThat(error).isEqualTo("id: O campo deve ser do tipo UUID");

        }

        @Test
        void validateNotFound() {
            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            "/restaurants/e903732e-9d20-4023-a71a-5c761253fc1c",
                            String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String error = documentContext.read("$.errors");

            assertThat(error).isEqualTo("Restaurante(s) não existe!");
        }


        @Test
        void validateFound(){

            var request = GenerateData.gerenRestaurantRequest();

            ResponseEntity<String> create = createRestaurantPost(request);

            URI location = create.getHeaders().getLocation();

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            location,
                            String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String name = documentContext.read("$.name");
            assertThat(name).isEqualTo(request.name());

            String addressStreet = documentContext.read("$.address.street");
            assertThat(addressStreet).isEqualTo(request.address().street());

            Integer addressNumber = documentContext.read("$.address.number");
            assertThat(addressNumber).isEqualTo(request.address().number());

            String addressAddittionalDetails = documentContext.read("$.address.addittionalDetails");
            assertThat(addressAddittionalDetails).isEqualTo(request.address().addittionalDetails());

            String addressNeighborhood = documentContext.read("$.address.neighborhood");
            assertThat(addressNeighborhood).isEqualTo(request.address().neighborhood());

            String addressCity = documentContext.read("$.address.city");
            assertThat(addressCity).isEqualTo(request.address().city());

            String addressState = documentContext.read("$.address.state");
            assertThat(addressState).isEqualTo(request.address().state());

        }

    }

    @Nested
    class GetAllBy {

        @Test
        void validateNullParameters(){

            var paramaters = new HashMap<String, String>();
            paramaters.put("name", null);
            paramaters.put("location", null);
            paramaters.put("cuisines", null);

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            "/restaurants/",
                            String.class,
                            paramaters
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String error = documentContext.read("$.errors");

            assertThat(error).isEqualTo("Pelo menos um dos parâmetros deve ser fornecido.");

        }

        @Test
        void validateEmptyParameters(){

            var paramaters = new HashMap<String, Object>();
            paramaters.put("name", "");
            paramaters.put("location", "");
            paramaters.put("cuisines", "");

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            "/restaurants/?name={name}&location={location}&cuisines={cuisines}",
                            String.class,
                            paramaters
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String error = documentContext.read("$.errors");

            assertThat(error).isEqualTo("Pelo menos um dos parâmetros deve ser fornecido.");

        }


        @Test
        void validateSuccessName(){

            var request = GenerateData.gerenRestaurantRequest();

            createRestaurantPost(request);

            var paramaters = new HashMap<String, Object>();
            paramaters.put("name", request.name());
            paramaters.put("location", "");
            paramaters.put("cuisines", "");

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            "/restaurants/?name={name}&location={location}&cuisines={cuisines}",
                            String.class,
                            paramaters
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String name = documentContext.read("$.[0].name");

            assertThat(name).isEqualTo(request.name());

        }

        @Test
        void validateSuccessLocation(){

            var request = GenerateData.gerenRestaurantRequest();

            createRestaurantPost(request);

            var paramaters = new HashMap<String, Object>();
            paramaters.put("name", "");
            paramaters.put("location", request.address().street());
            paramaters.put("cuisines", "");

            ResponseEntity<String> response = restTemplate
                    .getForEntity(
                            "/restaurants/?name={name}&location={location}&cuisines={cuisines}",
                            String.class,
                            paramaters
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            DocumentContext documentContext = JsonPath.parse(response.getBody());

            String name = documentContext.read("$.[0].name");

            assertThat(name).isEqualTo(request.name());

        }

        @Test
        void validateSuccessCuisines(){

            var request = GenerateData.gerenRestaurantRequest();

            createRestaurantPost(request);

            var paramaters = new HashMap<String, Object>();
            paramaters.put("name", "");
            paramaters.put("location", "");
            paramaters.put("cuisines", request.cuisines().get(0).cuisine());

            ResponseEntity<ClientResponse[]> response = restTemplate
                    .getForEntity(
                            "/restaurants/?name={name}&location={location}&cuisines={cuisines}",
                            ClientResponse[].class,
                            paramaters
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);





        }

    }

    private ResponseEntity<String> createRestaurantPost(ClientRequest request) {
        return restTemplate
                .postForEntity(
                        "/restaurants/",
                        request,
                        String.class
                );

    }

}
