package community.mingle.api.security.configuration;

import community.mingle.api.security.bean.JwtAccessDeniedHandler;
import community.mingle.api.security.bean.JwtAuthenticationEntryPoint;
import community.mingle.api.security.bean.JwtFilter;
import community.mingle.api.security.component.TokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(
        securedEnabled = true,
        proxyTargetClass = true,
        mode = AdviceMode.PROXY
)
public class SecurityConfiguration {

    private final TokenVerifier tokenVerifier;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(tokenVerifier);
    }

    //TODO 프로필별로 따로 설정 (prod swagger는 막기)
    //TODO hasRole로 역할에 따라 url 분리
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(it -> {
                    it.requestMatchers(
                            "/auth/**",
                            "/country",
                            "/university/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    ).permitAll()
                    .requestMatchers("/**")
                            .authenticated();
                })
                .csrf(CsrfConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .exceptionHandling(it-> {
                        it.authenticationEntryPoint(authenticationEntryPoint());
                        it.accessDeniedHandler(accessDeniedHandler());
                })
                .sessionManagement(it -> {
                    it.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();

    }



}
