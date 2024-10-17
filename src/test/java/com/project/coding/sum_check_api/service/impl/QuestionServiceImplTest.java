package com.project.coding.sum_check_api.service.impl;

import com.project.coding.sum_check_api.exceptions.InvalidBodyRequestException;
import com.project.coding.sum_check_api.exceptions.InvalidQuestionException;
import com.project.coding.sum_check_api.exceptions.WrongAnswerException;
import com.project.coding.sum_check_api.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @InjectMocks
    private QuestionServiceImpl service;

    @Mock
    private Random random;

    @BeforeEach
    void setUp() {
        QuestionServiceImpl.questionNumbersByClientMap.clear();
    }

    //Default successful scenario
    String clientIp = "192.168.0.1";
    String question = "Please sum the numbers 0,1,2";
    Integer answer = 3;
    int[] questionNumbers = new int[]{0,1,2};

    @Test
    void generateNewQuestionWith3Numbers() {
        assertTrue(QuestionServiceImpl.questionNumbersByClientMap.isEmpty());

        when(random.nextInt(Constants.MIN_SIZE,Constants.MAX_SIZE + 1)).thenReturn(3);
        when(random.ints(3, Constants.MIN_NUMBER, Constants.MAX_NUMBER + 1)).thenReturn(IntStream.of(3,4,5));

        String question = service.generateNewQuestion(clientIp);

        assertTrue(Arrays.equals(new int[]{3, 4, 5}, QuestionServiceImpl.questionNumbersByClientMap.get(clientIp)));
        assertEquals("Please sum the numbers 3,4,5", question);
    }

    @Test
    void generateNewQuestionWith2Numbers() {
        assertTrue(QuestionServiceImpl.questionNumbersByClientMap.isEmpty());

        when(random.nextInt(Constants.MIN_SIZE,Constants.MAX_SIZE + 1)).thenReturn(2);
        when(random.ints(2, Constants.MIN_NUMBER, Constants.MAX_NUMBER + 1)).thenReturn(IntStream.of(1,2));

        String question = service.generateNewQuestion(clientIp);

        assertTrue(Arrays.equals(new int[]{1, 2}, QuestionServiceImpl.questionNumbersByClientMap.get(clientIp)));
        assertEquals("Please sum the numbers 1,2", question);
    }

    @Test
    void generateMultipleQuestionsByTheSameClient() {
        assertTrue(QuestionServiceImpl.questionNumbersByClientMap.isEmpty());

        when(random.nextInt(Constants.MIN_SIZE,Constants.MAX_SIZE + 1)).thenReturn(2, 2);
        when(random.ints(2, Constants.MIN_NUMBER, Constants.MAX_NUMBER + 1)).thenReturn(IntStream.of(1,2), IntStream.of(9,10));

        String question1 = service.generateNewQuestion(clientIp);
        String question2 = service.generateNewQuestion(clientIp);

        assertEquals(1, QuestionServiceImpl.questionNumbersByClientMap.size());
        assertTrue(Arrays.equals(new int[]{9, 10}, QuestionServiceImpl.questionNumbersByClientMap.get(clientIp)));
        assertEquals("Please sum the numbers 1,2", question1);
        assertEquals("Please sum the numbers 9,10", question2);
    }

    @Test
    void generateMultipleQuestionsByDifferentClients() {
        assertTrue(QuestionServiceImpl.questionNumbersByClientMap.isEmpty());

        when(random.nextInt(Constants.MIN_SIZE,Constants.MAX_SIZE + 1)).thenReturn(2, 3,2,3);
        when(random.ints(2, Constants.MIN_NUMBER, Constants.MAX_NUMBER + 1)).thenReturn(IntStream.of(1,2), IntStream.of(9,10));
        when(random.ints(3, Constants.MIN_NUMBER, Constants.MAX_NUMBER + 1)).thenReturn(IntStream.of(4,5,6), IntStream.of(7,8,0));

        String question1_client1 = service.generateNewQuestion("ipTest_client1");
        String question1_client2 = service.generateNewQuestion("ipTest_client2");
        String question2_client1 = service.generateNewQuestion("ipTest_client1");
        String question2_client2 = service.generateNewQuestion("ipTest_client2");

        assertEquals(2, QuestionServiceImpl.questionNumbersByClientMap.size());
        assertTrue(Arrays.equals(new int[]{9, 10}, QuestionServiceImpl.questionNumbersByClientMap.get("ipTest_client1")));
        assertTrue(Arrays.equals(new int[]{7, 8, 0}, QuestionServiceImpl.questionNumbersByClientMap.get("ipTest_client2")));
        assertEquals("Please sum the numbers 1,2", question1_client1);
        assertEquals("Please sum the numbers 4,5,6", question1_client2);
        assertEquals("Please sum the numbers 9,10", question2_client1);
        assertEquals("Please sum the numbers 7,8,0", question2_client2);
    }

    @Test
    void validateWithSuccess() {
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        assertDoesNotThrow(() -> {
            String result = service.validate(clientIp, question, answer);
            assertEquals(Constants.SUCCESS_MESSAGE, result);
        });

    }

    @Test
    void validateWithInvalidBodyRequestExceptionWhenNumericPartPassedInBodyIsOutOfPattern() {
        question = "Please! sum the numbers: 0,1,2";

        Exception exception = assertThrows(InvalidBodyRequestException.class, () -> {
            service.validate(clientIp, question, answer);
        });
        assertEquals("Invalid request body.", exception.getMessage());
    }

    @Test
    void validateWithInvalidBodyRequestExceptionWhenPrefixTextPassedInBodyIsOutOfPattern() {
        question = "Please sum the numbers 0,@1,2.";

        Exception exception = assertThrows(InvalidBodyRequestException.class, () -> {
            service.validate(clientIp, question, answer);
        });
        assertEquals("Invalid request body.", exception.getMessage());
    }

    @Test
    void validateWithInvalidQuestionExceptionWhenThereIsNoCurrentRequestedQuestion() {
        assertTrue(QuestionServiceImpl.questionNumbersByClientMap.isEmpty());

        Exception exception = assertThrows(InvalidQuestionException.class, () -> {
            service.validate(clientIp, question, answer);
        });
        assertEquals("Question is wrong. If you forgot it, please get a new one.", exception.getMessage());
    }

    @Test
    void validateWithInvalidQuestionExceptionWhenThereIsNoRequestedQuestionByTheSpecificClient() {
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        clientIp = "111.111.1.1";
        Exception exception = assertThrows(InvalidQuestionException.class, () -> {
            service.validate(clientIp, question, answer);
        });
        assertEquals("Question is wrong. If you forgot it, please get a new one.", exception.getMessage());
    }

    @Test
    void validateWithInvalidQuestionExceptionWhenTheNumbersOfQuestionAreDifferentFromPreviousRequested() {
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        question = "Please sum the numbers 0,3";
        Exception exception = assertThrows(InvalidQuestionException.class, () -> {
            service.validate(clientIp, question, answer);
        });
        assertEquals("Question is wrong. If you forgot it, please get a new one.", exception.getMessage());
    }

    @Test
    void validateWithWrongAnswerException() {
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        answer = 2;
        Exception exception = assertThrows(WrongAnswerException.class, () -> {
            service.validate(clientIp, question, answer);
        });
        assertEquals("That's wrong. Please try again.", exception.getMessage());
    }


}