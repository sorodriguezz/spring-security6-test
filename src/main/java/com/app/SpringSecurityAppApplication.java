package com.app;

import com.app.persistence.entity.PermissionEntity;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityAppApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            /* CREATE PERMISSIONS */
            PermissionEntity createPermission = PermissionEntity.builder()
                    .name("CREATE")
                    .build();
            PermissionEntity readPermission = PermissionEntity.builder()
                    .name("READ")
                    .build();
            PermissionEntity updatePermission = PermissionEntity.builder()
                    .name("UPDATE")
                    .build();
            PermissionEntity deletePermission = PermissionEntity.builder()
                    .name("DELETE")
                    .build();
            PermissionEntity refactorPermission = PermissionEntity.builder()
                    .name("REFACTOR")
                    .build();

            /* CREATE ROLES */
            RoleEntity roleAdmin = RoleEntity
                    .builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionEntities(Set.of(createPermission, readPermission, updatePermission, deletePermission))
                    .build();
            RoleEntity roleUser = RoleEntity
                    .builder()
                    .roleEnum(RoleEnum.USER)
                    .permissionEntities(Set.of(createPermission, readPermission))
                    .build();
            RoleEntity roleInvited = RoleEntity
                    .builder()
                    .roleEnum(RoleEnum.INVITED)
                    .permissionEntities(Set.of(readPermission))
                    .build();
            RoleEntity roleDeveloper = RoleEntity
                    .builder()
                    .roleEnum(RoleEnum.DEVELOPER)
                    .permissionEntities(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
                    .build();
            /* CREATE USERS*/
            UserEntity userSorodriguezz = UserEntity
                    .builder()
                    .username("sorodriguezz")
                    .password("1234")
                    .isEnable(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roleEntities(Set.of(roleAdmin))
                    .build();
            UserEntity userTest = UserEntity
                    .builder()
                    .username("usertest")
                    .password("12345")
                    .isEnable(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roleEntities(Set.of(roleUser))
                    .build();
            UserEntity userInvited = UserEntity
                    .builder()
                    .username("userinvited")
                    .password("123")
                    .isEnable(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roleEntities(Set.of(roleInvited))
                    .build();
            UserEntity userDev = UserEntity
                    .builder()
                    .username("userdev")
                    .password("123456")
                    .isEnable(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roleEntities(Set.of(roleDeveloper))
                    .build();

            userRepository.saveAll(List.of(userSorodriguezz, userDev, userInvited, userTest));
        };
    }
}
