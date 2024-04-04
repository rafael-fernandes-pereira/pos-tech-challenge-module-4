package com.github.rafaelfernandes;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Tech Challenge 3 - Restaurant API",
				version = "1.0",
				description = "Api of Restaurant to Tech Challenge 3 - Pos Tech FIAP",
				license = @License(name = "Apache 2.0")

		)
)
@SpringBootApplication
public class ClientApplication {


	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

}
