package it.progetto.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE", "PATCH","OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET, "/api/film/**", "/api/generi/**", "/api/registi/**", "/api/attori/**").permitAll()

                        // Registrazione post-login Keycloak: richiede JWT valido ma non un ruolo specifico
                        // (un utente appena registrato potrebbe non avere ancora il ruolo CLIENTE assegnato)
                        .requestMatchers(HttpMethod.POST, "/api/clienti/register").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/film/*/recensioni").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/recensioni/**").hasAuthority("CLIENTE")


                        .requestMatchers(HttpMethod.DELETE, "/api/recensioni/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/film/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/film/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/film/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/ordini/admin/tutti").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/ordini/**").hasAuthority("ADMIN")


                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(jwt);

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");

                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.toUpperCase())));
            }
            return authorities;
        });
        return converter;
    }
}