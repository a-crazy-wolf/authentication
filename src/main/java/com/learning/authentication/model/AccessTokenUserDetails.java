package com.learning.authentication.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenUserDetails implements Serializable {

    private static final long serialVersionUID = -1;

    private String emailId;

    private String firstName;

    private String lastName;

    private Collection<String> authorities;
}
