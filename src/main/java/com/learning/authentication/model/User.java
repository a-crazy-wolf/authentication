package com.learning.authentication.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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

    private Integer user_type;

    private boolean enabled;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;

    private String mfaSecret;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="role_user" ,
            joinColumns = {@JoinColumn(name = "user_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "id")})
    private List<Role> roles;

    public User(User user){
        this.id = id;
        this.firstName = user.firstName;
        this.username = user.emailId;
        this.emailId = user.emailId;
        this.password = user.password;
        this.user_type = user.user_type;
        this.enabled = user.enabled;
        this.accountNonExpired = user.accountNonExpired;
        this.accountNonLocked = user.accountNonLocked;
        this.credentialsNonExpired = user.credentialsNonExpired;
        this.mfaSecret = user.mfaSecret;
        this.roles = user.getRoles();
    }

}
