package com.project.coding.sum_check_api.exceptions;

public class WrongAnswerException extends RuntimeException {

    public WrongAnswerException() { super("That's wrong. Please try again.");}
}
