package br.com.fiap.clyvo_companion.security;

import br.com.fiap.clyvo_companion.exception.UnauthorizedException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private final JwtService jwtService = jwtService();

    @Test
    void accessTokenCarregaOEmail() {
        UsuarioDetails usuario = usuario();

        String token = jwtService.gerarAccessToken(usuario);

        assertEquals("maria@email.com", jwtService.extrairEmailAccess(token));
    }

    @Test
    void tokenExpiradoEhRecusado() throws Exception {
        String token = tokenComExpiracao(Instant.now().minusSeconds(60), JwtService.TIPO_ACCESS);

        assertThrows(UnauthorizedException.class, () -> jwtService.extrairEmailAccess(token));
    }

    @Test
    void refreshTokenNaoValeComoAccess() {
        String refresh = jwtService.gerarRefreshToken(usuario());

        assertThrows(UnauthorizedException.class, () -> jwtService.extrairEmailAccess(refresh));
        assertEquals("maria@email.com", jwtService.extrairEmailRefresh(refresh));
    }

    private static String tokenComExpiracao(Instant expiraEm, String tipo) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("maria@email.com")
                .claim("tipo", tipo)
                .expirationTime(Date.from(expiraEm))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner("clyvo-companion-dev-secret-key-32b!".getBytes(StandardCharsets.UTF_8)));
        return jwt.serialize();
    }

    private static JwtService jwtService() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("clyvo-companion-dev-secret-key-32b!");
        properties.setAccessTokenMinutes(30);
        properties.setRefreshTokenDays(7);
        return new JwtService(properties);
    }

    private static UsuarioDetails usuario() {
        return new UsuarioDetails(1L, "Maria Silva", "maria@email.com", "hash", "TUTOR");
    }
}
