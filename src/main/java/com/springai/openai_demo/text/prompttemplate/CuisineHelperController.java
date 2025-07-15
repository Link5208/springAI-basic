package com.springai.openai_demo.text.prompttemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springai.openai_demo.services.OpenAiService;

@Controller
public class CuisineHelperController {
	@Autowired
	private OpenAiService chatService;

	@GetMapping("/showCuisineHelper")
	public String showChatPage() {
		return "cuisineHelper";
	}

	@PostMapping("/cuisineHelper")
	public String getChatResponse(@RequestParam("country") String country,
			@RequestParam("numCuisines") String numCuisines, @RequestParam("language") String language, Model model) {

		return "cuisineHelper";
	}
}
