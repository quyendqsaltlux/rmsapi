package saltlux.ctv.tranSS.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.User;
import saltlux.ctv.tranSS.payload.PasswordRequest;
import saltlux.ctv.tranSS.payload.UserIdentityAvailability;
import saltlux.ctv.tranSS.payload.UserPermission;
import saltlux.ctv.tranSS.payload.UserProfile;
import saltlux.ctv.tranSS.repository.user.UserRepository;
import saltlux.ctv.tranSS.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/checkEmailAvailability")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('USER')")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/permission/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserPermission getUserPermission(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        UserPermission permission = new UserPermission();
        BeanUtils.copyProperties(user, permission);

        return permission;
    }

    /**
     * @param profile profile
     * @return UserProfile
     */
    @PostMapping("/updateProfile")
    @PreAuthorize("hasRole('USER')")
    public UserProfile saveUserProfile(@RequestBody @Valid UserProfile profile) {
        return userService.updateProfile(profile);
    }

    /**
     * @param profile profile
     * @return UserProfile
     */
    @PostMapping("/updatePassword")
    @PreAuthorize("hasRole('USER')")
    public UserProfile updatePassword(@RequestBody @Valid PasswordRequest profile) {
        return userService.updatePassword(profile);
    }
}
