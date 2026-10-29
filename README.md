# ModelMesh AI

Policy-driven multi-provider AI gateway for Java teams. ModelMesh ranks OpenAI, Gemini, Anthropic and a private runtime by privacy, quality, latency and cost, then returns an auditable route and fallback.

[![CI](https://github.com/thillainirmal-tech/ModelMesh-AI/actions/workflows/ci.yml/badge.svg)](https://github.com/thillainirmal-tech/ModelMesh-AI/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?logo=springboot&logoColor=white)

## Highlights

- deterministic provider scoring with no API key required
- strict on-prem routing for restricted data
- vision capability filtering and health-aware eligibility
- cost estimates, fallback selection and complete policy traces
- REST API, responsive control-plane dashboard, Actuator and Prometheus

## Run

```bash
mvn spring-boot:run
```

Open http://localhost:8082 or call:

```bash
curl -X POST http://localhost:8082/api/route -H "Content-Type: application/json" \
  -d '{"prompt":"Review this design","priority":"QUALITY","sensitivity":"INTERNAL","visionRequired":false,"expectedOutputTokens":700}'
```

Provider prices and model names are demonstration configuration, not live quotes. Plug real provider adapters behind the routing decision in production.

## Test

`mvn verify`

## Roadmap

- circuit breakers and rolling provider health
- budget envelopes per team
- OpenTelemetry decision traces
- pluggable Spring AI provider adapters
- shadow routing and response evaluation

Apache-2.0. Contributions should use synthetic prompts and must not include credentials or confidential data.
