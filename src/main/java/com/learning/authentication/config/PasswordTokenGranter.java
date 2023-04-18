package com.learning.authentication.config;

import com.learning.authentication.model.AuthUserDetail;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PasswordTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "password";
    private final AuthenticationManager authenticationManager;

    public PasswordTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,AuthenticationManager authenticationManager){
        super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(), endpointsConfigurer.getOAuth2RequestFactory(),GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest){
        Map<String,String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");
        String password = parameters.get("password");
        parameters.remove("password");
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username,password);
        ((AbstractAuthenticationToken)userAuth).setDetails(parameters);

        try{
            userAuth = this.authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException e){
            throw new InvalidGrantException(e.getMessage());
        }

        if(Objects.nonNull(userAuth) && userAuth.isAuthenticated()){
            AuthUserDetail authUserDetail = (AuthUserDetail)userAuth.getPrincipal();
            OAuth2Request request = this.getRequestFactory().createOAuth2Request(client,tokenRequest);
            return new OAuth2Authentication(request, userAuth);
        }else{
            throw new InvalidGrantException("Could not authenticate user : " +username);
        }
    }
}
