package org.example.arakshasmartdisasterreliefbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Lazy
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/api/auth/**", "/api/landing/**") // ignore CSRF for public login/signup/stats
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // support session-based auth
                )
                .securityContext(context -> context
                        .securityContextRepository(new DelegatingSecurityContextRepository(
                                new HttpSessionSecurityContextRepository(),
                                new RequestAttributeSecurityContextRepository()
                        ))
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Return 401 instead of redirecting
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/landing/**").permitAll()
                        .requestMatchers("/api/dashboard/stats/*").hasAnyRole("VOLUNTEER", "ADMIN")
                        .requestMatchers("/api/dashboard/stats").hasRole("ADMIN")
                        .requestMatchers("/api/citizens/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers("/api/volunteerhub/**").hasAnyRole("VOLUNTEER", "ADMIN")
                        .requestMatchers("/api/volunteers/**", "/api/shelters/**", "/api/inventory/**", "/api/allocations/**", "/api/incidents/**", "/api/emergency-requests/**", "/api/notifications/**", "/api/reports/**", "/api/settings/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
