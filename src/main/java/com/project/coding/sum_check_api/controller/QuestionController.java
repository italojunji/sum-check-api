package com.project.coding.sum_check_api.controller;

import com.project.coding.sum_check_api.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<String> getNewQuestion(HttpServletRequest request) {
        String question = questionService.generateNewQuestion(request.getRemoteAddr());
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PostMapping("/{answer}")
    public ResponseEntity<String> answerQuestion(@RequestBody String question, @PathVariable Integer answer, HttpServletRequest request) {
        try {
            String success = questionService.validate(request.getRemoteAddr(), question, answer);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
