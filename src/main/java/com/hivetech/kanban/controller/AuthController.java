package com.hivetech.kanban.controller;

import com.hivetech.kanban.dto.AuthResponseDTO;
import com.hivetech.kanban.dto.LoginRequestDTO;
import com.hivetech.kanban.dto.RegisterRequestDTO;
import com.hivetech.kanban.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO user){
        return ResponseEntity.ok(this.userService.authenticateUser(user));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO user){
        return ResponseEntity.ok(this.userService.registerUser(user));
    }
}
