package com.learning.authentication.custom.response;

import com.learning.authentication.model.AuthUserDetail;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

public class CustomAccessTokenConverter extends DefaultAccessTokenConverter implements AccessTokenConverter {

    public Map<String,?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication){
        Map<String, Object> response = (Map<String, Object>)super.convertAccessToken(token,authentication);
        AuthUserDetail authUserDetail = (AuthUserDetail)authentication.getPrincipal();
        response.put("userInfo",authUserDetail);
        response.put("userId",authUserDetail.getId());
        return response;
    }
}
