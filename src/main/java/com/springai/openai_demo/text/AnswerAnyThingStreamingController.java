package com.springai.openai_demo.text;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springai.openai_demo.services.OpenAiService;

import reactor.core.publisher.Flux;

@RestController
public class AnswerAnyThingStreamingController {

	@Autowired
	OpenAiService service;

	@PostMapping("/stream")
	public Flux<String> askAnything(@RequestParam("message") String message, Model model) {
		return service.streamAnswer(message);

	}

}