package facultad.logistics.service;

import facultad.logistics.dto.user.ProfileEditDTO;
import facultad.logistics.dto.user.UserCreateDTO;
import facultad.logistics.dto.user.UserResponseDTO;
import facultad.logistics.exception.user.EmailExistsException;
import facultad.logistics.exception.user.IncorrectPasswordException;
import facultad.logistics.exception.user.UsernameExistsException;
import facultad.logistics.model.ERole;
import facultad.logistics.model.Role;
import facultad.logistics.model.User;
import facultad.logistics.exception.user.UserNotFoundException;
import facultad.logistics.repository.RoleRepository;
import facultad.logistics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        final User user = userRepository.findByEmail(email);

        if (user == null) throw new UserNotFoundException();

        return user;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public UserResponseDTO saveUser(UserCreateDTO userCreateDTO) {
        ERole eRole = (userCreateDTO.getRole().equals("admin")) ? ERole.ROLE_ADMIN : ERole.ROLE_USER;
        Role role = roleRepository.getByEnumRole(eRole);

        String encryptedPassword = passwordEncoder.encode(userCreateDTO.getPassword());

        final User user = new User(userCreateDTO.getEmail(), userCreateDTO.getUsername(), encryptedPassword, role);

        userRepository.save(user);
        return new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getRole());
    }

    public void deleteUser(Long id){
        final Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) throw new UserNotFoundException();
        userRepository.delete(user.get());
    }

    public void validateUsername(String username) {
        if (userRepository.existsByUsername(username))
            throw new UsernameExistsException("Username " + username + " already taken");
    }

    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailExistsException("Email " + email + " already taken");
    }

    public UserResponseDTO getUserById(Long userId) {
        final Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) throw new UserNotFoundException();

        return new UserResponseDTO(user.get().getId(), user.get().getEmail(), user.get().getUsername(), user.get().getRole());
    }

    public void editUser(ProfileEditDTO profileEditDTO, Long userId) {
        final Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) throw new UserNotFoundException();

        String oldPassword = profileEditDTO.getOldPassword();
        String newPassword = profileEditDTO.getNewPassword();

        if (oldPassword != null){
            if (passwordEncoder.matches(oldPassword, user.get().getPassword())) {
                if (newPassword != null)
                    user.get().setPassword(passwordEncoder.encode(newPassword));
            } else throw new IncorrectPasswordException();
        }

        String newUsername = profileEditDTO.getUsername();

        if (newUsername != null){
            if(!userRepository.existsByUsername(newUsername)) {
                user.get().setUsername(newUsername);
            } else throw new UsernameExistsException(String.format("Username %s already taken",newUsername));
        }

        userRepository.save(user.get());
    }
}
