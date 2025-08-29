package com.example.waterlily.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")  // no /api here
    public Map<String,Object> health() { return Map.of("ok", true); }


}
