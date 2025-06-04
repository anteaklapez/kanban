package com.hivetech.kanban.controller;

import com.hivetech.kanban.dao.UserLoginDao;
import com.hivetech.kanban.dao.UserRegisterDao;
import com.hivetech.kanban.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> login(@RequestBody UserLoginDao user){
        try {
            System.out.println(user.getEmail() + " " + user.getPassword());
            return ResponseEntity.ok(this.userService.authenticateUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> login(@RequestBody UserRegisterDao user){
        try {
            System.out.println(user.getEmail() + " " + user.getPassword());
            return ResponseEntity.ok(this.userService.registerUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
