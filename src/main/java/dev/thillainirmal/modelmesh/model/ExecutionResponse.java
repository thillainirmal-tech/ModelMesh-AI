package dev.thillainirmal.modelmesh.model;
public record ExecutionResponse(String provider,String model,String content,boolean fallbackUsed,long latencyMs,RouteDecision route){}
