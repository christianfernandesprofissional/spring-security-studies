package com.security.config.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import javax.sql.DataSource;

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
        http.httpBasic(Customizer.withDefaults()); // Este metodo irá mostra um login básico gerado pelo proprio navegador, que faz um pequeno formulario em forma de popup
        //http.httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable()); // Desabilitando esse popup basico, receberemos uma error page
        return http.build();
    }

    //Como nós implementamos a classe EazyBankUserDetailsService, ela já vai retornar um UserDetails, por isso o metodo abaixo não é mais necessário
    //já que já temos uma implementação que implementa UserDetailsService e já vai buscar o usuário para nós
   // @Bean
   // public UserDetailsService userDetailsService(DataSource dataSource){ // DataSource foi adicionado para implementação do banco de dados
        // -----GRAVAÇÃO DE USUÁRIO SOMENTE NA MEMÓRIA---------
        // Essa classe User já é padrão do Spring Security e ela implementa a interface UserDetails
        //UserDetails user = User.withUsername("user").password("{noop}12345").authorities("read").build();
        //UserDetails user = User.withUsername("user").password("12345").authorities("read").build();
        // Esse prefixo {noop} diz para a aplicação que eu não quero usar um passwordEncoder
        //UserDetails admin = User.withUsername("admin").password("{noop}54321").authorities("admin").build();
        //UserDetails admin = User.withUsername("admin").password("54321").authorities("admin").build();

        // Esse InMemoryUserDetailsManager armazena os dados em um objeto HashMap
        //return new InMemoryUserDetailsManager(user, admin);

        // -----GRAVAÇÃO DE USUÁRIO NO BANCO DE DADOS---------
        //As operações SQL são feitas automaticamente pelo SpringSecurity, em uma tabela padrão
        //que contem username, password e enabled
     //   return new JdbcUserDetailsManager(dataSource); // O próprio Spring cuida de trazer esta dataSource
    //}

    @Bean
    public PasswordEncoder passwordEncoder(){

        // Por padrão o Spring Security usa o Bcrypt encoder, mas se você passar
        //um prefixo dentro de chaves contendo o nome de outro encoder, o spring irá
        //entender, por exemplo senha: 12345, mas com o prefixo ficaria {bcrypt}12345
        //assim o spring irá entender que esta senha está criptografada com bcrypt e vai
        //decodificar antes de ler a senha. Para mais opções de prefixo basta
        //olhar o metodo createDelegatingPasswordEncoder(); usando o PasswordEncoderFactories
        //o encoder fica mais versátil porque vc pode trocar a qualquer momento o encoder.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        //Essa interface HaveIBeenPwnedRestApiPasswordChecker a verificar se a senha criada pelo usuário
        //já foi comprometida ou não. Se você não quiser usar esta classe
        //voce pode fazer sua própria, basta implementar a interface: CompromisedPasswordChecker
        //Caso a senha esteja comprometida, o Spring não vai deixar que o usuário se autentique
        //até que tenha uma senha que não esteja comprometida.
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
