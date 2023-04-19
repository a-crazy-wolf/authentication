package com.learning.authentication.custom.response;

import com.learning.authentication.model.AccessTokenUserDetails;
import com.learning.authentication.model.AuthUserDetail;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String,Object> additionalInfo = new HashMap<>();
        AuthUserDetail authUserDetail = ((AuthUserDetail) authentication.getPrincipal());
        AccessTokenUserDetails accessTokenUserDetails = new AccessTokenUserDetails();
        accessTokenUserDetails.setFirstName(authUserDetail.getFirstName());
        accessTokenUserDetails.setLastName(authUserDetail.getLastName());
        accessTokenUserDetails.setEmailId(authUserDetail.getEmailId());
        if(authUserDetail.getUserType() == 1){
            accessTokenUserDetails.setAuthorities(AuthorityUtils.authorityListToSet(authUserDetail.getAuthorities()));
        }
        additionalInfo.put("userInfo",accessTokenUserDetails);
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
