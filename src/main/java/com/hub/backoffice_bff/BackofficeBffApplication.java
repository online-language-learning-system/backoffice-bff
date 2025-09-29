package com.hub.backoffice_bff;

import com.hub.backoffice_bff.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class BackofficeBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackofficeBffApplication.class, args);
	}

}
