package com.project.coding.sum_check_api.exceptions;

public class InvalidQuestionException extends RuntimeException {

    public InvalidQuestionException() { super("Question is wrong. If you forgot it, please get a new one.");}
}
