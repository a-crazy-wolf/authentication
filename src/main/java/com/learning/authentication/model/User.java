package com.learning.authentication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;

    private String firstName;

    private String lastName;

    private String emailId;

    private String password;

    private Integer userType;

    private boolean enabled;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;

    private String mfaSecret;

    private int failedAttempt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="role_user" ,
            joinColumns = {@JoinColumn(name = "user_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "id")})
    private List<Role> roles;

    public User(User user){
        this.id = user.id;
        this.firstName = user.firstName;
        this.username = user.emailId;
        this.emailId = user.emailId;
        this.password = user.password;
        this.userType = user.userType;
        this.enabled = user.enabled;
        this.accountNonExpired = user.accountNonExpired;
        this.accountNonLocked = user.accountNonLocked;
        this.credentialsNonExpired = user.credentialsNonExpired;
        this.mfaSecret = user.mfaSecret;
        this.roles = user.getRoles();
        this.failedAttempt=user.failedAttempt;
    }

}
