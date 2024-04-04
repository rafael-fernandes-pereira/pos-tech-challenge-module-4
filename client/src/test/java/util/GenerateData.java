package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.rafaelfernandes.client.adapter.in.web.request.AddressRequest;
import com.github.rafaelfernandes.client.adapter.in.web.request.ClientRequest;
import com.github.rafaelfernandes.client.application.domain.model.Client;
import com.github.rafaelfernandes.common.enums.Cuisine;
import net.datafaker.Faker;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GenerateData {

    private static final Faker faker = new Faker(new Locale("pt", "BR"));

    private static final Random random = new Random();

    public static List<Client.OpeningHour> createDefaultOpeningHours(){
        List<Client.OpeningHour> openingHours = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            LocalTime start = LocalTime.of(9, 0);
            LocalTime end = LocalTime.of(18, 0);
            openingHours.add(new Client.OpeningHour(dayOfWeek.name(), start, end));
        }

        return openingHours;
    }

    public static Client createRestaurant(){
        var name = faker.restaurant().name();
        var address = generateAddress();
        var openingHours = createDefaultOpeningHours();

        return new Client(name, address, openingHours, generateCuisines(), 10);
    }

    public static Client.Address generateAddress() {
        return new Client.Address(
                faker.address().streetAddress(),
                Integer.valueOf(faker.address().streetAddressNumber()),
                faker.address().secondaryAddress(),
                "Centro",
                faker.address().city(),
                faker.address().stateAbbr());
    }

    public static List<Client.Cuisine> generateCuisines() {
        var cuisines = new ArrayList<Client.Cuisine>();

        cuisines.add(new Client.Cuisine(Cuisine.BRAZILIAN.name()));

        return cuisines;
    }



    public static AddressRequest generateAddressRequest(){
        return new AddressRequest(faker.address().streetAddress(),
                Integer.valueOf(faker.address().streetAddressNumber()),
                faker.address().secondaryAddress(),
                faker.name().lastName(),
                faker.address().city(),
                faker.address().stateAbbr()
        );
    }

    public static List<CuisineRequest> generateCuisinesRequest() {
        var cuisines = new ArrayList<CuisineRequest>();

        cuisines.add(new CuisineRequest(Cuisine.BRAZILIAN.name()));

        return cuisines;
    }

    public static List<OpeningHourRequest> generateOpeningHoursRequest(){
        var openingHours = new ArrayList<OpeningHourRequest>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            var start = LocalTime.of(9, 0);
            var end = LocalTime.of(18, 0);
            openingHours.add(new OpeningHourRequest(dayOfWeek.name(), start, end));
        }

        return openingHours;
    }

    public static ClientRequest gerenRestaurantRequest(){
        String name = faker.restaurant().name();
        AddressRequest addressRequest = generateAddressRequest();

        return new ClientRequest(name, addressRequest, 10, generateOpeningHoursRequest(), generateCuisinesRequest());
    }

    private static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Iterator<Map<String, Object>> generatePerformanceRequest(){



        return
                Stream.generate((Supplier<Map<String, Object>>) () -> {

                    var request = new HashMap<String, Object>();

                    request.put("address_street", faker.address().streetAddress());
                    request.put("address_number", Integer.valueOf(faker.address().streetAddressNumber()));
                    request.put("address_addittionalDetails", faker.address().secondaryAddress());
                    request.put("address_neighborhood", faker.name().lastName());
                    request.put("address_city", faker.address().city());
                    request.put("address_state", faker.address().stateAbbr());

                    request.put("name", faker.restaurant().name());


                    return request;


                }).iterator();

    }

}
