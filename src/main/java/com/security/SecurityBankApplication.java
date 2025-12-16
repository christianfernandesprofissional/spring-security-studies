package com.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
//@EnableWebSecurity //Anotação necessária caso voce não use Spring Boot e use somente Spring. É opcional no caso do Spring Boot
//@EnableJpaRepositories("com.security.repository") // A mesma ideia abaixo só que para os repositories
//@EntityScan("com.security.model")//anotações necessárias caso seja criado um pacote fora do principal
public class SecurityBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityBankApplication.class, args);
	}

}

