package com.example.waterlily.survey;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurveyController {

    // Public endpoint (permitted in SecurityConfig as "/survey")
    @GetMapping("/survey")
    public Map<String, Object> getSurvey() {
        return Map.of(
                "title", "Waterlily Intake Survey",
                "description", "Tell us about your demographics, health, and finances to estimate long-term care needs.",
                "questions", List.of(
                        Map.of(
                                "id", "age",
                                "section", "demographic",
                                "label", "Age",
                                "type", "number",
                                "required", true,
                                "help", "Enter your age in years."
                        ),
                        Map.of(
                                "id", "gender",
                                "section", "demographic",
                                "label", "Gender",
                                "type", "select",
                                "options", List.of("Female","Male","Non-binary","Prefer not to say"),
                                "required", true
                        ),
                        Map.of(
                                "id", "conditions",
                                "section", "health",
                                "label", "Current conditions",
                                "type", "multiselect",
                                "options", List.of("Diabetes","Hypertension","Arthritis","Dementia","None"),
                                "required", false
                        ),
                        Map.of(
                                "id", "adlLimitations",
                                "section", "health",
                                "label", "ADL limitations",
                                "type", "multiselect",
                                "options", List.of("Bathing","Dressing","Eating","Toileting","Transferring","Continence","None"),
                                "required", false
                        ),
                        Map.of(
                                "id", "income",
                                "section", "financial",
                                "label", "Annual household income (USD)",
                                "type", "number",
                                "required", true
                        ),
                        Map.of(
                                "id", "savings",
                                "section", "financial",
                                "label", "Liquid savings (USD)",
                                "type", "number",
                                "required", false
                        )
                )
        );
    }
}
