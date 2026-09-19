package com.healthcare.platform.service;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.mongo.UserDocument;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.repository.mongo.UserMongoRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
public class AuthTokenService {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenService.class);

    public static final String AUTH_COOKIE_NAME = "CAREPULSE_AUTH";
    public static final long TOKEN_VALIDITY_MS = 30L * 24 * 60 * 60 * 1000; // 30 days

    @Value("${app.auth.secret:CarePulseHealthcareSuperSecretKey2025ForSecureTokens!}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMongoRepository userMongoRepository;

    public String generateToken(String username) {
        if (username == null || username.isBlank()) return null;
        long expiresAt = System.currentTimeMillis() + TOKEN_VALIDITY_MS;
        String payload = username.trim() + ":" + expiresAt;
        String signature = computeHmac(payload);
        String combined = payload + ":" + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
    }

    public String validateAndExtractUsername(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(token.trim());
            String combined = new String(decoded, StandardCharsets.UTF_8);
            String[] parts = combined.split(":");
            if (parts.length < 3) return null;

            String username = parts[0];
            long expiresAt = Long.parseLong(parts[1]);
            String signature = parts[2];

            if (System.currentTimeMillis() > expiresAt) {
                log.debug("Auth token expired for user: {}", username);
                return null;
            }

            String expectedSig = computeHmac(username + ":" + expiresAt);
            if (!expectedSig.equals(signature)) {
                log.warn("Auth token signature mismatch for user: {}", username);
                return null;
            }

            return username;
        } catch (Exception e) {
            log.debug("Failed to validate auth token: {}", e.getMessage());
            return null;
        }
    }

    public void setAuthCookie(HttpServletResponse response, String username) {
        if (response == null || username == null || username.isBlank()) return;
        String token = generateToken(username);
        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMillis(TOKEN_VALIDITY_MS))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearAuthCookie(HttpServletResponse response) {
        if (response == null) return;
        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public User restoreSessionIfPresent(HttpServletRequest request, HttpSession session) {
        if (session != null && session.getAttribute("currentUser") != null) {
            return (User) session.getAttribute("currentUser");
        }
        if (request == null) return null;

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if (AUTH_COOKIE_NAME.equals(c.getName())) {
                String username = validateAndExtractUsername(c.getValue());
                if (username != null && !username.isBlank()) {
                    User jpaUser = userRepository.findByUsername(username)
                            .or(() -> userRepository.findByEmail(username))
                            .orElse(null);

                    UserDocument mongoUser = userMongoRepository.findByUsername(username)
                            .or(() -> userMongoRepository.findByEmail(username))
                            .orElse(null);

                    if (jpaUser == null && mongoUser != null) {
                        jpaUser = new User(mongoUser.getUsername(), mongoUser.getFullName(), mongoUser.getEmail(), mongoUser.getPhone(),
                                mongoUser.getRole() != null ? mongoUser.getRole() : "PATIENT");
                        jpaUser.setFirebaseUid(mongoUser.getFirebaseUid());
                        jpaUser.setBloodGroup(mongoUser.getBloodGroup());
                        jpaUser.setPhotoUrl(mongoUser.getPhotoUrl());
                        jpaUser.setPreferredCity(mongoUser.getCity());
                        jpaUser = userRepository.save(jpaUser);
                    } else if (jpaUser != null && mongoUser == null) {
                        String uid = jpaUser.getFirebaseUid() != null ? jpaUser.getFirebaseUid() : ("usr_" + System.currentTimeMillis());
                        mongoUser = new UserDocument(uid, jpaUser.getUsername(), jpaUser.getFullName(), jpaUser.getEmail(), jpaUser.getPhone());
                        mongoUser.setBloodGroup(jpaUser.getBloodGroup());
                        mongoUser.setPhotoUrl(jpaUser.getPhotoUrl());
                        mongoUser.setCity(jpaUser.getPreferredCity());
                        mongoUser = userMongoRepository.save(mongoUser);
                    }

                    if (jpaUser != null && session != null) {
                        session.setAttribute("currentUser", jpaUser);
                        if (mongoUser != null) {
                            session.setAttribute("mongoUser", mongoUser);
                            session.setAttribute("firebaseUid", mongoUser.getFirebaseUid());
                        }
                        String photo = (jpaUser.getPhotoUrl() != null && !jpaUser.getPhotoUrl().isBlank())
                                ? jpaUser.getPhotoUrl()
                                : (mongoUser != null ? mongoUser.getPhotoUrl() : null);
                        if (photo != null) {
                            session.setAttribute("photoUrl", photo);
                        }
                        log.info("Successfully rehydrated session from CAREPULSE_AUTH cookie for user: {}", jpaUser.getUsername());
                        return jpaUser;
                    }
                }
            }
        }
        return null;
    }

    private String computeHmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Could not compute HMAC signature", e);
        }
    }
}
