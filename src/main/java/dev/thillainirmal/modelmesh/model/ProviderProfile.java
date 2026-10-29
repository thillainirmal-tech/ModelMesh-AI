package dev.thillainirmal.modelmesh.model;

import java.util.List;

public record ProviderProfile(
        String id,
        String name,
        String model,
        double inputCostPerMillion,
        double outputCostPerMillion,
        int p95LatencyMs,
        int quality,
        int maxContext,
        boolean vision,
        boolean healthy,
        List<String> regions
) {
}
