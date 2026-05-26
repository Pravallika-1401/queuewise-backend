package com.queuewise.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AIService {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Value("${openai.api.url}")
    private String openAiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // AI wait time estimate generate cheyyi
    // Interview lo: "OpenAI API call chestamu, context pampistamu, AI smart estimate istaadi"
    public String getSmartWaitEstimate(int queueSize, int avgServiceTime, int currentHour) {

        // Simple math estimate
        int simpleEstimate = queueSize * avgServiceTime;

        // API key configure kaaledu ante fallback use cheyyi
        if (openAiKey.equals("your-openai-api-key-here")) {
            return getFallbackEstimate(queueSize, avgServiceTime, currentHour, simpleEstimate);
        }

        try {
            String prompt = buildPrompt(queueSize, avgServiceTime, currentHour, simpleEstimate);
            return callOpenAI(prompt);
        } catch (Exception e) {
            // API fail ayite fallback ki fall back cheyyi
            return getFallbackEstimate(queueSize, avgServiceTime, currentHour, simpleEstimate);
        }
    }

    private String buildPrompt(int queueSize, int avgTime, int hour, int simpleEst) {
        String timeOfDay = getTimeOfDay(hour);
        return String.format(
            "You are a queue management assistant. Given this data:\n" +
            "- People waiting: %d\n" +
            "- Average service time: %d minutes per person\n" +
            "- Simple estimate: %d minutes\n" +
            "- Time of day: %s (hour %d)\n\n" +
            "Give a realistic wait time estimate in ONE short sentence (under 20 words). " +
            "Consider that %s hours tend to be %s. Be helpful and specific.",
            queueSize, avgTime, simpleEst, timeOfDay, hour,
            timeOfDay, getRushInfo(hour)
        );
    }

    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo"); // cheapest model
        body.put("messages", List.of(message));
        body.put("max_tokens", 60);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(openAiUrl, request, Map.class);

        // Response parse cheyyi
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map firstChoice = choices.get(0);
        Map messageMap = (Map) firstChoice.get("message");
        return (String) messageMap.get("content");
    }

    // API key lekapothe smart fallback — still looks impressive
    private String getFallbackEstimate(int queueSize, int avgTime, int hour, int simpleEst) {
        String timeNote = "";
        if (hour >= 12 && hour <= 14) timeNote = " Lunch hour — slightly longer delays expected.";
        else if (hour >= 17 && hour <= 19) timeNote = " Evening rush — expect some delay.";
        else if (hour >= 9 && hour <= 11) timeNote = " Morning hours — service is typically faster.";

        if (queueSize == 0) return "You are next! Please proceed.";
        if (queueSize <= 3) return "Short wait of about " + simpleEst + " minutes." + timeNote;
        return "Estimated wait: " + simpleEst + " minutes for " + queueSize + " people ahead." + timeNote;
    }

    private String getTimeOfDay(int hour) {
        if (hour < 12) return "morning";
        if (hour < 17) return "afternoon";
        return "evening";
    }

    private String getRushInfo(int hour) {
        if (hour >= 12 && hour <= 14) return "busier due to lunch breaks";
        if (hour >= 17 && hour <= 19) return "busier due to after-work rush";
        return "relatively normal";
    }
}
