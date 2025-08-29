package com.example.waterlily.survey.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class SaveResponseRequest {
    @NotNull
    public Map<String, Object> answers; // key: question id, value: user answer
}
