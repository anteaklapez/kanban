package com.hivetech.kanban.service;

import com.hivetech.kanban.dao.UserLoginDao;
import com.hivetech.kanban.dao.UserRegisterDao;
import com.hivetech.kanban.model.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    private final JwtUtil jwtUtils;



    public String authenticateUser(UserLoginDao user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails.getUsername());
    }

    public String registerUser(UserRegisterDao user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Error: Username is already taken!";
        }

        User newUser = new User(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                encoder.encode(user.getPassword())
        );

        userRepository.save(newUser);
        return "User registered successfully!";
    }
}
