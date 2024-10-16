package com.project.coding.sum_check_api;

import com.project.coding.sum_check_api.service.QuestionService;
import com.project.coding.sum_check_api.service.impl.QuestionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SumCheckApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(SumCheckApiApplication.class, args);

		QuestionServiceImpl questionService = new QuestionServiceImpl();
		System.out.println(questionService.validate("00", "Please sum the numbers 9,4,3", 16));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
		System.out.println(questionService.generateNewQuestion("00"));
	}

}
