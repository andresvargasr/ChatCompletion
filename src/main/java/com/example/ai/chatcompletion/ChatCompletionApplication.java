package com.example.ai.chatcompletion;


import java.util.ArrayList;
import java.util.List;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.IterableStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ChatCompletionApplication {
    private static final Logger log = LoggerFactory.getLogger(ChatCompletionApplication.class);


    public static void main(String[] args) {

        SpringApplication.run(ChatCompletionApplication.class, args);
        String apiKey = "ANNgZZXb8kD8SSHLReWVaGr3CJHAsUmRvC6jIAKy0LNQeeoIGRvXJQQJ99BEACHYHv6XJ3w3AAAAACOGcTl0";
        String endpoint = "https://ai-andresvargas3736ai743028576454.openai.azure.com/";
        String model = "gpt-4.1";
        String deploymentName = "gpt-4";
        OpenAIClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildClient();

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage("You are a helpful assistant."));
        chatMessages.add(new ChatRequestUserMessage("I am going to Paris, what should I see?"));
        chatMessages.add(new ChatRequestAssistantMessage("Paris, the capital of France, is known for its stunning architecture, art museums, historical landmarks, and romantic atmosphere. Here are some of the top attractions to see in Paris:\n \n 1. The Eiffel Tower: The iconic Eiffel Tower is one of the most recognizable landmarks in the world and offers breathtaking views of the city.\n 2. The Louvre Museum: The Louvre is one of the world's largest and most famous museums, housing an impressive collection of art and artifacts, including the Mona Lisa.\n 3. Notre-Dame Cathedral: This beautiful cathedral is one of the most famous landmarks in Paris and is known for its Gothic architecture and stunning stained glass windows.\n \n These are just a few of the many attractions that Paris has to offer. With so much to see and do, it's no wonder that Paris is one of the most popular tourist destinations in the world."));
        chatMessages.add(new ChatRequestUserMessage("What is so great about #1?"));

        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
        chatCompletionsOptions.setMaxCompletionTokens(800);
        chatCompletionsOptions.setTemperature(1d);
        chatCompletionsOptions.setTopP(1d);
        chatCompletionsOptions.setFrequencyPenalty(0d);
        chatCompletionsOptions.setPresencePenalty(0d);
        chatCompletionsOptions.setModel(model);

        IterableStream<ChatCompletions> chatCompletionsStream = client.getChatCompletionsStream(deploymentName,
                new ChatCompletionsOptions(chatMessages));

        // The delta is the message content for a streaming response.
        // Subsequence of streaming delta will be like:
        // "delta": {
        //     "role": "assistant"
        // },
        // "delta": {
        //     "content": "Why"
        //  },
        //  "delta": {
        //     "content": " don"
        //  },
        //  "delta": {
        //     "content": "'t"
        //  }
        chatCompletionsStream
                .stream()
                .forEach(chatCompletions -> {
                    if (CoreUtils.isNullOrEmpty(chatCompletions.getChoices())) {
                        return;
                    }
                    ChatResponseMessage delta = chatCompletions.getChoices().get(0).getDelta();
                    if (delta.getRole() != null) {
                        System.out.println("Role = " + delta.getRole());
                    }
                    if (delta.getContent() != null) {
                        System.out.print(delta.getContent());
                    }
                });
    }
}
