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

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        user.getRoles().forEach(roleEntity -> {

            SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(roleEntity.getName());
            for (var permission : roleEntity.getPermissions()) {
                SimpleGrantedAuthority permissionAuthority = new SimpleGrantedAuthority(permission.getName());
                authorities.add(permissionAuthority);
            }
            authorities.add(roleAuthority);
        });

        return User.builder()

                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)

                .build();


    }
}
