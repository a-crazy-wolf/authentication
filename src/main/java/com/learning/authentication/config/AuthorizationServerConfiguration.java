package com.learning.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter implements AuthorizationServerConfigurer {
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthorizationServerConfiguration(PasswordEncoder passwordEncoder, DataSource dataSource, AuthenticationManager authenticationManager, UserDetailsService userDetailsService){
      this.passwordEncoder = passwordEncoder;
      this.dataSource = dataSource;
      this.authenticationManager = authenticationManager;
      this.userDetailsService = userDetailsService;
    }

    @Bean
    TokenStore jdbcTokenStore(){
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception{
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints){
        List<TokenGranter> granters = new ArrayList<TokenGranter>(Arrays.asList(endpoints.getTokenGranter()));
        granters.add(new PasswordTokenGranter(endpoints,authenticationManager));
        return new CompositeTokenGranter(granters);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        endpoints.tokenStore(jdbcTokenStore());
        endpoints.tokenGranter(tokenGranter(endpoints));
        endpoints.userDetailsService(userDetailsService);
    }

}
