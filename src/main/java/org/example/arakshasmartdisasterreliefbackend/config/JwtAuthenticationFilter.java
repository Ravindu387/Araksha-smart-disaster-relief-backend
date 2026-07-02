package org.example.arakshasmartdisasterreliefbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectProvider<UserDetailsService> userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("========== JWT FILTER ==========");

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header : " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer Token Found");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("Token : " + token);

        try {

            String username = jwtService.extractUsername(token);
            System.out.println("Username From Token : " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                System.out.println("Loading UserDetails...");

                UserDetailsService actualUserDetailsService = userDetailsService.getObject();

                UserDetails userDetails = actualUserDetailsService.loadUserByUsername(username);

                System.out.println("Loaded User : " + userDetails.getUsername());

                if (jwtService.isTokenValid(token, userDetails)) {

                    System.out.println("TOKEN VALID");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("Authentication Success");
                    System.out.println(SecurityContextHolder.getContext().getAuthentication());

                } else {
                    System.out.println("TOKEN INVALID");
                }

            }

        } catch (Exception e) {

            System.out.println("JWT FILTER ERROR");
            e.printStackTrace();

        }

        filterChain.doFilter(request, response);
    }
}