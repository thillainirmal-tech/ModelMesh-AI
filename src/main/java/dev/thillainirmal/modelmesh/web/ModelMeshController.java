package dev.thillainirmal.modelmesh.web;

import dev.thillainirmal.modelmesh.model.ProviderProfile;
import dev.thillainirmal.modelmesh.model.RouteDecision;
import dev.thillainirmal.modelmesh.model.RouteRequest;
import dev.thillainirmal.modelmesh.service.ModelMeshRouter;
import dev.thillainirmal.modelmesh.service.ModelExecutionService;
import dev.thillainirmal.modelmesh.model.ExecuteRequest;
import dev.thillainirmal.modelmesh.model.ExecutionResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ModelMeshController {
    private final ModelMeshRouter router;
    private final ModelExecutionService execution;

    public ModelMeshController(ModelMeshRouter router, ModelExecutionService execution) { this.router = router; this.execution = execution; }

    @GetMapping("/providers")
    public List<ProviderProfile> providers() { return router.providers(); }

    @PostMapping("/route")
    public RouteDecision route(@Valid @RequestBody RouteRequest request) { return router.route(request); }

    @PostMapping("/execute")
    public ExecutionResponse execute(@Valid @RequestBody ExecuteRequest request) { return execution.execute(request); }

}
