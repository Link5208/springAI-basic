package com.springai.openai_demo.text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.springai.openai_demo.services.OpenAiService;

@RestController
public class AnswerAnyThingStreamingController {

	@Autowired
	OpenAiService service;

}