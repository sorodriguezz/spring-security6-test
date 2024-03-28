package com.app.config;

import com.app.config.filter.JwtTokenValidator;
import com.app.service.UserDetailServiceImpl;
import com.app.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity // activar seguridad web
@EnableMethodSecurity // hacer configuraciones con notaciones
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean // sin notaciones
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults()) // solo cuando nos logueamos con user y pass
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
//                    http.requestMatchers(HttpMethod.GET, "/method/hello").permitAll(); // endp publicos
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll(); // endp publicos

                    http.requestMatchers(HttpMethod.POST, "/method/hello").hasAnyRole("ADMIN","DEVELOPER"); // endp privados
                    http.requestMatchers(HttpMethod.PATCH, "/method/hello").hasAnyAuthority("REFACTOR"); // endp privados
                    http.requestMatchers(HttpMethod.GET, "/method/hello").hasAnyRole("INVITED"); // endp privados

                    // endp no especificados, no ingresan
                    http.anyRequest().denyAll();
                    // endp no especificados, se tienen que loguear
                    // http.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class) // se ejecuta antes del filtro de autenticación
                .build();
    }

//    @Bean // con notaciones
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .build();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
//        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

//    @Bean // usuarios en memoria
//    public UserDetailsService userDetailsService() {
//        List<UserDetails> userDetailsList = new ArrayList<>();
//
//        userDetailsList.add(User.withUsername("sorodriguezz")
//                .password("1234")
//                .roles("ADMIN")
//                .authorities("READ", "CREATE")
//                .build());
//        userDetailsList.add(User.withUsername("sorodriguezz2")
//                .password("12345")
//                .roles("ADMIN")
//                .authorities("READ")
//                .build());
//
//        return new InMemoryUserDetailsManager(userDetailsList);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance(); // solo para hacer pruebas, ya que no encripta las contraseñas
        return new BCryptPasswordEncoder(); // en producción
    }

}
