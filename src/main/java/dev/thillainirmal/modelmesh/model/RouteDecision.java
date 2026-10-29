package dev.thillainirmal.modelmesh.model;

import java.util.List;

public record RouteDecision(
        ProviderProfile selected,
        ProviderProfile fallback,
        int routeScore,
        double estimatedCostUsd,
        String reason,
        List<CandidateScore> candidates,
        List<String> policyTrace
) {
    public record CandidateScore(String provider, int score, boolean eligible, String note) {}
}
