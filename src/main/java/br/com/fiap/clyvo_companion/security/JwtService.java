package br.com.fiap.clyvo_companion.security;

import br.com.fiap.clyvo_companion.exception.UnauthorizedException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    static final String TIPO_ACCESS = "access";
    static final String TIPO_REFRESH = "refresh";

    private final byte[] secret;
    private final long accessTokenMinutes;
    private final long refreshTokenDays;
    private final MACSigner signer;
    private final MACVerifier verifier;

    public JwtService(JwtProperties properties) {
        this.secret = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.accessTokenMinutes = properties.getAccessTokenMinutes();
        this.refreshTokenDays = properties.getRefreshTokenDays();
        if (secret.length < 32) {
            throw new IllegalStateException("app.security.jwt.secret deve ter pelo menos 32 bytes");
        }
        if (accessTokenMinutes <= 0 || refreshTokenDays <= 0) {
            throw new IllegalStateException("Os prazos do JWT devem ser maiores que zero");
        }
        try {
            this.signer = new MACSigner(secret);
            this.verifier = new MACVerifier(secret);
        } catch (JOSEException ex) {
            throw new IllegalStateException("Não foi possível inicializar a chave JWT", ex);
        }
    }

    public String gerarAccessToken(UsuarioDetails usuario) {
        Instant expiraEm = Instant.now().plus(accessTokenMinutes, ChronoUnit.MINUTES);
        return gerar(usuario.getUsername(), TIPO_ACCESS, expiraEm);
    }

    public String gerarRefreshToken(UsuarioDetails usuario) {
        Instant expiraEm = Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS);
        return gerar(usuario.getUsername(), TIPO_REFRESH, expiraEm);
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenMinutes * 60;
    }

    public String extrairEmailAccess(String token) {
        return validar(token, TIPO_ACCESS).getSubject();
    }

    public String extrairEmailRefresh(String token) {
        return validar(token, TIPO_REFRESH).getSubject();
    }

    private String gerar(String email, String tipo, Instant expiraEm) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email)
                .jwtID(UUID.randomUUID().toString())
                .claim("tipo", tipo)
                .issueTime(new Date())
                .expirationTime(Date.from(expiraEm))
                .build();
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException("Não foi possível gerar o token", ex);
        }
    }

    private JWTClaimsSet validar(String token, String tipoEsperado) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier)) {
                throw new UnauthorizedException("Token inválido");
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date expiracao = claims.getExpirationTime();
            if (expiracao == null || !expiracao.toInstant().isAfter(Instant.now())) {
                throw new UnauthorizedException("Token expirado");
            }
            if (!tipoEsperado.equals(claims.getStringClaim("tipo"))) {
                throw new UnauthorizedException("Tipo de token inválido");
            }
            if (claims.getSubject() == null || claims.getSubject().isBlank()) {
                throw new UnauthorizedException("Token inválido");
            }
            return claims;
        } catch (UnauthorizedException ex) {
            throw ex;
        } catch (ParseException | JOSEException ex) {
            throw new UnauthorizedException("Token inválido");
        }
    }
}
