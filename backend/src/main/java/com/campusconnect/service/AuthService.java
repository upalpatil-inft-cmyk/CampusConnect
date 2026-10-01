package com.campusconnect.service;

import com.campusconnect.dto.*;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager auth;
    private final UserRepository users;
    private final JwtService jwt;

    public AuthService(AuthenticationManager auth,UserRepository users,JwtService jwt){this.auth=auth;this.users=users;this.jwt=jwt;}

    public LoginResponse login(LoginRequest req){
        auth.authenticate(new UsernamePasswordAuthenticationToken(req.email(),req.password()));
        var user=users.findByEmail(req.email()).orElseThrow();
        return new LoginResponse(jwt.generate(user.getEmail(),user.getRole().name()),user.getRole().name(),user.getFullName());
    }
}
