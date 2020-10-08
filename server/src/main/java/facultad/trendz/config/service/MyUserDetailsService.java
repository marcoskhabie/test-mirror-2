package facultad.trendz.config.service;

import facultad.trendz.config.model.MyUserDetails;
import facultad.trendz.model.User;
import facultad.trendz.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return MyUserDetails.build(user);
    }

    public String getUsernameByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) throw new UsernameNotFoundException("User Not Found with email: " + email);

        return user.getUsername();
    }
}
