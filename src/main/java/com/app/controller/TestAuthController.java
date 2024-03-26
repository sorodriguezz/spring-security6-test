package com.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()") // se debe tener el @EnableMethodSecurity en el SecurityConfig.class
// denyAll por defecto rechaza todas las peticiones
public class TestAuthController {

    @GetMapping("/hello")
    @PreAuthorize("permitAll()") // permite a todos ingresas
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/hello-secured")
    @PreAuthorize("hasAnyAuthority('READ')") // solo pueden ingresar permisos "READ" o se puede especificar otro
    public String helloSecured() {
        return "Hello World Secured";
    }

    @GetMapping("/hello-secured2")
    @PreAuthorize("hasAnyAuthority('READ')")
    public String helloSecured2() {
        return "Hello World Secured";
    }
}
