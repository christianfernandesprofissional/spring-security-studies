package com.security.config.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        //http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); //dessa maneira todas as requisições não terão segurança
        //http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll()); //dessa maneira todas as requisições serão negadas com erro 403
        http.authorizeHttpRequests((requests) -> requests.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/mycards").authenticated() //páginas que eu quero que sejam autenticadas
                .requestMatchers("/notices", "/contact", "/error").permitAll()); //paginas permitidas; A página /error tbm é protegida por padrão, por isso precisamos permitir ela.
        //http.formLogin(Customizer.withDefaults());
        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable()); //desabilitando a tela padrão de login
        // http.httpBasic(Customizer.withDefaults()); // Este metodo irá mostra um login básico gerado pelo proprio navegador, que faz um pequeno formulario em forma de popup
        //http.httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable()); // Desabilitando esse popup basico, receberemos uma error page
        return http.build();
    }

}
