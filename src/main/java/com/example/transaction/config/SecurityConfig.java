package com.example.transaction.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Secures {@code /getTrx/**} with JWT; all other paths are public.
     * CSRF is disabled (stateless API).
     *
     * @param http Spring Security builder; must not be {@code null}
     * @throws Exception on configuration failure
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/getTrx/**").authenticated()
                .anyRequest().permitAll()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class);
    }

        private static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private static final String BEARER_PREFIX = "Bearer ";
        private final SecretKey key;

        /**
         * Derives the HMAC-SHA signing key from the supplied secret.
         *
         * @param jwtSecret raw secret from {@code jwt.secret}; min 32 characters for HS256
         */
        JwtAuthenticationFilter(String jwtSecret) {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Validates the {@code Authorization: Bearer <token>} header on each request.
         * On success, populates {@link SecurityContextHolder}. On failure, returns 401.
         *
         * @param request     incoming HTTP request
         * @param response    HTTP response for writing 401 errors
         * @param filterChain remaining filter chain
         * @throws ServletException on servlet-level errors
         * @throws IOException      on I/O errors writing the error response
         */
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                String token = authorizationHeader.substring(BEARER_PREFIX.length());
                try {
                    var claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String subject = claims.getSubject();
                    if (subject == null || subject.isBlank()) {
                        throw new BadCredentialsException("Invalid JWT token subject");
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            subject,
                            token,
                            List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
