package PFE.Gestion_Des_Anonces.Api.Services;

import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Models.User.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<User> userList = userRepository.findByEmail(email);
        if(userList.size() == 0){
            throw new UsernameNotFoundException("User is null");
        }
        User user =  userList.get(0);
        return user;
    }

}
