package dev.thillainirmal.modelmesh.service;

import dev.thillainirmal.modelmesh.model.RouteRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelMeshRouterTest {
    private final ModelMeshRouter router = new ModelMeshRouter();

    @Test void restrictedDataStaysPrivate() {
        var decision = router.route(new RouteRequest("restricted incident", RouteRequest.Priority.QUALITY,
                RouteRequest.Sensitivity.RESTRICTED, false, 500));
        assertThat(decision.selected().id()).isEqualTo("local");
        assertThat(decision.candidates()).filteredOn(c -> !c.eligible()).hasSize(3);
    }

    @Test void qualityPrefersHighestQualityEligibleProvider() {
        var decision = router.route(new RouteRequest("review code", RouteRequest.Priority.QUALITY,
                RouteRequest.Sensitivity.INTERNAL, false, 900));
        assertThat(decision.selected().id()).isEqualTo("openai");
        assertThat(decision.routeScore()).isPositive();
    }

    @Test void costPrefersPrivateZeroMarginalCostRuntime() {
        var decision = router.route(new RouteRequest("summarize", RouteRequest.Priority.COST,
                RouteRequest.Sensitivity.PUBLIC, false, 400));
        assertThat(decision.selected().id()).isEqualTo("local");
        assertThat(decision.estimatedCostUsd()).isZero();
    }

    @Test void visionExcludesLocalRuntime() {
        var decision = router.route(new RouteRequest("analyze image", RouteRequest.Priority.BALANCED,
                RouteRequest.Sensitivity.PUBLIC, true, 400));
        assertThat(decision.selected().id()).isNotEqualTo("local");
        assertThat(decision.candidates()).anyMatch(c -> c.provider().equals("Private Runtime") && !c.eligible());
    }
}
