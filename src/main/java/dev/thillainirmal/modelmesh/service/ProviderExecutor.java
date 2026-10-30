package dev.thillainirmal.modelmesh.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.thillainirmal.modelmesh.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.Duration;
import java.util.*;

@Service
public class ProviderExecutor {
 private final RestClient http; private final String openAiKey,geminiKey,anthropicKey,ollamaUrl;
 private final String openAiModel,geminiModel,anthropicModel,ollamaModel;
 public ProviderExecutor(RestClient.Builder builder,@Value("${modelmesh.openai.api-key:}")String openAiKey,@Value("${modelmesh.gemini.api-key:}")String geminiKey,@Value("${modelmesh.anthropic.api-key:}")String anthropicKey,@Value("${modelmesh.ollama.url:http://localhost:11434}")String ollamaUrl,@Value("${modelmesh.openai.model:gpt-5-mini}")String openAiModel,@Value("${modelmesh.gemini.model:gemini-2.5-flash}")String geminiModel,@Value("${modelmesh.anthropic.model:claude-sonnet-4-5}")String anthropicModel,@Value("${modelmesh.ollama.model:llama3.2}")String ollamaModel){this.http=builder.build();this.openAiKey=openAiKey;this.geminiKey=geminiKey;this.anthropicKey=anthropicKey;this.ollamaUrl=ollamaUrl;this.openAiModel=openAiModel;this.geminiModel=geminiModel;this.anthropicModel=anthropicModel;this.ollamaModel=ollamaModel;}
 public Result execute(String provider,String prompt,int maxTokens){return switch(provider){case"openai"->openAi(prompt,maxTokens);case"gemini"->gemini(prompt,maxTokens);case"anthropic"->anthropic(prompt,maxTokens);case"local"->ollama(prompt);default->throw new IllegalArgumentException("Unsupported provider: "+provider);};}
 private Result openAi(String prompt,int max){require(openAiKey,"OPENAI_API_KEY");JsonNode n=http.post().uri("https://api.openai.com/v1/chat/completions").header("Authorization","Bearer "+openAiKey).body(Map.of("model",openAiModel,"messages",List.of(Map.of("role","user","content",prompt)),"max_completion_tokens",max)).retrieve().body(JsonNode.class);return new Result(openAiModel,text(n,"/choices/0/message/content"));}
 private Result gemini(String prompt,int max){require(geminiKey,"GOOGLE_API_KEY");JsonNode n=http.post().uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}",geminiModel,geminiKey).body(Map.of("contents",List.of(Map.of("parts",List.of(Map.of("text",prompt)))),"generationConfig",Map.of("maxOutputTokens",max))).retrieve().body(JsonNode.class);return new Result(geminiModel,text(n,"/candidates/0/content/parts/0/text"));}
 private Result anthropic(String prompt,int max){require(anthropicKey,"ANTHROPIC_API_KEY");JsonNode n=http.post().uri("https://api.anthropic.com/v1/messages").header("x-api-key",anthropicKey).header("anthropic-version","2023-06-01").body(Map.of("model",anthropicModel,"max_tokens",max,"messages",List.of(Map.of("role","user","content",prompt)))).retrieve().body(JsonNode.class);return new Result(anthropicModel,text(n,"/content/0/text"));}
 private Result ollama(String prompt){JsonNode n=http.post().uri(ollamaUrl+"/api/chat").body(Map.of("model",ollamaModel,"stream",false,"messages",List.of(Map.of("role","user","content",prompt)))).retrieve().body(JsonNode.class);return new Result(ollamaModel,text(n,"/message/content"));}
 private String text(JsonNode node,String pointer){if(node==null||node.at(pointer).isMissingNode())throw new IllegalStateException("Provider returned an unexpected response");return node.at(pointer).asText();}
 private void require(String value,String name){if(value==null||value.isBlank())throw new IllegalStateException(name+" is not configured");}
 public record Result(String model,String content){}
}
