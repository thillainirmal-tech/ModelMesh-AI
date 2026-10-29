package dev.thillainirmal.modelmesh.service;

import dev.thillainirmal.modelmesh.model.ProviderProfile;
import dev.thillainirmal.modelmesh.model.RouteDecision;
import dev.thillainirmal.modelmesh.model.RouteRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ModelMeshRouter {
    private final List<ProviderProfile> providers = List.of(
            new ProviderProfile("openai", "OpenAI", "GPT-5", 1.25, 10.0, 780, 96, 400_000, true, true, List.of("US", "EU")),
            new ProviderProfile("gemini", "Google Gemini", "Gemini 2.5 Pro", 1.25, 10.0, 620, 93, 1_000_000, true, true, List.of("US", "EU", "APAC")),
            new ProviderProfile("anthropic", "Anthropic", "Claude Sonnet 4", 3.0, 15.0, 840, 95, 200_000, true, true, List.of("US", "EU")),
            new ProviderProfile("local", "Private Runtime", "Llama 3.3 70B", 0.0, 0.0, 1450, 82, 128_000, false, true, List.of("ON_PREM"))
    );

    public List<ProviderProfile> providers() {
        return providers;
    }

    public RouteDecision route(RouteRequest request) {
        List<String> trace = new ArrayList<>();
        trace.add("Classified sensitivity as " + request.sensitivity());
        trace.add("Applied " + request.priority() + " optimization weights");
        if (request.visionRequired()) trace.add("Required a vision-capable model");

        List<Scored> scored = providers.stream().map(provider -> score(provider, request))
                .sorted(Comparator.comparingInt(Scored::score).reversed()).toList();
        List<Scored> eligible = scored.stream().filter(Scored::eligible).toList();
        if (eligible.isEmpty()) throw new IllegalArgumentException("No provider satisfies the active policy");
        ProviderProfile selected = eligible.get(0).provider();
        ProviderProfile fallback = eligible.size() > 1 ? eligible.get(1).provider() : selected;
        trace.add("Selected " + selected.name() + "; fallback is " + fallback.name());

        double promptTokens = Math.max(50, request.prompt().length() / 4.0);
        double cost = promptTokens / 1_000_000 * selected.inputCostPerMillion()
                + request.expectedOutputTokens() / 1_000_000.0 * selected.outputCostPerMillion();
        String reason = switch (request.priority()) {
            case COST -> "Lowest policy-compliant estimated cost";
            case SPEED -> "Best eligible latency profile";
            case QUALITY -> "Highest eligible quality score";
            case BALANCED -> "Best quality, latency and cost trade-off";
        };
        return new RouteDecision(selected, fallback, eligible.get(0).score(),
                Math.round(cost * 1_000_000.0) / 1_000_000.0, reason,
                scored.stream().map(item -> new RouteDecision.CandidateScore(item.provider().name(),
                        item.score(), item.eligible(), item.note())).toList(), trace);
    }

    private Scored score(ProviderProfile provider, RouteRequest request) {
        if (!provider.healthy()) return new Scored(provider, 0, false, "provider unhealthy");
        if (request.visionRequired() && !provider.vision()) return new Scored(provider, 0, false, "vision unavailable");
        if (request.sensitivity() == RouteRequest.Sensitivity.RESTRICTED && !provider.id().equals("local")) {
            return new Scored(provider, 0, false, "restricted data must remain on-prem");
        }
        int costScore = provider.inputCostPerMillion() == 0 ? 100 : (int) Math.max(15, 100 - provider.inputCostPerMillion() * 18);
        int speedScore = Math.max(10, 120 - provider.p95LatencyMs() / 12);
        int score = switch (request.priority()) {
            case COST -> costScore * 6 / 10 + provider.quality() * 2 / 10 + speedScore * 2 / 10;
            case SPEED -> speedScore * 6 / 10 + provider.quality() * 3 / 10 + costScore / 10;
            case QUALITY -> provider.quality() * 7 / 10 + speedScore * 2 / 10 + costScore / 10;
            case BALANCED -> provider.quality() * 4 / 10 + speedScore * 3 / 10 + costScore * 3 / 10;
        };
        return new Scored(provider, score, true, "eligible");
    }

    private record Scored(ProviderProfile provider, int score, boolean eligible, String note) {}
}
