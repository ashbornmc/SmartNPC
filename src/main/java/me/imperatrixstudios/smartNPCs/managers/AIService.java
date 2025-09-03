package me.imperatrixstudios.smartNPCs.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.data.ChatMessage;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AIService {

    private final SmartNPCs plugin;
    private final ConfigManager configManager;
    private final OkHttpClient httpClient;
    private final Gson gson;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public AIService() {
        this.plugin = SmartNPCs.getInstance();
        this.configManager = plugin.getConfigManager();
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    public CompletableFuture<String> getResponse(SmartNPCData npcData, List<ChatMessage> history, String playerMessage) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String apiKey = configManager.getApiKey();
        if (apiKey == null || apiKey.equals("YOUR_API_KEY") || apiKey.isEmpty()) {
            future.complete("&cThe server owner has not configured an API key.");
            return future;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("model", configManager.getModel());
        payload.addProperty("max_tokens", configManager.getMaxTokens());
        payload.addProperty("temperature", configManager.getTemperature());

        JsonArray messages = new JsonArray();
        // 1. Add the system personality prompt
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", npcData.getPersonality());
        messages.add(systemMessage);

        // 2. Add the recent conversation history
        for (ChatMessage chatMessage : history) {
            JsonObject historyMessage = new JsonObject();
            historyMessage.addProperty("role", chatMessage.role());
            historyMessage.addProperty("content", chatMessage.content());
            messages.add(historyMessage);
        }

        // 3. Add the new player message
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", playerMessage);
        messages.add(userMessage);

        payload.add("messages", messages);

        RequestBody body = RequestBody.create(gson.toJson(payload), JSON);
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                plugin.getLogger().severe("Failed to get response from OpenAI API: " + e.getMessage());
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    plugin.getLogger().severe("API request was not successful: " + response);
                    future.complete("&cAn API error occurred. Check console.");
                    return;
                }
                try {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    String aiMessage = jsonResponse.getAsJsonArray("choices").get(0)
                            .getAsJsonObject().getAsJsonObject("message")
                            .get("content").getAsString();
                    future.complete(aiMessage.trim());
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to parse API response: " + e.getMessage());
                    future.complete("&cFailed to parse API response.");
                }
            }
        });

        return future;
    }
}