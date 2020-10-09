package facultad.logistics.controller;

import facultad.logistics.config.model.MyUserDetails;
import facultad.logistics.dto.*;
import facultad.logistics.dto.user.*;
import facultad.logistics.model.User;
import facultad.logistics.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Controller
public class UserController {
    private final UserService userService;
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("userId") Long userId) {
        final UserResponseDTO body = userService.getUserById(userId);
        final HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(body, status);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/user")
    public ResponseEntity<List<User>> getAll() {
        final List<User> result = userService.getAll();
        final HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(result, status);
    }

    @PostMapping(value = "/user")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDTO user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            final HttpStatus status = HttpStatus.BAD_REQUEST;
            String error = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            return new ResponseEntity<>(error, status);
        }
        userService.validateEmail(user.getEmail());
        userService.validateUsername(user.getUsername());
        final UserResponseDTO body = userService.saveUser(user);
        final HttpStatus status = HttpStatus.CREATED;

        return new ResponseEntity<>(body, status);
    }

    @DeleteMapping(value = "/user")
    public ResponseEntity<MessageResponseDTO> deleteUser()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((MyUserDetails)authentication.getPrincipal()).getId();
         userService.deleteUser(id);
        MessageResponseDTO dto= new MessageResponseDTO("User Deleted");
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(dto, status);
    }


    @PutMapping("/user")
    public ResponseEntity<MessageResponseDTO> editProfile(@RequestBody ProfileEditDTO profileEditDTO, Authentication authentication){

        MyUserDetails userDetails = (MyUserDetails)authentication.getPrincipal();

        userService.editUser(profileEditDTO, userDetails.getId());

        MessageResponseDTO body = new MessageResponseDTO("Profiled edited successfully");
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body, status);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminContent() { // testing admin exclusive endpoint
        String body = "Admin Content";
        final HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body, status);
    }
}
