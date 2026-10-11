package com.chrisnkl.cfbackend.infrastructure.service.auth;

import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmailWithRolesAndPermissions(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            role.getPermissions().forEach(permission ->
                    authorities.add(
                            new SimpleGrantedAuthority(permission.getCode())
                    )
            );
        });

        return User.builder()

                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)

                .build();


    }
}
