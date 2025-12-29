package com.security.config.securityconfig;

import com.security.exceptionHandling.CustomAccessDeniedHandler;
import com.security.exceptionHandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import javax.sql.DataSource;

@Configuration
@Profile("!prod")
public class ProjectSecurityConfig {


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        //http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); //dessa maneira todas as requisições não terão segurança
        //http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll()); //dessa maneira todas as requisições serão negadas com erro 403
        //Por padrão, através da proteção csrf, toda a operação que altere dados é protegida pelo Spring Security mesmo com o endpoint estando permitido
        http
                                                                                                        //número máximo de sessões que um usuário pode ter ao mesmo tempo e configuração para prevenir que o usuário consiga fazer login novamente caso o número maximo de sessoes seja atingido, caso 'false' (default) o Spring só vai invalidar as sessoes mais antigas
                .sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(2).maxSessionsPreventsLogin(true).expiredUrl("/paginaDeSessaoExpiradaCasoONumeroMaximoDeSessoesSejaAtingido")) //Caminho para a página com sessão inválida, para ser mais agradavel para o usuário, do que ser sempre redirecionado para o login sempre que a sessão expira, não se esqueça de permitir essa página
                .redirectToHttps((https) -> https.disable()) // Only HTTP Config
              //  .redirectToHttps((https) -> https.requestMatchers(AnyRequestMatcher.INSTANCE)) // Only HTTPS Config, Porque por padrão o spring permite HTTP e HTTPS, para que o somente seja permitido HTTPS deve-se configurar explicitamente
                .csrf(csrfConfig -> csrfConfig.disable())// desabilitar o csrf para poder fazer metodos posts (depois irei configurar o csrf para aceitar os metodos posts)
                .authorizeHttpRequests((requests) -> requests.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/mycards").authenticated() //páginas que eu quero que sejam autenticadas
                .requestMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll()); //paginas permitidas; A página /error tbm é protegida por padrão, por isso precisamos permitir ela.
        //http.formLogin(Customizer.withDefaults());
        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable()); //desabilitando a tela padrão de login

        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); //Agora quando houver um erro de autenticação, nossa classe customizada ira tratar o erro isso somente para o basic HTTP
        //http.exceptionHandling(ehc -> ehc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); // Mesma configuração acima, só que de outra forma, e agora é uma configuração global ou seja, vale tanto para o HTTP basic, quanto para JWT, OAuth2 etc
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()).accessDeniedPage("/denied"));//Customização de acesso negado. O metodo accessDeniedPage é para voce redirecionar o usuário para outra pagina de acesso negado caso a aplicação tenha uma página dessa
        //http.httpBasic(Customizer.withDefaults()); // Este metodo irá tratar erro de autenticação da forma padrão (codigo 401) do Spring Security fazendo com que o navegador gere um popup de login básico caso o formLogin não esteja habilitado
        //http.httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable()); // Desabilitando esse popup basico, receberemos uma error page

        return http.build();
    }

    //Como nós implementamos a classe EazyBankUserDetailsService, ela já vai retornar um UserDetails, por isso o metodo abaixo não é mais necessário
    //já que já temos uma implementação de UserDetailsService e já vai buscar o usuário para nós
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

/*

Falando sobre Encoding, Encryption e Hashing

Enconding: Um processo de converter os dados de uma forma para outra forma, não tem nada a ver com criptografia
Encryption: Transforma os dados de uma maneira que garante confidencialidade, existem 2 tipos, criptografia simetrica e assimetrica
    - Simetrica: usa uma chave para criptografar e descriptografar
    - Assimetrica: usa uma chave publica para criptografar, e uma chave privada para descriptografar

Hashing: Dados são convertidos em hash, e uma vez que o dado vira um Hash ele é irreversível.
         O Hash gerado de um valor, sempre será o mesmo hash, por exemplo o valor hash de 12345 sempre será o mesmo.
         A desvantagem disso é que se voce armazena o mesmo valor varias vezes, ele sempre tera o mesmo valor hash.
         Outra desvantagem é que é muito rapido de produzir um valor hash, independente do tamanho do arquivo, oq torna ataques "fáceis" de serem feitos.
         Para evitar essas desvantagens temos algumas maneiras, uma é adicionar um valor "salt" que é um valor aleatorio que sera gerado e adicionado antes
         de cada texto armazenado por exemplo, o texto 12345, o valor salt seria "qwert" então oq sera transformado em hash será qwert12345.
         Outras maneiras alem disso são as recomendações padrões que temos para armazenamento de senhas.

         Principais implementações de encoders:
            Bcrypt
            Argon2 -> Dificil de configurar
            Scrypt -> Dificil de configurar

*/