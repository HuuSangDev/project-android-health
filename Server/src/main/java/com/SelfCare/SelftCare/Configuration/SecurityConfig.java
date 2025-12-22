package com.SelfCare.SelftCare.Configuration;

import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {


    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerkey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity
                .authorizeHttpRequests(request->
                    request.requestMatchers(
                            "/auth/**",
                            "/Users/Register",
                            "/api/chat/**",
                            "/ws/**",           // WebSocket endpoint
                            "/notifications/**" // Notification API
                    ).permitAll()
                            .anyRequest().authenticated()//
                        );



        //xác thực( authentication)
        httpSecurity.oauth2ResourceServer(oauth2->
                oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(jwtDecoder()) //kiểm tra token có hợp lệ hay k , sau đó gọi tới để giải
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))

        );


        // Tắt CSRF nếu cần
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
    //customer lai SCOPE_ thanh ROLE_
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter()
    {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter= new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");//custom Scope thành ROLE

        JwtAuthenticationConverter jwtAuthenticationConverter= new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder()
    {
        SecretKeySpec secretKeySpec= new SecretKeySpec(signerkey.getBytes(),"HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(10);
    }
}
