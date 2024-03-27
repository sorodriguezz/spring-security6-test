package com.app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication) {
        // encripta la key con un algoritmo criptografico
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();
        // permisos a declarar, debe ir como "CREATE,UPDATE,DELETE"
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority) // .map(grantAuthoritie -> granAuthoritie.getAuthority())
                .collect(Collectors.joining(",")); // "CREATE,UPDATE,DELETE"
        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator) // usuario que genera el token
                .withSubject(username) // usuario que se autentica
                .withClaim("authorities", authorities) // permisos en claims de jwt
                .withIssuedAt(new Date()) // fecha en que se genera el token
                .withExpiresAt(new Date(System.currentTimeMillis() + 1_800_000)) // tiempo de duracion del token en milisegundos, en este caso es media hora
                .withJWTId(UUID.randomUUID().toString()) // identificador unico del token
                .withNotBefore(new Date(System.currentTimeMillis())) // desde que momento se considera token valido en MS
                .sign(algorithm); // firma con algoritmo de encriptacion
        return jwtToken;
    }


    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator) // usuario que genero token
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        } catch (JWTVerificationException exception) { // si el token es invalido cae en el catch
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject().toString();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
}
