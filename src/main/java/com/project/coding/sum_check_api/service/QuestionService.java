package com.project.coding.sum_check_api.service;

public interface QuestionService {

    String generateNewQuestion(String ipClient);

    String validate(String ipClient, String question, int answer);
}
