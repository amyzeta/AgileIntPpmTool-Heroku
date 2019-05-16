package io.agileintelligence.ppmtool.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(final AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final Optional<Claims> claims = Optional.ofNullable(request.getHeader(SecurityConstants.AUTHORIZATION_HEADER))
                .filter(h -> h.startsWith(SecurityConstants.TOKEN_PREFIX))
                .map(h -> h.substring(SecurityConstants.TOKEN_PREFIX.length()))
                .map(this::getClaims);

        claims.map(Claims::getSubject)
                .map(JwtAuthorizationFilter::tokenForUserName)
                .ifPresent(t -> SecurityContextHolder.getContext().setAuthentication(t));

        claims.ifPresent( c-> {
            if (needsRenewal(c)) {
                final String token = JwtToken.generate(c.getSubject());
                response.setHeader(SecurityConstants.AUTHORIZATION_HEADER, token);
            }
        });
        chain.doFilter(request, response);
    }

    private Claims getClaims(final String token) {
        try {
            return Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static UsernamePasswordAuthenticationToken tokenForUserName(final String username) {
        return new UsernamePasswordAuthenticationToken(username, null, Collections.singleton((GrantedAuthority) () -> "USER"));
    }

    private static boolean needsRenewal(final Claims claims) {
        // provide user with new token when in the second half of its valid period
        return new Date().getTime() > (claims.getExpiration().getTime() + claims.getIssuedAt().getTime())/2;
    }
}
