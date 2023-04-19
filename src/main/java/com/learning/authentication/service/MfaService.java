package com.learning.authentication.service;

import com.learning.authentication.model.User;
import com.learning.authentication.repository.UserDetailRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MfaService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    private GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    public boolean isMfaEnabled(String emailId){
        Optional<User> optionalUser = userDetailRepository.findByEmailId(emailId);
        if(optionalUser.isPresent() && optionalUser.get().getMfaSecret() != null){
            return true;
        }
        return false;
    }

    public boolean verifyCode(String emailId, int code){
        Optional<User> optionalUser = userDetailRepository.findByEmailId(emailId);
        if(optionalUser.isPresent() && optionalUser.get().getMfaSecret() != null){
            return code == googleAuthenticator.getTotpPassword(optionalUser.get().getMfaSecret());
        }
        return false;
    }
}
