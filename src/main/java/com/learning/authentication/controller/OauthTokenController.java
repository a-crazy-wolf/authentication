package com.learning.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

@RestController
@RequestMapping("/oauth/token")
public class OauthTokenController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DeleteMapping("/revoke")
    public ResponseEntity<Boolean> revokeToken(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String bearer = "Bearer";
        boolean result = false;
        if(authorization != null && authorization.contains(bearer)){
            String tokenId = authorization.substring(bearer.length()+1);
            result = consumerTokenServices.revokeToken(tokenId);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/disable")
    public ResponseEntity<Boolean> deleteTokenForInactiveUser(@RequestParam("username") String username){
        boolean result = false;
        if (username != null){
            Collection<OAuth2AccessToken> accessTokens = ((JdbcTokenStore)tokenStore).findTokensByUserName(username);
            for(OAuth2AccessToken token : accessTokens){
                result = consumerTokenServices.revokeToken(token.getValue());
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/check_client")
    public ResponseEntity<Boolean> validateClient(@RequestParam("token") String token){
        boolean result = false;
        try {
            String credentials = getClientCredentials(token);
            String clientId = getClientIdOrPassword(credentials,0);
            String password = getClientIdOrPassword(credentials,1);
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            if(clientDetails != null
                    && passwordEncoder.matches(password,clientDetails.getClientSecret())
                    && clientDetails.isAutoApprove("true")){
                result = true;
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    private String getClientCredentials(String base64Credentials){
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        return new String(credDecoded, StandardCharsets.UTF_8);
    }

    private String getClientIdOrPassword(String credentials, int index){
        String[] values = credentials.split(":",2);
        return values[index];
    }
}
