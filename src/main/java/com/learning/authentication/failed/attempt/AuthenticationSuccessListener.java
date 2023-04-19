package com.learning.authentication.failed.attempt;

import com.learning.authentication.model.AuthUserDetail;
import com.learning.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof AuthUserDetail){
            AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
            if(authUserDetail.getFailedAttempt() > 0)
                userService.resetFailedAttempt(authUserDetail.getEmailId());
        }
    }
}
