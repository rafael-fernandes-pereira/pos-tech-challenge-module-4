package com.github.rafaelfernandes.client.application.domain.model;

import com.github.rafaelfernandes.common.enums.State;
import com.github.rafaelfernandes.common.validation.ValueOfEnum;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.github.rafaelfernandes.common.validation.Validation.validate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Client {

    private final RestaurantId restaurantId;

    @NotEmpty(message = "O campo deve estar preenchido")
    @Length(min = 3, max = 100, message = "O campo deve ter no minimo {min} e no maximo {max} caracteres")
    private String name;

    @NotNull(message = "O campo deve ser maior que zero (0)")
    @Positive(message = "O campo deve ser maior que zero (0)")
    private Integer tables;

    private final LocalDateTime register;

    @NotNull(message = "O campo deve estar preenchido")
    private Address address;

    @NotNull(message = "O campo deve estar preenchido")
    private List<OpeningHour> openingHours;

    @NotNull(message = "O campo deve estar preenchido")
    private List<Cuisine> cuisines;

    public record RestaurantId(
            @NotEmpty(message = "O campo deve ser do tipo UUID")
            @org.hibernate.validator.constraints.UUID(message = "O campo deve ser do tipo UUID")
            String id
    ) {
        public RestaurantId(String id){
            this.id = id;
            validate(this);
        }
    }

    @Value
    public static class Address {

        @NotEmpty(message = "O campo deve estar preenchido")
        @Length( min = 10, max = 150, message = "O campo deve ter no minimo {min} e no maximo {max} caracteres")
        String street;

        @NotNull(message = "O campo deve ser maior que zero (0)")
        @Positive(message = "O campo deve ser maior que zero (0)")
        Integer number;

        @Length( max = 150, message = "O campo deve ter no máximo {max} caracteres")
        String addittionalDetails;

        @NotEmpty(message = "O campo deve estar preenchido")
        @Length( min = 3, max = 30, message = "O campo deve ter no minimo {min} e no máximo {max} caracteres")
        String neighborhood;

        @NotEmpty(message = "O campo deve estar preenchido")
        @Length( min = 3, max = 60, message = "O campo deve ter no minimo {min} e no máximo {max} caracteres")
        String city;

        @NotNull(message = "O campo deve ser uma sigla de estado válida")
        @ValueOfEnum(enumClass = State.class, message = "O campo deve ser uma sigla de estado válida")
        String state;

        public Address(String street, Integer number, String addittionalDetails, String neighborhood, String city, String state) {
            this.street = street;
            this.number = number;
            this.addittionalDetails = addittionalDetails;
            this.neighborhood = neighborhood;
            this.city = city;
            this.state = state;
            validate(this);
        }
    }

    @Value
    public static class OpeningHour {
        @NotNull(message = "O campo deve ser uma sigla de dias válidos")
        @ValueOfEnum(enumClass = DayOfWeek.class, message = "O campo deve ser uma sigla de dias válidos")
        String dayOfWeek;

        @NotNull(message = "O campo deve estar preenchido")
        LocalTime start;

        @NotNull(message = "O campo deve estar preenchido")
        LocalTime end;

        public OpeningHour(String dayOfWeek, LocalTime start, LocalTime end) {
            this.dayOfWeek = dayOfWeek;
            this.start = start;
            this.end = end;
            validate(this);

            if (this.end.isBefore(this.start) || this.end.equals(this.start)) throw new ValidationException("O horário final deve ser depois do inicial");

        }
    }

    @Value
    public static class Cuisine {
        @NotEmpty(message = "O tipo de cozinha deve seguir um dos exemplos.")
        @ValueOfEnum(enumClass = com.github.rafaelfernandes.common.enums.Cuisine.class, message = "O tipo de cozinha deve seguir um dos exemplos.")
        String cuisine;
        public Cuisine(String cuisine){
            this.cuisine = cuisine;
            validate(this);
        }
    }

    public Client(String name, Address address) {
        this.name = name;
        this.address = address;

        this.register = LocalDateTime.now();

        validate(this);

        this.restaurantId = new RestaurantId(UUID.randomUUID().toString());

    }


    public static Client of(String restaurantId, String name, Address address, LocalDateTime register, List<OpeningHour> openingHours, Integer numberOfTables, List<Cuisine> cuisines){
        return new Client(new RestaurantId(restaurantId), name, numberOfTables, register, address,  openingHours, cuisines);
    }

}
