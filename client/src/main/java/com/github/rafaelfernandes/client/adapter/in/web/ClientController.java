package com.github.rafaelfernandes.client.adapter.in.web;

import com.github.rafaelfernandes.common.response.ErrorResponse;
import com.github.rafaelfernandes.client.adapter.in.web.request.ClientRequest;
import com.github.rafaelfernandes.client.adapter.in.web.response.*;
import com.github.rafaelfernandes.client.application.port.in.*;
import com.github.rafaelfernandes.common.annotations.WebAdapter;
import com.github.rafaelfernandes.client.application.domain.model.Client;
import com.github.rafaelfernandes.common.enums.Cuisine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
@Tag(name = "01 - Restaurant", description = "Restaurant Endpoint")
public class ClientController {

    private final ManageRestaurantUseCase useCase;

    @Operation(summary = "Create a Restaurant")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "201", headers = {@Header(name = "/restaurant/id", description = "Location of restaurant")}),
            @ApiResponse(description = "Bad Request", responseCode = "400")
    })
    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> create(
            @Parameter @RequestBody final ClientRequest request, UriComponentsBuilder uriComponentsBuilder) {

        var addressModel = new Client.Address(
                request.address().street(),
                request.address().number(),
                request.address().addittionalDetails(),
                request.address().neighborhood(),
                request.address().city(),
                request.address().state()
        );

        var restaurantModel = new Client(
                request.name(), addressModel, openinHours, cuisines, request.tables()
        );

        var retaurantId = this.useCase.create(restaurantModel);

        URI location = uriComponentsBuilder
                .path("restaurants/{id}")
                .buildAndExpand(retaurantId.id())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();

    }

    @Operation(summary = "Get a restaurant by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Success", responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientResponse.class)
                    )
            ),
            @ApiResponse(
                    description = "Bad request", responseCode = "400",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    description = "Not found", responseCode = "404",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping(path = "/{restaurantId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ClientResponse> getById(@PathVariable final String restaurantId){

        var restaurantIdModel = new Client.RestaurantId(restaurantId);

        var restaurantData = useCase.findById(restaurantIdModel);

        var response = getRestaurantResponse(restaurantData);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Search Restaurant")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Success", responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientResponse.class)
                    )
            ),
            @ApiResponse(
                    description = "Bad request", responseCode = "400",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    description = "Not found", responseCode = "404",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ClientResponse>> getAllBy(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) List<String> cuisines ){

        var cuisinesEnum = (cuisines == null || cuisines.isEmpty()) ? new ArrayList<Cuisine>() :
                cuisines.stream()
                .map(Cuisine::valueOf)
                .toList();

        var restaurants = useCase.findAllBy(name, location, cuisinesEnum);

        var restaurantsData = restaurants.stream()
                .map(ClientController::getRestaurantResponse)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(restaurantsData);



    }

    private static ClientResponse getRestaurantResponse(Client restaurant){
        return getRestaurantResponse(Optional.ofNullable(restaurant));
    }

    private static ClientResponse getRestaurantResponse(Optional<Client> restaurantData) {

        var addressResponse = new AddressResponse(
                restaurantData.get().getAddress().getStreet(),
                restaurantData.get().getAddress().getNumber(),
                restaurantData.get().getAddress().getAddittionalDetails(),
                restaurantData.get().getAddress().getNeighborhood(),
                restaurantData.get().getAddress().getCity(),
                restaurantData.get().getAddress().getState()
        );



        var openingHoursResponses = restaurantData.get().getOpeningHours().stream()
                .map(openingHour -> new OpeningHourResponse(
                        openingHour.getDayOfWeek(),
                        openingHour.getStart(),
                        openingHour.getEnd()
                ))
                .toList();

        var cuisineResponses = restaurantData.get().getCuisines().stream()
                .map(cuisine -> new CuisineResponse(cuisine.getCuisine()))
                .toList();

        return new ClientResponse(
                UUID.fromString(restaurantData.get().getRestaurantId().id()),
                restaurantData.get().getName(),
                addressResponse,
                restaurantData.get().getTables(),
                openingHoursResponses,
                cuisineResponses
        );

    }




}



