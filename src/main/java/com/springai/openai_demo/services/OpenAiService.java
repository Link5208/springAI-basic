package com.springai.openai_demo.services;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import com.springai.openai_demo.text.prompttemplate.dtos.CountryCuisines;

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

	public String getTravelGuidance(String city, String month, String language, String budget) {
		PromptTemplate promptTemplate = new PromptTemplate(" Welcome to the {city} travel guide!\r\n" +
				" If you're visiting in {month}, here's what you can do:\r\n" +
				" 1. Must-visit attractions.\r\n" +
				" 2. Local cuisine you must try.\r\n" +
				" 3. Useful phrases in {language}.\r\n" +
				" 4. Tips for traveling on a {budget} budget.\r\n" +
				" Enjoy your trip!");
		Prompt prompt = promptTemplate.create(Map.of("city", city, "month", month, "language", language, "budget", budget));
		return chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
	}

	public CountryCuisines getCuisines(String country, String numCuisines, String language) {
		PromptTemplate promptTemplate = new PromptTemplate(" You are an expert in traditional cuisines.\r\n" +
				" You provide information about a specific dish from a specific\r\n" +
				" country.\r\n" +
				" Answer the question: What is the traditional cuisine of {country}?\r\n" +
				" Return a list of {numCuisines} in {language}." +
				" Avoid giving information about fictional places. If the country is\r\n" +
				" fictional\r\n" +
				" or non-existent answer: I don't know.\r\n" +
				"");
		Prompt prompt = promptTemplate
				.create(Map.of("country", country, "language", language, "numCuisines", numCuisines));
		return chatClient.prompt(prompt).call().entity(CountryCuisines.class);
	}
}
