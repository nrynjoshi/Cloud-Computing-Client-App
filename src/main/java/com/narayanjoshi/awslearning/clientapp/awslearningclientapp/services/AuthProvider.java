package com.narayanjoshi.awslearning.clientapp.awslearningclientapp.services;

import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.AuthenticateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired private HttpServerService httpServerService;

    @Override
    public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
 
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        try{
            AuthenticateDTO authenticateDTO = httpServerService.loginUser(name, password);
            return new UsernamePasswordAuthenticationToken(
                    authenticateDTO, password, new ArrayList<>());
        }catch (Exception x){
            throw x;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}