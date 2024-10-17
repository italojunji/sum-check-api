package com.project.coding.sum_check_api.controller;

import com.project.coding.sum_check_api.exceptions.InvalidBodyRequestException;
import com.project.coding.sum_check_api.exceptions.InvalidQuestionException;
import com.project.coding.sum_check_api.exceptions.WrongAnswerException;
import com.project.coding.sum_check_api.service.QuestionService;
import com.project.coding.sum_check_api.service.impl.QuestionServiceImpl;
import com.project.coding.sum_check_api.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    @InjectMocks
    private QuestionController controller;

    @Mock
    private HttpServletRequest request;

    @Mock
    private QuestionService questionService;

    String clientIp = "192.168.0.1";
    String question = "Please sum the numbers 1,2,3";
    Integer answer = 6;

    @Test
    void getNewQuestionReturning200() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        when(questionService.generateNewQuestion(clientIp)).thenReturn(question);

        ResponseEntity<String> responseEntity = controller.getNewQuestion(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(question, responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning200() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        when(questionService.validate(clientIp, question, answer)).thenReturn(Constants.SUCCESS_MESSAGE);

        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Constants.SUCCESS_MESSAGE, responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning400WhenThrowsInvalidBodyRequestException() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        doThrow(new InvalidBodyRequestException()).when(questionService).validate(clientIp, question, answer);

        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid request body.", responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning400WhenThrowsInvalidQuestionException() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        doThrow(new InvalidQuestionException()).when(questionService).validate(clientIp, question, answer);

        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Question is wrong. If you forgot it, please get a new one.", responseEntity.getBody());
    }

    @Test
    void answerQuestionReturning400WhenThrowsWrongAnswerExceptionException() {
        when(request.getRemoteAddr()).thenReturn(clientIp);
        doThrow(new WrongAnswerException()).when(questionService).validate(clientIp, question, answer);

        ResponseEntity<String> responseEntity = controller.answerQuestion(question, answer, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("That's wrong. Please try again.", responseEntity.getBody());
    }

}