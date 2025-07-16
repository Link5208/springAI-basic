package com.springai.openai_demo.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {
	private ChatClient chatClient;

	public OpenAiService(ChatClient.Builder builder) {

		InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();
		ChatMemory chatMemory = MessageWindowChatMemory.builder()
				.chatMemoryRepository(memoryRepository)
				.maxMessages(20)
				.build();
		MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
		chatClient = builder.defaultAdvisors(advisor).build();
	}

	public ChatResponse generateAnswer(String question) {
		// OpenAiChatOptions options = new OpenAiChatOptions();
		// options.setModel("gpt-4o");
		// options.setTemperature(0.7);
		// options.setMaxTokens(20);
		return chatClient.prompt(question).call().chatResponse();
	}
}
