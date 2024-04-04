package com.github.rafaelfernandes.client.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "ClientResponse", description = "Data of Client")
public record ClientResponse(

        @Schema(description = "Id of Client")
        UUID id,

        @Schema(description = "Name of Client")
        String name,

        @Schema(name = "address", description = "address of Client")
        AddressResponse address

) {
}
