package com.springai.openai_demo.text;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springai.openai_demo.services.OpenAiService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AnswerAnyThingController {

	private OpenAiService service;

	@GetMapping("/showAskAnything")
	public String showAskAnything() {
		return "askAnything";
	}

	@PostMapping("/askAnything")
	public String askAnything(@RequestParam("question") String question, Model model) {
		ChatResponse response = service.generateAnswer(question);
		System.out.println(response);
		model.addAttribute("question", question);
		model.addAttribute("answer", response.getResult().getOutput().getText());
		return "askAnything";
	}
}