package com.trendyol.checkout.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Checkout API",
                version = "1.0",
                description = "Checkout API for Trendyol Case Study",
                contact = @Contact(
                        name = "Altan Şimşir",
                        email = "simsiraltan@gmail.com",
                        url = "https://altansimsir.vercel.app/"
        ),
        license = @License(
                name = "no license",
                url = "no license url"
        )
        )
)
public class OpenApiConfig {
}
