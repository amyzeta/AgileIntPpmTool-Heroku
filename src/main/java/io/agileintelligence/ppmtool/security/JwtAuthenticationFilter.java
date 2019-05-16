package io.agileintelligence.ppmtool.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.agileintelligence.ppmtool.domain.UserPrincipal;
import io.agileintelligence.ppmtool.dto.LoginDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)  throws AuthenticationException {
        final LoginDto login;
        try {
            login = new Gson().fromJson(new InputStreamReader(request.getInputStream()), LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> emptyCredentials = new HashMap<>();
        if (StringUtils.isEmpty(login.getUsername())) {
            emptyCredentials.put("username", "Username cannot be empty");
        }
        if (StringUtils.isEmpty(login.getPassword())) {
            emptyCredentials.put("password", "Password cannot be empty");
        }
        if (!emptyCredentials.isEmpty()) {
            throw new EmptyCredentialsException(emptyCredentials);
        }
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
    }

    private static final class EmptyCredentialsException extends AuthenticationException {
        private final Map<String, String> emptyCredentials;

        Map<String, String> getEmptyCredentials() {
            return emptyCredentials;
        }

        public EmptyCredentialsException(final Map<String, String> emptyCredentials) {
            super("Username or password was empty");
            this.emptyCredentials = emptyCredentials;
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authentication) throws IOException, ServletException {
        final String username = ((UserPrincipal)authentication.getPrincipal()).getUsername();
        final String token = JwtToken.generate(username);
        response.setHeader(SecurityConstants.AUTHORIZATION_HEADER, token);
    }


    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException failed) throws IOException, ServletException {
        if (failed instanceof EmptyCredentialsException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(gsonBuilder().toJson(((EmptyCredentialsException)failed).getEmptyCredentials()));
        } else {
            super.unsuccessfulAuthentication(request, response, failed);
        }
    }

    private static Gson gsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

}
