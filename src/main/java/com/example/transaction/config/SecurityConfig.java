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
     * Configures the Spring Security filter chain for the application.
     *
     * <p>Rules applied:
     * <ul>
     *   <li>CSRF protection is disabled (stateless JWT-based API).</li>
     *   <li>All requests to {@code /getTrx/**} require a valid JWT bearer token.</li>
     *   <li>All other requests (e.g. {@code /generate-token}, {@code /booking/**}) are
     *       permitted without authentication.</li>
     *   <li>The custom {@link JwtAuthenticationFilter} is inserted before the default
     *       {@link UsernamePasswordAuthenticationFilter} so that JWT validation runs
     *       first on every request.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} builder provided by Spring Security; must not
     *             be {@code null}
     * @throws Exception if any Spring Security configuration step fails during startup
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
         * Constructs a new {@code JwtAuthenticationFilter} and derives the HMAC-SHA signing
         * key from the supplied secret string.
         *
         * @param jwtSecret the raw secret value from {@code jwt.secret} in
         *                  {@code application.yml}; must be at least 32 characters to
         *                  satisfy the HS256 minimum key length
         */
        JwtAuthenticationFilter(String jwtSecret) {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Intercepts each HTTP request exactly once and attempts JWT validation.
         *
         * <p>If the {@code Authorization} header is present and begins with {@code "Bearer "},
         * the token is parsed and verified against the HMAC-SHA key. On success, a
         * {@link UsernamePasswordAuthenticationToken} with the token subject and the
         * {@code ROLE_USER} authority is stored in the {@link SecurityContextHolder} so
         * that Spring Security treats the request as authenticated.
         *
         * <p>If the header is absent the filter passes the request through without
         * modification, allowing Spring Security's authorisation rules (configured in
         * {@link SecurityConfig#configure(HttpSecurity)}) to decide whether the request
         * is permitted.
         *
         * @param request     the incoming HTTP request; must not be {@code null}
         * @param response    the HTTP response used to send an error if token validation fails;
         *                    must not be {@code null}
         * @param filterChain the remaining filter chain to invoke after this filter completes;
         *                    must not be {@code null}
         * @throws ServletException if a servlet-level error occurs during filter processing
         * @throws IOException      if an I/O error occurs while writing the error response
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
