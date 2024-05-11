package PFE.Gestion_Des_Anonces.Api.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_DURATION;

    public String extractEmail(String Token){
        return extractClaim(Token , Claims::getSubject);
    }

    public String generateToken(UserDetails user){
        return generateToken(new HashMap<>(), user);
    }

    public boolean isTokenValid(String token , UserDetails user){
        final String  email = extractEmail(token);
        final String username = user.getUsername();
        return ( email.equals(username) ) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date Expiration = extractClaim(token , Claims::getExpiration);
        return Expiration.before(new Date(System.currentTimeMillis()));
    }


    public String generateToken(Map<String , Object> extraClaims , UserDetails user){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_DURATION))
                .signWith(getSignInKey() , SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String Token , Function<Claims , T> ClaimResolver){
        final Claims claims = extractAllClaim(Token);
        return ClaimResolver.apply(claims);
    }
    public Claims extractAllClaim(String Token){
        return   Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(Token)
                .getBody();
    }

    private Key getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
