package com.github.rafaelfernandes.client.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ClientRequest", description = "Data of client")
public record ClientRequest(

        @Schema(description = "Name of client")
        String name,

        @Schema(description = "Email of client")
        String email,

        @Schema(name = "address", description = "Address of client")
        AddressRequest address



) {
}
