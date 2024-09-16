package com.example.demo.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("registerAdmin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.registerAdmin(request));
    }

    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));

    }
}
