package com.project.coding.sum_check_api.controller;

import com.project.coding.sum_check_api.service.impl.QuestionServiceImpl;
import com.project.coding.sum_check_api.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class QuestionControllerIntegrationTest {

    private QuestionController controller;

    @Mock
    private HttpServletRequest request;

    //Default successful scenario
    String clientIp = "192.168.0.1";
    String question = "Please sum the numbers 7,8,9";
    Integer answer = 24;
    int[] questionNumbers = new int[]{7,8,9};

    @Autowired
    QuestionControllerIntegrationTest(QuestionController controller) {
        this.controller = controller;
    }

    @BeforeEach
    void setUp() {
        QuestionServiceImpl.questionNumbersByClientMap.clear();
    }

    @Test
    void getNewQuestionReturning200() {
        when(request.getRemoteAddr()).thenReturn(clientIp);

        ResponseEntity<String> responseEntity = controller.getNewQuestion(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().contains(Constants.SENTENCE_PREFIX));
    }

    @Test
    void answerQuestionReturning200() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Constants.SUCCESS_MESSAGE, responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning400WhenThrowsInvalidBodyRequestException() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        question = "Please sum the numbers 7,8,9@";
        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid request body.", responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning400WhenThrowsInvalidQuestionException() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        questionNumbers = new int[]{1,1,1};
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Question is wrong. If you forgot it, please get a new one.", responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning400WhenThrowsWrongAnswerExceptionException() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        QuestionServiceImpl.questionNumbersByClientMap.put(clientIp, questionNumbers);

        answer = 23;
        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("That's wrong. Please try again.", responseEntity.getBody());
    }

    @Test
    void getNewQuestionReturning200ThenAnswerQuestionReturning200() {
        when(request.getRemoteAddr()).thenReturn(clientIp);

        //Getting the question
        ResponseEntity<String> questionResponse = controller.getNewQuestion(request);
        String question = questionResponse.getBody();

        assertEquals(HttpStatus.OK, questionResponse.getStatusCode());
        assertTrue(question.contains(Constants.SENTENCE_PREFIX));

        //Sum numbers of the question
        Integer sumNumbersQuestion = Arrays.stream(question
                .replace(Constants.SENTENCE_PREFIX, Constants.EMPTY)
                .split(Constants.DELIMITER))
                .mapToInt(Integer::parseInt)
                .sum();
        answer = sumNumbersQuestion;

        //Answer the question
        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Constants.SUCCESS_MESSAGE, responseEntity.getBody());
    }

    @Test
    void getNewTwoQuestionsReturning200ByDifferentClientsThenAnswerThemReturning200() {
        String clientIp2 = "192.168.0.2";
        //Mock to simulate requests from different clients
        when(request.getRemoteAddr()).thenReturn(clientIp, clientIp2, clientIp, clientIp2);

        //Get two questions in sequence by different clients each
        ResponseEntity<String> questionResponse1 = controller.getNewQuestion(request);
        String question1 = questionResponse1.getBody();

        ResponseEntity<String> questionResponse2 = controller.getNewQuestion(request);
        String question2 = questionResponse2.getBody();

        assertEquals(HttpStatus.OK, questionResponse1.getStatusCode());
        assertEquals(HttpStatus.OK, questionResponse2.getStatusCode());

        //Calculating the respective answers
        Integer sumNumbersQuestion1 = Arrays.stream(question1
                        .replace(Constants.SENTENCE_PREFIX, Constants.EMPTY)
                        .split(Constants.DELIMITER))
                .mapToInt(Integer::parseInt)
                .sum();

        Integer sumNumbersQuestion2 = Arrays.stream(question2
                        .replace(Constants.SENTENCE_PREFIX, Constants.EMPTY)
                        .split(Constants.DELIMITER))
                .mapToInt(Integer::parseInt)
                .sum();

        //Answer the questions in the same order
        ResponseEntity<String> response1 = controller.answerQuestion(question1, sumNumbersQuestion1, request);
        ResponseEntity<String> response2 = controller.answerQuestion(question2, sumNumbersQuestion2, request);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(Constants.SUCCESS_MESSAGE, response1.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(Constants.SUCCESS_MESSAGE, response2.getBody());
        assertEquals(2, QuestionServiceImpl.questionNumbersByClientMap.size());
    }

}