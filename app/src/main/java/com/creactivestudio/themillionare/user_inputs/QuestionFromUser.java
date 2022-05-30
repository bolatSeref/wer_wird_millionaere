package com.creactivestudio.themillionare.user_inputs;

public class QuestionFromUser {
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private boolean isApplied;
    private String userEmail;
    private int rightAnswer;
    private int questionLevel;

    public QuestionFromUser() {
    }

    public QuestionFromUser(String question, String choiceA, String choiceB, String choiceC, String choiceD, boolean isApplied, String userEmail, int rightAnswer, int questionLevel) {
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.isApplied = isApplied;
        this.userEmail = userEmail;
        this.rightAnswer = rightAnswer;
        this.questionLevel=questionLevel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public int getQuestionLevel(){
        return questionLevel;
    }
    public void setQuestionLevel(int questionLevel)
    {
        this.questionLevel=questionLevel;
    }

    public boolean isApplied() {
        return isApplied;
    }

    public void setApplied(boolean applied) {
        isApplied = applied;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(int rightAnswer) {
        this.rightAnswer = rightAnswer;
    }
}

