package com.learning.authentication.config;

import com.learning.authentication.mfa.MfaRequiredException;
import com.learning.authentication.model.AuthUserDetail;
import com.learning.authentication.service.BarCodeUtilities;
import com.learning.authentication.service.MfaService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PasswordTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "password";

    private static final GrantedAuthority PRE_AUTH = new SimpleGrantedAuthority("PRE_AUTH");

    private final AuthenticationManager authenticationManager;

    private final MfaService mfaService;

    public PasswordTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,AuthenticationManager authenticationManager, MfaService mfaService){
        super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(), endpointsConfigurer.getOAuth2RequestFactory(),GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.mfaService = mfaService;
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
            if(mfaService.isMfaEnabled(authUserDetail.getEmailId())){
                userAuth = new UsernamePasswordAuthenticationToken(userAuth.getPrincipal(),password, Collections.singleton(PRE_AUTH));
                OAuth2AccessToken accessToken = getTokenServices().createAccessToken(new OAuth2Authentication(request,userAuth));
                String mfaToken = authUserDetail.getMfaSecret();
                String barCodeUrl = BarCodeUtilities.getBarCodeUrl(authUserDetail.getEmailId(),mfaToken);
                throw new MfaRequiredException(accessToken.getValue(),barCodeUrl);
            }
            return new OAuth2Authentication(request, userAuth);
        }else{
            throw new InvalidGrantException("Could not authenticate user : " +username);
        }
    }
}
