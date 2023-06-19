package PFE.Gestion_Des_Anonces.Api.Config;

import PFE.Gestion_Des_Anonces.Api.Models.Role.RoleRepository;
import PFE.Gestion_Des_Anonces.Api.Services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.cloudinary.Cloudinary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Config {

    private final String CLOUD_NAME = "drkbf7big";
    private final String API_KEY = "176817514936632";
    private final String API_SECRET = "y-5ZNzPECJeZxd23g2icnPRYTvs";

    @Autowired
    private final JwtTokenFilter jwtTokenFilter;
    @Autowired
    private final UserService userService;
    @Autowired
    private final RoleRepository roleRepository;

    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name",CLOUD_NAME);
        config.put("api_key",API_KEY);
        config.put("api_secret",API_SECRET);
        return new Cloudinary(config);
    }
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http =  http
                .userDetailsService(userService)
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and()
                .authorizeRequests(
                        auth -> auth
                        .requestMatchers("/api/Membre/**")
                        .hasAnyAuthority("MEMBRE","ADMIN")
                        .requestMatchers("/api/Admin/**")
                        .hasAuthority("ADMIN")
                        .requestMatchers("/api/Auth/VerifyTokenAdmin")
                        .hasAuthority("ADMIN")
                        .requestMatchers("/api/Auth/VerifyToken")
                        .hasAnyAuthority("MEMBRE","ADMIN")
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}