package com.learning.authentication.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "role")
public class Role implements Serializable {

    private static final long serialVersionUID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private Integer type;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="permission_role" ,
            joinColumns = {@JoinColumn(name = "role_id" ,referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id",referencedColumnName = "id")})
    private List<Permission> permissions;

}
