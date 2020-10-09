package facultad.logistics;

import facultad.logistics.model.*;
import facultad.logistics.repository.RoleRepository;
import facultad.logistics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class OnStartLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public OnStartLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(ApplicationArguments args) {
        roleRepository.save(new Role(ERole.ROLE_ADMIN, 1L));
        roleRepository.save(new Role(ERole.ROLE_USER, 2L));

        Role role = roleRepository.getByEnumRole(ERole.ROLE_ADMIN);
        userRepository.save(new User("agustinbettati@gmail.com", "AgustinBettati", passwordEncoder.encode("password"), role));
        userRepository.save(new User("marcoskhabie@gmail.com", "MarcosKhabie", passwordEncoder.encode("password"), role));

    }
}
