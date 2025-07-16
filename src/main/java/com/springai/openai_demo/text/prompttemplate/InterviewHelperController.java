package com.springai.openai_demo.text.prompttemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springai.openai_demo.services.OpenAiService;

@Controller
public class InterviewHelperController {
	@Autowired
	private OpenAiService service;

	@GetMapping("/showInterviewHelper")
	public String showInterviewHelper() {
		return "interviewHelper";
	}

	@PostMapping("/interviewHelper")
	public String interviewHelper(@RequestParam("company") String company,
			@RequestParam("jobTitle") String jobTitle,
			@RequestParam("strength") String strengths,
			@RequestParam("weakness") String weaknesses, Model model) {

		String response = service.getInterviewHelper(company, jobTitle, strengths, weaknesses);
		model.addAttribute("response", response);
		return "interviewHelper";
	}

}
