package dev.thillainirmal.modelmesh.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RouteRequest(
        @NotBlank @Size(max = 4000) String prompt,
        Priority priority,
        Sensitivity sensitivity,
        boolean visionRequired,
        int expectedOutputTokens
) {
    public enum Priority { COST, BALANCED, QUALITY, SPEED }
    public enum Sensitivity { PUBLIC, INTERNAL, RESTRICTED }

    public RouteRequest {
        priority = priority == null ? Priority.BALANCED : priority;
        sensitivity = sensitivity == null ? Sensitivity.INTERNAL : sensitivity;
        expectedOutputTokens = expectedOutputTokens <= 0 ? 600 : Math.min(expectedOutputTokens, 8000);
    }
}
