package com.app.service;

import com.app.controller.dto.AuthCreateUser;
import com.app.controller.dto.AuthLoginRequest;
import com.app.controller.dto.AuthResponse;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.RoleRepository;
import com.app.persistence.repository.UserRepository;
import com.app.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        /* Mapeo para Spring Security */
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoleEntities()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoleEntities().stream()
                .flatMap(role -> role.getPermissionEntities().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnable(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList
        );
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User loged successfuly", accessToken, true);

        return authResponse;
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUser authCreateRoleRequest) {
        String username = authCreateRoleRequest.username();
        String password = authCreateRoleRequest.password();
        List<String> roleRequest = authCreateRoleRequest.roleRequest().roleListName();

        Set<RoleEntity> roleEntitySet = new HashSet<>(roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest));

        if (roleEntitySet.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        UserEntity userEntity = UserEntity.builder().username(username).password(passwordEncoder.encode(password)).roleEntities(roleEntitySet).isEnable(true).accountNoLocked(true).accountNoExpired(true).credentialNoExpired(true).build();

        UserEntity userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userCreated.getRoleEntities()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userCreated.getRoleEntities()
                .stream()
                .flatMap(role -> role.getPermissionEntities().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userCreated.getUsername(),
                userCreated.getPassword(),
                authorityList);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(
                userCreated.getUsername(),
                "User created successfuly",
                accessToken,
                true);

        return authResponse;
    }
}
