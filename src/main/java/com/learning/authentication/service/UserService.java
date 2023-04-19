package com.learning.authentication.service;

import com.learning.authentication.model.User;
import com.learning.authentication.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    public void increasedFailedAttempt(String emailId, int failedAttempt){
        userDetailRepository.updateFailedAttempt(1+ failedAttempt, emailId);
    }

    public void resetFailedAttempt(String emailId){
        userDetailRepository.updateFailedAttempt(0, emailId);
    }

    public void lockUser(User user){
        user.setAccountNonLocked(false);
        userDetailRepository.save(user);
    }

    public User findByEmailId(String emailId){
        return userDetailRepository.findByEmailId(emailId).orElse(null);
    }
}
