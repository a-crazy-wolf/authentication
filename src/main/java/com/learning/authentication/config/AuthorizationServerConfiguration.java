package com.learning.authentication.config;

import com.learning.authentication.custom.response.CustomAccessTokenConverter;
import com.learning.authentication.custom.response.CustomTokenEnhancer;
import com.learning.authentication.failed.attempt.CustomWebResponseExceptionTranslator;
import com.learning.authentication.mfa.MfaTokenGranter;
import com.learning.authentication.service.MfaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
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
    private final CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;
    private final MfaService mfaService;

    public AuthorizationServerConfiguration(PasswordEncoder passwordEncoder, DataSource dataSource, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator, MfaService mfaService){
      this.passwordEncoder = passwordEncoder;
      this.dataSource = dataSource;
      this.authenticationManager = authenticationManager;
      this.userDetailsService = userDetailsService;
      this.customWebResponseExceptionTranslator = customWebResponseExceptionTranslator;
      this.mfaService = mfaService;
    }

    @Bean
    TokenStore jdbcTokenStore(){
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception{
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception{
        security.checkTokenAccess("permitAll()").tokenKeyAccess("permitAll()");
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints){
        List<TokenGranter> granters = new ArrayList<TokenGranter>(Arrays.asList(endpoints.getTokenGranter()));
        granters.add(new PasswordTokenGranter(endpoints,authenticationManager,mfaService));
        granters.add(new MfaTokenGranter(endpoints,authenticationManager,mfaService));
        return new CompositeTokenGranter(granters);
    }

    @Bean
    public TokenEnhancer tokenEnhancer(){
        return new CustomTokenEnhancer();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        endpoints.tokenStore(jdbcTokenStore())
                .accessTokenConverter(new CustomAccessTokenConverter())
                .tokenEnhancer(tokenEnhancer());
        endpoints.tokenGranter(tokenGranter(endpoints));
        endpoints.userDetailsService(userDetailsService);
        endpoints.exceptionTranslator(customWebResponseExceptionTranslator);
    }

}
