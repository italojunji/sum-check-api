package com.project.coding.sum_check_api;

import com.project.coding.sum_check_api.service.impl.QuestionServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class SumCheckApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(SumCheckApiApplication.class, args);
		Random random = new Random();
		QuestionServiceImpl questionService = new QuestionServiceImpl(random);
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
