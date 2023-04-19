package com.learning.authentication.mfa;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class MfaRequiredException extends OAuth2Exception {

    public MfaRequiredException(String mfaToken) {
        super("Multi-factor authentication required");
        this.addAdditionalInformation("mfa_token",mfaToken);
    }

    public MfaRequiredException(String mfaToken, String barCodeUrl) {
        super("Multi-factor authentication required");
        this.addAdditionalInformation("mfa_token",mfaToken);
        this.addAdditionalInformation("barCodeUrl",barCodeUrl);
    }

    public String getOAuth2ErrorCode(){
        return "mfa_required";
    }

    public int getHttpErrorCode(){
        return 403;
    }
}
