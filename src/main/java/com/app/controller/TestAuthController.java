package com.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()") // se debe tener el @EnableMethodSecurity en el SecurityConfig.class
// denyAll por defecto rechaza todas las peticiones
public class TestAuthController {

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('READ')")
    public String helloGet() {
        return "Hello World - GET";
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('READ') or hasAuthority('CREATE')")
    public String helloPost() {
        return "Hello World - POST";
    }

    @PutMapping("/put")
    public String helloPut() {
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloDelete() {
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    @PreAuthorize("hasAuthority('REFACTOR')")
    public String helloPatch() {
        return "Hello World - PATCH";
    }
}
