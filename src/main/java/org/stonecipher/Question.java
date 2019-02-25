package org.stonecipher;

public class Question {

    enum Answer {
        TRUE, FALSE, EITHER;
    }

    private Answer actionRequired;
    private String message;

    public Question(Answer actionRequired, String message) {
        this.actionRequired = actionRequired;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public Answer getActionRequired() {
        return this.actionRequired;
    }
}
