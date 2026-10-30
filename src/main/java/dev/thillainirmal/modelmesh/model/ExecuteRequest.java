package dev.thillainirmal.modelmesh.model;
import jakarta.validation.constraints.*;
public record ExecuteRequest(@NotBlank @Size(max=30000) String prompt,@NotNull RouteRequest.Priority priority,@NotNull RouteRequest.Sensitivity sensitivity,boolean visionRequired,@Min(1) @Max(16000) int expectedOutputTokens) {
 public RouteRequest routeRequest(){return new RouteRequest(prompt,priority,sensitivity,visionRequired,expectedOutputTokens);}
}
