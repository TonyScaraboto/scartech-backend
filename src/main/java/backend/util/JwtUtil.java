package backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.logging.Logger;

public class JwtUtil {
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());
    
    // Chave secreta para assinar tokens (em produção, usar variável de ambiente)
    private static final String SECRET_KEY = "scartech-secret-key-min-256-bits-for-hs256-algorithm-safe-production";
    
    // Tempo de expiração: 7 dias em milissegundos
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Gera um JWT token para um usuário
     */
    public static String generateToken(String userId, String email) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

            String token = Jwts.builder()
                    .subject(userId)
                    .claim("email", email)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            logger.info("Token gerado para usuário: " + email);
            return token;
        } catch (Exception e) {
            logger.severe("Erro ao gerar token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Valida um token JWT
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            logger.warning("Token inválido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrai o userId de um token
     */
    public static String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            logger.warning("Erro ao extrair userId: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrai o email de um token
     */
    public static String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return (String) claims.get("email");
        } catch (Exception e) {
            logger.warning("Erro ao extrair email: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retorna o tempo de expiração do token em segundos
     */
    public static long getExpirationTimeSeconds() {
        return EXPIRATION_TIME / 1000;
    }
}
