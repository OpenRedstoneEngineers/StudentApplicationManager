package org.stonecipher;

public class Question {

    enum Answer {
        YES, NO, EITHER;
    }

    private Answer actionRequired;
    private String message;

    public Question(String messsage) {
        this.message = messsage;
    }

    public Question(String actionRequired, String message) {
        this.actionRequired = computeActionRequired(actionRequired);
        this.message = message;
    }
    public Question(Answer actionRequired, String message) {
        this.actionRequired = actionRequired;
        this.message = message;
    }

    private Answer computeActionRequired(String action) {
        if (action.equals("YES")) return Answer.YES;
        if (action.equals("NO")) return Answer.NO;
        return Answer.EITHER;
    }

    public void setAnswer(Question.Answer answer) {
        this.actionRequired = answer;
    }

    public String toString() {
        return "Question(q: " + this.message + ",a: " + this.actionRequired + ")";
    }

    public String getMessage() {
        return this.message;
    }

    public Answer getActionRequired() {
        return this.actionRequired;
    }
}
