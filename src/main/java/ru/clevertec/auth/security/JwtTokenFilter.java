package ru.clevertec.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;


/**
 * Filter class for JWT token authentication.
 * This filter intercepts the request to check for the presence of a JWT token in the Authorization header.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Processes an incoming request to authenticate the user based on a JWT token.
     *
     * @param servletRequest  the request from the client.
     * @param servletResponse the response to the client.
     * @param filterChain     the filter chain.
     */
    @Override
    @SneakyThrows
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain
    ) {
        String bearerToken = ((HttpServletRequest) servletRequest)
                .getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }
        try {
            if (bearerToken != null && jwtTokenProvider.isValid(bearerToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(bearerToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ignored) {
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
