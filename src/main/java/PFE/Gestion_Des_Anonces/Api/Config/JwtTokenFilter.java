package PFE.Gestion_Des_Anonces.Api.Config;

import PFE.Gestion_Des_Anonces.Api.Models.Role.RoleRepository;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Models.User.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request
            ,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
            throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return ;
        }
        final String Token = authHeader.split(" ")[1].trim();
        final String email = jwtService.extractEmail(Token);
        List<User> userList = userRepository.findByEmail(email);
        if(userList.isEmpty()){
            filterChain.doFilter(request,response);
            return;
        }
        User user = userList.get(0);
        if (!jwtService.isTokenValid(Token,user)) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                user, null,
                user.getAuthorities()
        );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request,response);
    }
}
