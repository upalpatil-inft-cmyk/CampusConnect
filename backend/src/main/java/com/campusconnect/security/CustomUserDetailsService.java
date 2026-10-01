package com.campusconnect.security;

import com.campusconnect.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users){this.users=users;}

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user=users.findByEmail(email).orElseThrow(()->new UsernameNotFoundException(email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.isActive(), true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()))
        );
    }
}
