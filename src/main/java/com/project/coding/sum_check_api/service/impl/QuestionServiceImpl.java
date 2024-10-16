package com.project.coding.sum_check_api.service.impl;

import com.project.coding.sum_check_api.exceptions.InvalidBodyRequestException;
import com.project.coding.sum_check_api.exceptions.InvalidQuestionException;
import com.project.coding.sum_check_api.exceptions.WrongAnswerException;
import com.project.coding.sum_check_api.service.QuestionService;
import com.project.coding.sum_check_api.util.Constants;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static HashMap<String, int[]> questionNumbersByClientMap = new HashMap<>();

    @Override
    public String generateNewQuestion(String ipClient) {

        Random random = new Random();
        int size = random.nextInt(Constants.MIN_SIZE,Constants.MAX_SIZE + 1);
        IntStream stream = random.ints(size, Constants.MIN_NUMBER, Constants.MAX_NUMBER + 1);
        int[] questionNumbers = stream.toArray();

        String question = Constants.SENTENCE_PREFIX.concat(Arrays.stream(questionNumbers)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(Constants.DELIMITER)));

        questionNumbersByClientMap.put(ipClient, questionNumbers);

        return question;
    }

    @Override
    public String validate(String ipClient, String question, int answer) {
        int[] numbers = extractNumbers(question);

        validateQuestion(ipClient, numbers);
        validateAnswer(numbers, answer);

        return Constants.SUCCESS_MESSAGE;
    }

    private int[] extractNumbers(String question) {
        try {
            return Arrays.stream(question
                            .replace(Constants.SENTENCE_PREFIX, Constants.EMPTY)
                            .split(Constants.DELIMITER))
                    .mapToInt(Integer::parseInt).toArray();
        } catch (Exception e) {
            throw new InvalidBodyRequestException();
        }
    }

    private void validateQuestion(String ipClient, int[] numbers) {
        int[] expectedNumbers = questionNumbersByClientMap.get(ipClient);
        if (!Arrays.equals(numbers, expectedNumbers)) {
            throw new InvalidQuestionException();
        }
    }

    private void validateAnswer(int[] numbers, int answer) {
        if (Arrays.stream(numbers).sum() != answer) {
            throw new WrongAnswerException();
        }
    }
}
