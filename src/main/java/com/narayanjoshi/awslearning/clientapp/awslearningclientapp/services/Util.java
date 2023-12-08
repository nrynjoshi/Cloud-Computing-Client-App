package com.narayanjoshi.awslearning.clientapp.awslearningclientapp.services;

import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.AuthenticateDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class Util {

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.isAuthenticated() && auth instanceof UsernamePasswordAuthenticationToken;
    }

    public static String getAuthToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthenticateDTO authenticateDTO = (AuthenticateDTO) auth.getPrincipal();
        return authenticateDTO.getToken();
    }

}
