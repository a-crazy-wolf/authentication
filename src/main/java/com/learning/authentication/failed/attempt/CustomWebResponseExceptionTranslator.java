package com.learning.authentication.failed.attempt;

import com.learning.authentication.model.User;
import com.learning.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * We used strategy design pattern for maintain failedAttempt.
 * failedAttempt count used by two different time because we need to send this count failed response
 */
@Component
public class CustomWebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private int failedAttempt = 0;

    @Autowired
    private UserService userService;

    @Value("${max.failed.attempt}")
    private int maxFailedAttempt;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        Authentication authentication = event.getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof String){
            String emailId = (String) authentication.getPrincipal();
            User user = userService.findByEmailId(emailId);
            if (user != null){
                if (user.isEnabled() && user.isAccountNonLocked()){
                    if (user.getFailedAttempt() < maxFailedAttempt -1)
                        userService.increasedFailedAttempt(user.getEmailId(), user.getFailedAttempt());
                    else
                        userService.lockUser(user);
                }
                failedAttempt = user.getFailedAttempt() + 1;
            }
        }
    }

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception{
        ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
        if(e instanceof InvalidGrantException){
            OAuth2Exception body = responseEntity.getBody();
            HttpStatus httpStatus = responseEntity.getStatusCode();
            body.addAdditionalInformation("failedAttempt",String.valueOf(failedAttempt));

            HttpHeaders headers = new HttpHeaders();
            headers.setAll(responseEntity.getHeaders().toSingleValueMap());
            return new ResponseEntity<>(body,headers,httpStatus);
        }
        return responseEntity;
    }

}
