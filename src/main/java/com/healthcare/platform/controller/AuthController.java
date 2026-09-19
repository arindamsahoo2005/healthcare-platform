package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.mongo.UserDocument;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.repository.mongo.UserMongoRepository;
import com.healthcare.platform.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserMongoRepository userMongoRepository;
    @Autowired private AuthTokenService authTokenService;

    @PostMapping("/firebase-login")
    public ResponseEntity<?> firebaseLogin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String uid,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String photoUrl,
            @RequestParam(required = false) String phone,
            @RequestBody(required = false) Map<String, Object> body,
            HttpSession session,
            HttpServletResponse response) {

        String effectiveUid = (uid != null && !uid.isBlank()) ? uid : (body != null && body.get("uid") != null ? body.get("uid").toString() : null);
        String effectiveEmail = (email != null && !email.isBlank()) ? email : (body != null && body.get("email") != null ? body.get("email").toString() : null);
        String effectiveName = (displayName != null && !displayName.isBlank()) ? displayName : (body != null && body.get("displayName") != null ? body.get("displayName").toString() : null);
        String effectivePhoto = (photoUrl != null && !photoUrl.isBlank()) ? photoUrl : (body != null && body.get("photoUrl") != null ? body.get("photoUrl").toString() : null);
        String effectivePhone = (phone != null && !phone.isBlank()) ? phone : (body != null && body.get("phone") != null ? body.get("phone").toString() : null);

        // If an Authorization Bearer token is passed, parse standard JWT claims in pure Java
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                try {
                    byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
                    String json = new String(decoded, StandardCharsets.UTF_8);
                    if (effectiveUid == null || effectiveUid.isBlank()) {
                        effectiveUid = extractJsonField(json, "sub", "user_id");
                    }
                    if (effectiveEmail == null || effectiveEmail.isBlank()) {
                        effectiveEmail = extractJsonField(json, "email");
                    }
                    if (effectiveName == null || effectiveName.isBlank()) {
                        effectiveName = extractJsonField(json, "name");
                    }
                    if (effectivePhoto == null || effectivePhoto.isBlank()) {
                        effectivePhoto = extractJsonField(json, "picture");
                    }
                } catch (Exception ignored) {}
            }
        }

        if (effectiveUid == null || effectiveUid.isBlank()) {
            if (effectiveEmail != null && !effectiveEmail.isBlank()) {
                effectiveUid = "google_" + Base64.getEncoder().encodeToString(effectiveEmail.getBytes(StandardCharsets.UTF_8)).replace("=", "");
            } else {
                effectiveUid = "usr_" + System.currentTimeMillis();
            }
        }

        String username = (effectiveEmail != null && !effectiveEmail.isBlank()) ? effectiveEmail : "fb_" + effectiveUid;
        String name = (effectiveName != null && !effectiveName.isBlank()) ? effectiveName : ((effectiveEmail != null && effectiveEmail.contains("@")) ? effectiveEmail.split("@")[0] : "Universal Patient");

        // 1. Persist in MongoDB collection 'users'
        final String finalUid = effectiveUid;
        final String finalEmail = effectiveEmail;
        UserDocument mongoUser = userMongoRepository.findByFirebaseUid(finalUid)
                .or(() -> (finalEmail != null && !finalEmail.isBlank()) ? userMongoRepository.findByEmail(finalEmail) : Optional.empty())
                .orElseGet(() -> new UserDocument(finalUid, username, name, finalEmail != null ? finalEmail : "", phone != null ? phone : ""));

        mongoUser.setFirebaseUid(finalUid);
        mongoUser.setUsername(username);
        mongoUser.setFullName(name);
        if (finalEmail != null && !finalEmail.isBlank()) mongoUser.setEmail(finalEmail);
        if (phone != null && !phone.isBlank()) mongoUser.setPhone(phone);
        if (effectivePhoto != null && !effectivePhoto.isBlank()) mongoUser.setPhotoUrl(effectivePhoto);
        mongoUser.setUpdatedAt(LocalDateTime.now());
        mongoUser = userMongoRepository.save(mongoUser);

        // 2. Synchronize JPA user entity
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new User(username, name, finalEmail != null ? finalEmail : "", phone != null ? phone : "", "PATIENT");
            user.setFirebaseUid(finalUid);
        } else {
            user.setFirebaseUid(finalUid);
            user.setFullName(name);
            if (phone != null && !phone.isBlank()) user.setPhone(phone);
        }
        user.setBloodGroup(mongoUser.getBloodGroup());
        user.setEmergencyContactName(mongoUser.getEmergencyContactName());
        user.setEmergencyContactPhone(mongoUser.getEmergencyContactPhone());
        user.setAllergies(mongoUser.getAllergies());
        user.setChronicConditions(mongoUser.getChronicConditions());
        user.setPreferredCity(mongoUser.getCity());
        user = userRepository.save(user);

        session.setAttribute("currentUser", user);
        session.setAttribute("mongoUser", mongoUser);
        session.setAttribute("firebaseUid", finalUid);
        session.setAttribute("photoUrl", effectivePhoto != null ? effectivePhoto : mongoUser.getPhotoUrl());

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("success", true);
        resp.put("uid", finalUid);
        resp.put("mongoId", mongoUser.getId());
        resp.put("name", user.getFullName());
        resp.put("email", user.getEmail());
        resp.put("role", user.getRole());
        resp.put("state", mongoUser.getState());
        resp.put("city", mongoUser.getCity());

        authTokenService.setAuthCookie(response, user.getUsername());
        return ResponseEntity.ok(resp);
    }

    private String extractJsonField(String json, String... fieldNames) {
        for (String field : fieldNames) {
            String pattern = "\"" + field + "\":\"";
            int idx = json.indexOf(pattern);
            if (idx != -1) {
                int start = idx + pattern.length();
                int end = json.indexOf("\"", start);
                if (end != -1) {
                    return json.substring(start, end);
                }
            }
        }
        return null;
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(
            @RequestParam String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String emergencyContactName,
            @RequestParam(required = false) String emergencyContactPhone,
            @RequestParam(required = false) String allergies,
            @RequestParam(required = false) String chronicConditions,
            @RequestParam(defaultValue = "West Bengal") String state,
            @RequestParam(defaultValue = "Kolkata") String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String photoUrl,
            HttpSession session) {

        String uid = (String) session.getAttribute("firebaseUid");
        User currentUser = (User) session.getAttribute("currentUser");

        UserDocument mongoUser = null;
        if (uid != null && !uid.isBlank()) {
            mongoUser = userMongoRepository.findByFirebaseUid(uid).orElse(null);
        }
        if (mongoUser == null && currentUser != null && currentUser.getEmail() != null) {
            mongoUser = userMongoRepository.findByEmail(currentUser.getEmail()).orElse(null);
        }
        if (mongoUser == null) {
            String generatedUid = uid != null ? uid : ("usr_" + System.currentTimeMillis());
            mongoUser = new UserDocument(generatedUid, email != null ? email : "patient", fullName, email != null ? email : "", phone != null ? phone : "");
        }

        mongoUser.setFullName(fullName);
        if (email != null) mongoUser.setEmail(email);
        if (phone != null) mongoUser.setPhone(phone);
        mongoUser.setBloodGroup(bloodGroup);
        mongoUser.setEmergencyContactName(emergencyContactName);
        mongoUser.setEmergencyContactPhone(emergencyContactPhone);
        mongoUser.setAllergies(allergies);
        mongoUser.setChronicConditions(chronicConditions);
        mongoUser.setState(state);
        mongoUser.setCity(city);
        mongoUser.setLocality(locality);
        mongoUser.setAddress(address);
        if (photoUrl != null && !photoUrl.isBlank()) {
            mongoUser.setPhotoUrl(photoUrl);
        }
        mongoUser.setUpdatedAt(LocalDateTime.now());
        mongoUser = userMongoRepository.save(mongoUser);

        if (currentUser != null) {
            currentUser.setFullName(fullName);
            if (email != null) currentUser.setEmail(email);
            if (phone != null) currentUser.setPhone(phone);
            currentUser.setBloodGroup(bloodGroup);
            currentUser.setEmergencyContactName(emergencyContactName);
            currentUser.setEmergencyContactPhone(emergencyContactPhone);
            currentUser.setAllergies(allergies);
            currentUser.setChronicConditions(chronicConditions);
            currentUser.setPreferredCity(city);
            if (photoUrl != null && !photoUrl.isBlank()) {
                currentUser.setPhotoUrl(photoUrl);
            }
            userRepository.save(currentUser);
        }

        session.setAttribute("mongoUser", mongoUser);
        session.setAttribute("currentUser", currentUser);
        if (photoUrl != null && !photoUrl.isBlank()) {
            session.setAttribute("photoUrl", photoUrl);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("mongoId", mongoUser.getId());
        resp.put("photoUrl", mongoUser.getPhotoUrl());
        resp.put("message", "Profile successfully synchronized with MongoDB collection 'users'");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/profile/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam String photoUrl, HttpSession session) {
        if (photoUrl == null || photoUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Avatar URL cannot be empty"));
        }

        String uid = (String) session.getAttribute("firebaseUid");
        User currentUser = (User) session.getAttribute("currentUser");
        UserDocument mongoUser = null;

        if (uid != null && !uid.isBlank()) {
            mongoUser = userMongoRepository.findByFirebaseUid(uid).orElse(null);
        }
        if (mongoUser == null && currentUser != null && currentUser.getEmail() != null) {
            mongoUser = userMongoRepository.findByEmail(currentUser.getEmail()).orElse(null);
        }
        if (mongoUser == null && currentUser != null) {
            mongoUser = userMongoRepository.findByUsername(currentUser.getUsername()).orElse(null);
        }
        if (mongoUser == null) {
            String genUid = (currentUser != null && currentUser.getFirebaseUid() != null) ? currentUser.getFirebaseUid() : ("usr_" + System.currentTimeMillis());
            mongoUser = new UserDocument(genUid, currentUser != null ? currentUser.getUsername() : "user", currentUser != null ? currentUser.getFullName() : "User", currentUser != null ? currentUser.getEmail() : "", currentUser != null ? currentUser.getPhone() : "");
        }

        mongoUser.setPhotoUrl(photoUrl);
        mongoUser.setUpdatedAt(LocalDateTime.now());
        mongoUser = userMongoRepository.save(mongoUser);

        if (currentUser != null) {
            currentUser.setPhotoUrl(photoUrl);
            userRepository.save(currentUser);
            session.setAttribute("currentUser", currentUser);
        }
        session.setAttribute("mongoUser", mongoUser);
        session.setAttribute("photoUrl", photoUrl);

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("photoUrl", photoUrl);
        resp.put("message", "Profile icon successfully updated and synchronized!");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("currentUser");
        UserDocument mongoUser = (UserDocument) session.getAttribute("mongoUser");
        if (user == null && mongoUser == null && request != null) {
            user = authTokenService.restoreSessionIfPresent(request, session);
            mongoUser = (UserDocument) session.getAttribute("mongoUser");
        }
        if (user == null && mongoUser == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("authenticated", true);
        resp.put("id", user != null ? user.getId() : null);
        resp.put("mongoId", mongoUser != null ? mongoUser.getId() : null);
        resp.put("username", user != null ? user.getUsername() : mongoUser.getUsername());
        resp.put("fullName", mongoUser != null ? mongoUser.getFullName() : user.getFullName());
        resp.put("email", mongoUser != null ? mongoUser.getEmail() : user.getEmail());
        resp.put("phone", mongoUser != null ? mongoUser.getPhone() : user.getPhone());
        resp.put("bloodGroup", mongoUser != null ? mongoUser.getBloodGroup() : user.getBloodGroup());
        resp.put("state", mongoUser != null ? mongoUser.getState() : "West Bengal");
        resp.put("city", mongoUser != null ? mongoUser.getCity() : "Kolkata");
        resp.put("locality", mongoUser != null ? mongoUser.getLocality() : null);
        resp.put("role", user != null ? user.getRole() : "PATIENT");
        resp.put("photoUrl", session.getAttribute("photoUrl"));
        resp.put("firebaseUid", session.getAttribute("firebaseUid"));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false, defaultValue = "Kolkata") String city,
            @RequestParam(required = false, defaultValue = "West Bengal") String state,
            @RequestBody(required = false) Map<String, Object> body,
            HttpSession session,
            HttpServletResponse response) {

        String effectiveName = (fullName != null && !fullName.isBlank()) ? fullName.trim() : (body != null && body.get("fullName") != null ? body.get("fullName").toString().trim() : null);
        String effectiveEmail = (email != null && !email.isBlank()) ? email.trim() : (body != null && body.get("email") != null ? body.get("email").toString().trim() : null);
        String effectivePassword = (password != null && !password.isBlank()) ? password.trim() : (body != null && body.get("password") != null ? body.get("password").toString().trim() : null);
        String effectivePhone = (phone != null && !phone.isBlank()) ? phone.trim() : (body != null && body.get("phone") != null ? body.get("phone").toString().trim() : "");
        String effectiveCity = (city != null && !city.isBlank()) ? city.trim() : (body != null && body.get("city") != null ? body.get("city").toString().trim() : "Kolkata");
        String effectiveState = (state != null && !state.isBlank()) ? state.trim() : (body != null && body.get("state") != null ? body.get("state").toString().trim() : "West Bengal");

        if (effectiveName == null || effectiveName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "status", "ERROR", "message", "Full Name is required."));
        }
        if (effectiveEmail == null || effectiveEmail.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "status", "ERROR", "message", "Email address is required."));
        }
        if (effectivePassword == null || effectivePassword.isBlank() || effectivePassword.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "status", "ERROR", "message", "Password must be at least 4 characters."));
        }

        // Check if user already exists
        Optional<UserDocument> existingMongo = userMongoRepository.findByEmail(effectiveEmail)
                .or(() -> userMongoRepository.findByUsername(effectiveEmail));
        if (existingMongo.isPresent()) {
            UserDocument existing = existingMongo.get();
            if (existing.getPassword() != null && !existing.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "status", "EXISTS", "message", "An account with email " + effectiveEmail + " already exists. Please log in."));
            }
            // If existing user has no password (e.g. registered via Google previously), set password and activate
            existing.setPassword(effectivePassword);
            existing.setFullName(effectiveName);
            if (!effectivePhone.isBlank()) existing.setPhone(effectivePhone);
            existing.setCity(effectiveCity);
            existing.setState(effectiveState);
            existing.setUpdatedAt(LocalDateTime.now());
            userMongoRepository.save(existing);

            User user = userRepository.findByUsername(existing.getUsername())
                    .or(() -> userRepository.findByEmail(effectiveEmail))
                    .orElseGet(() -> new User(effectiveEmail, effectiveName, effectiveEmail, effectivePhone, "PATIENT"));
            user.setPassword(effectivePassword);
            user.setFullName(effectiveName);
            user = userRepository.save(user);

            session.setAttribute("currentUser", user);
            session.setAttribute("mongoUser", existing);
            session.setAttribute("firebaseUid", existing.getFirebaseUid());

            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "SUCCESS");
            resp.put("success", true);
            resp.put("message", "Account registered and linked successfully!");
            resp.put("name", user.getFullName());
            resp.put("email", user.getEmail());
            resp.put("role", user.getRole());
            resp.put("uid", existing.getFirebaseUid());
            authTokenService.setAuthCookie(response, user.getUsername());
            return ResponseEntity.ok(resp);
        }

        String uid = "usr_" + System.currentTimeMillis();
        UserDocument mongoUser = new UserDocument(uid, effectiveEmail, effectiveName, effectiveEmail, effectivePhone);
        mongoUser.setPassword(effectivePassword);
        mongoUser.setCity(effectiveCity);
        mongoUser.setState(effectiveState);
        mongoUser.setCreatedAt(LocalDateTime.now());
        mongoUser.setUpdatedAt(LocalDateTime.now());
        mongoUser = userMongoRepository.save(mongoUser);

        User user = new User(effectiveEmail, effectiveName, effectiveEmail, effectivePhone, "PATIENT");
        user.setPassword(effectivePassword);
        user.setFirebaseUid(uid);
        user.setPreferredCity(effectiveCity);
        user = userRepository.save(user);

        session.setAttribute("currentUser", user);
        session.setAttribute("mongoUser", mongoUser);
        session.setAttribute("firebaseUid", uid);

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("success", true);
        resp.put("message", "Welcome, " + effectiveName + "! Your account has been registered successfully.");
        resp.put("name", user.getFullName());
        resp.put("email", user.getEmail());
        resp.put("role", user.getRole());
        resp.put("uid", uid);
        authTokenService.setAuthCookie(response, user.getUsername());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestBody(required = false) Map<String, Object> body,
            HttpSession session,
            HttpServletResponse response) {

        String effectiveLogin = (email != null && !email.isBlank()) ? email.trim() : 
                                (username != null && !username.isBlank() ? username.trim() : 
                                (body != null && body.get("email") != null ? body.get("email").toString().trim() : 
                                (body != null && body.get("username") != null ? body.get("username").toString().trim() : 
                                (body != null && body.get("emailOrUsername") != null ? body.get("emailOrUsername").toString().trim() : null))));
        String effectivePassword = (password != null && !password.isBlank()) ? password.trim() : (body != null && body.get("password") != null ? body.get("password").toString().trim() : null);

        if (effectiveLogin == null || effectiveLogin.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "status", "ERROR", "message", "Email or Username is required."));
        }
        if (effectivePassword == null || effectivePassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "status", "ERROR", "message", "Password is required."));
        }

        // Find user by email or username
        Optional<UserDocument> mongoUserOpt = userMongoRepository.findByEmail(effectiveLogin)
                .or(() -> userMongoRepository.findByUsername(effectiveLogin));

        UserDocument mongoUser = mongoUserOpt.orElse(null);
        User jpaUser = userRepository.findByEmail(effectiveLogin)
                .or(() -> userRepository.findByUsername(effectiveLogin))
                .orElse(null);

        if (mongoUser == null && jpaUser == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "status", "NOT_FOUND", "message", "No registered account found with " + effectiveLogin + ". Please click 'Create Account' to register."));
        }

        String storedPassword = mongoUser != null && mongoUser.getPassword() != null ? mongoUser.getPassword() : (jpaUser != null ? jpaUser.getPassword() : null);

        if (storedPassword != null && !storedPassword.isBlank()) {
            if (!storedPassword.equals(effectivePassword)) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "status", "BAD_CREDENTIALS", "message", "Incorrect password. Please try again."));
            }
        } else {
            // First time password set for existing account
            if (mongoUser != null) {
                mongoUser.setPassword(effectivePassword);
                userMongoRepository.save(mongoUser);
            }
            if (jpaUser != null) {
                jpaUser.setPassword(effectivePassword);
                userRepository.save(jpaUser);
            }
        }

        // Sync JPA user if needed
        if (jpaUser == null && mongoUser != null) {
            jpaUser = new User(mongoUser.getUsername(), mongoUser.getFullName(), mongoUser.getEmail(), mongoUser.getPhone(), mongoUser.getRole());
            jpaUser.setFirebaseUid(mongoUser.getFirebaseUid());
            jpaUser.setPassword(effectivePassword);
            jpaUser = userRepository.save(jpaUser);
        } else if (mongoUser == null && jpaUser != null) {
            String uid = jpaUser.getFirebaseUid() != null ? jpaUser.getFirebaseUid() : ("usr_" + System.currentTimeMillis());
            mongoUser = new UserDocument(uid, jpaUser.getUsername(), jpaUser.getFullName(), jpaUser.getEmail(), jpaUser.getPhone());
            mongoUser.setPassword(effectivePassword);
            mongoUser = userMongoRepository.save(mongoUser);
        }

        session.setAttribute("currentUser", jpaUser);
        session.setAttribute("mongoUser", mongoUser);
        session.setAttribute("firebaseUid", mongoUser.getFirebaseUid());

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("success", true);
        resp.put("message", "Welcome back, " + jpaUser.getFullName() + "!");
        resp.put("name", jpaUser.getFullName());
        resp.put("email", jpaUser.getEmail());
        resp.put("role", jpaUser.getRole());
        resp.put("uid", mongoUser.getFirebaseUid());

        authTokenService.setAuthCookie(response, jpaUser.getUsername());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletResponse response) {
        authTokenService.clearAuthCookie(response);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("status", "LOGGED_OUT"));
    }
}