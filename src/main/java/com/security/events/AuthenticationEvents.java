package com.security.events;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


//Exemplo de implementação de eventos de autenticação do Spring Security
@Component
@Slf4j //Anotação lombok para criar um Logger. Obs: No IntelliJ é necessário o plugin do lombok e usar a opção Build, Execution, Deployment → Compiler → Annotation Processors: Enable annotation processing
public class AuthenticationEvents {


    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent){
        log.info("Login successful for the user: {}", successEvent.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent){
        log.error("Login failed for the user: {} due to: {}", failureEvent.getAuthentication().getName(), failureEvent.getException().getMessage());
    }

}
