package com.springai.openai_demo.services;

import java.util.List;
import java.util.Map;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat;
// import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
// import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.springai.openai_demo.text.prompttemplate.dtos.CountryCuisines;
import com.springai.openai_demo.tools.WeatherTools;

@Service
public class OpenAiService {
	private ChatClient chatClient;
	@Autowired
	private EmbeddingModel embeddingModel;
	@Autowired
	private OpenAiImageModel openAiImageModel;
	@Autowired
	private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
	@Autowired
	private OpenAiAudioSpeechModel openAiAudioSpeechModel;
	// @Autowired
	// private VectorStore vectorStore;

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

	public ChatResponse generateAnswerWithRoles(String question) {
		return chatClient.prompt(question).system("You are a helpful assistant that can answer any question")
				.user(question)
				.call().chatResponse();
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

	public String getInterviewHelper(String company, String jobTitle, String strengths, String weaknesses) {
		PromptTemplate promptTemplate = new PromptTemplate(
				" You are a career coach. Provide tailored interview tips for the\r\n" + //
						" position of {jobTitle} at {company}.\r\n" + //
						" Highlight your strengths in {strengths} and prepare for questions\r\n" + //
						" about your weaknesses such as {weaknesses}");
		Prompt prompt = promptTemplate
				.create(Map.of("jobTitle", jobTitle, "company", company, "weaknesses", weaknesses, "strengths", strengths));
		return chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
	}

	public float[] embed(String text) {
		return embeddingModel.embed(text);
	}

	public double findSimilarity(String text1, String text2) {
		List<float[]> reponse = embeddingModel.embed(List.of(text1, text2));
		return cosineSimilarity(reponse.get(0), reponse.get(1));
	}

	private double cosineSimilarity(float[] vectorA, float[] vectorB) {
		if (vectorA.length != vectorB.length) {
			throw new IllegalArgumentException("Vectors must be of the same length");
		}

		// Initialize variables for dot product and magnitudes
		double dotProduct = 0.0;
		double magnitudeA = 0.0;
		double magnitudeB = 0.0;

		// Calculate dot product and magnitudes
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			magnitudeA += vectorA[i] * vectorA[i];
			magnitudeB += vectorB[i] * vectorB[i];
		}

		// Calculate and return cosine similarity
		return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
	}

	// public List<Document> searchJobs(String query) {
	// return vectorStore.similaritySearch(query);
	// }

	// public String answer(String query) {
	// return chatClient.prompt(query).advisors(new
	// QuestionAnswerAdvisor(vectorStore)).call().content();
	// }

	public String generateImage(String prompt) {
		ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt, OpenAiImageOptions.builder().quality("hd")
				.height(1024).width(1024).N(1).build()));
		return response.getResult().getOutput().getUrl();
	}

	public String explainImage(String prompt, String path) {
		String explaination = chatClient.prompt()
				.user(u -> u.text(prompt).media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(path))).call()
				.content();
		return explaination;
	}

	public String getDietAdvice(String prompt, String path1, String path2) {
		String explaination = chatClient.prompt()
				.user(u -> u.text(prompt).media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(path1)))
				.user(u -> u.text(prompt).media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(path2)))
				.call().content();
		return explaination;
	}

	public String speechToText(String path) {
		OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
				.language("fr")
				.responseFormat(TranscriptResponseFormat.VTT).build();
		AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(new FileSystemResource(path));
		return openAiAudioTranscriptionModel.call(transcriptionPrompt).getResult().getOutput();
	}

	public byte[] textToSpeech(String text) {
		return openAiAudioSpeechModel.call(text);
	}

	public String callAgent(String query) {
		return chatClient.prompt(query).tools(new WeatherTools()).call().content();
	}
}
