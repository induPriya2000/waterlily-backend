package com.example.waterlily.survey;

import com.example.waterlily.persistence.ResponseRepository;
import com.example.waterlily.persistence.User;
import com.example.waterlily.persistence.UserRepository;
import com.example.waterlily.survey.dto.SaveResponseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/responses") // final paths: /api/responses and /api/responses/me
public class ResponseController {

    private final ResponseRepository responses;
    private final UserRepository users;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResponseController(ResponseRepository responses, UserRepository users) {
        this.responses = responses;
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody SaveResponseRequest req, Authentication authn) throws Exception {
        if (authn == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = authn.getName();
        User u = users.findByEmail(email).orElse(null);
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Use the fully-qualified name to avoid clashing with org.springframework.http.ResponseEntity
        com.example.waterlily.persistence.ResponseEntity r =
                new com.example.waterlily.persistence.ResponseEntity();

        r.setUser(u);
        r.setAnswersJson(mapper.writeValueAsString(req.answers));
        r.setCreatedAt(Instant.now());
        r = responses.save(r);

        return ResponseEntity.ok(Map.of(
                "id", r.getId(),
                "createdAt", r.getCreatedAt(),
                "userId", u.getId()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> mine(Authentication authn) throws Exception {
        if (authn == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = authn.getName();
        User u = users.findByEmail(email).orElse(null);
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        List<com.example.waterlily.persistence.ResponseEntity> list =
                responses.findAllByUserIdOrderByCreatedAtDesc(u.getId());

        var out = list.stream().map(r -> {
            try {
                return Map.of(
                        "id", r.getId(),
                        "createdAt", r.getCreatedAt(),
                        "answers", mapper.readValue(r.getAnswersJson(), Map.class)
                );
            } catch (Exception e) {
                return Map.<String, Object>of(
                        "id", r.getId(),
                        "createdAt", r.getCreatedAt(),
                        "answersJson", r.getAnswersJson()
                );
            }
        }).toList();

        return ResponseEntity.ok(Map.of("items", out));
    }
}
