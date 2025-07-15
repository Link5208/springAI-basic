package com.springai.openai_demo.text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springai.openai_demo.services.OpenAiService;

@Controller
public class AnswerAnyThingController {

	@Autowired
	private OpenAiService chatService;

	@GetMapping("/showAskAnything")
	public String showAskAnything() {
		return "askAnything";
	}

	@PostMapping("/askAnything")
	public String askAnything(@RequestParam("question") String question, Model model) {

		return "askAnything";
	}
}