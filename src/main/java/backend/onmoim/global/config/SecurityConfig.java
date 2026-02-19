package backend.onmoim.global.config;

import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.security.AuthenticationEntryPointImpl;
import backend.onmoim.global.security.JwtAuthFilter;
import backend.onmoim.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserQueryRepository userQueryRepository;


    private final String[] swagger_uris = {
            "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**"
    };

    private final String[] login_uris = {
            "/api/v1/users/login", "/api/v1/users/signup"
    };

    private final String[] test_uris = {
            "/api/v1/test/**"
    };

    private final String[] auth_uris = {
            "/api/v1/auth/email/**",
            "/api/v1/auth/refresh"
    };



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(swagger_uris).permitAll()
                        .requestMatchers(login_uris).permitAll()
                        .requestMatchers(test_uris).permitAll()
                        .requestMatchers(auth_uris).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint())
                );

        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil, userQueryRepository);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
