package com.gfa.siemensfoxbuybytemasters.configs;

import com.gfa.siemensfoxbuybytemasters.filters.JwtAuthFilter;
import com.gfa.siemensfoxbuybytemasters.services.UserInfoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig {

    @Bean
    UserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                   HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers(mvcMatcherBuilder.pattern("/api/register")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/auth")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/user")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/api/login")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/refresh")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/verify-email")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/user/{id}/ban")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST,"/user/{id}/rating")).hasAnyRole("USER", "VIP")
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,"/user/{id}/rating")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/user/rating/{id}")).hasAnyRole("USER", "VIP")
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE,"/user/{id}/rating/{ratingId}")).authenticated()

                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST,"/category")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT,"/category")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE,"/category")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,"/category")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/category/{id}")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/api/isRunning")).hasRole("USER")
//                        .requestMatchers(mvcMatcherBuilder.pattern("/api/isRunning")).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(mvcMatcherBuilder.pattern("/v3/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui.html")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()
//                       ^ Shouldn't be used with respect for frontend!

                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/advertisement")).authenticated()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT,"/advertisement/{id}")).authenticated()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE,"/advertisement/{id}")).authenticated()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,"/advertisement/{id}")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,"/advertisement")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/advertisement/{id}/message")).authenticated()
                        .requestMatchers(mvcMatcherBuilder.pattern("/advertisement/{id}/buy")).authenticated()
                        .requestMatchers(mvcMatcherBuilder.pattern("/advertisement/watch")).hasRole("VIP")
                        .requestMatchers(mvcMatcherBuilder.pattern("/vip")).hasRole("USER")


                        .requestMatchers(mvcMatcherBuilder.pattern("/logs")).hasRole("ADMIN")

                .anyRequest().authenticated())
                .csrf((csrf) -> csrf.disable());
        http.authenticationProvider(authenticationProvider())
                .addFilterBefore( jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}