package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.User;
import saltlux.ctv.tranSS.payload.PasswordRequest;
import saltlux.ctv.tranSS.payload.UserProfile;
import saltlux.ctv.tranSS.repository.user.UserRepository;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param username username
     * @return UserProfile
     */
    public UserProfile findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return convertToDto(user);
    }

    /**
     * @param profile profile
     * @return UserProfile
     */
    public UserProfile updateProfile(UserProfile profile) {
        User user = userRepository.findByUsername(profile.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", profile.getUsername()));

        user.setName(profile.getName());
        user.setEmail(profile.getEmail());
        user.setTel(profile.getTel());
        user.setAvatar(profile.getAvatar());

        return convertToDto(userRepository.save(user));
    }

    /**
     * @param profile profile
     * @return UserProfile
     */
    public UserProfile updatePassword(PasswordRequest profile) {
        User user = userRepository.findByUsername(profile.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", profile.getUsername()));
        String newPass = passwordEncoder.encode(profile.getPasswordNew());
        if (!passwordEncoder.matches(profile.getPassword(), user.getPassword())) {
            throw new BadRequestException("Password wrong");
        }
        user.setPassword(newPass);

        return convertToDto(userRepository.save(user));
    }

    /**
     * @param user user
     * @return UserProfile
     */
    private UserProfile convertToDto(User user) {
        return modelMapper.map(user, UserProfile.class);
    }
}