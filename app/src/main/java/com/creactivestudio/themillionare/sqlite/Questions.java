package com.creactivestudio.themillionare.sqlite;

public class Questions {
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private int questionLevel;
    private int wrightAnswer;
    private int questionId;
    private String questionLanguage;
    private String questionDocId;
    private String rightChoice;

    public Questions() {

    }

    public String getQuestionDocId() {
        return questionDocId;
    }

    public String getRightChoice() {
        return rightChoice;
    }

    public void setRightChoice(String rightChoice) {
        this.rightChoice = rightChoice;
    }

    public void setQuestionDocId(String questionDocId) {
        this.questionDocId = questionDocId;
    }

    public Questions(String question, String choiceA, String choiceB, String choiceC, String choiceD, int questionLevel, int wrightAnswer,
                     int questionId, String questionLanguage, String questionDocId,String rightChoice) {
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.questionLevel = questionLevel;
        this.wrightAnswer = wrightAnswer;
        this.questionId = questionId;
        this.questionLanguage = questionLanguage;
        this.questionDocId=questionDocId;
        this.rightChoice=rightChoice;
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

    public int getQuestionLevel() {
        return questionLevel;
    }

    public void setQuestionLevel(int questionLevel) {
        this.questionLevel = questionLevel;
    }

    public int getWrightAnswer() {
        return wrightAnswer;
    }

    public void setWrightAnswer(int wrightAnswer) {
        this.wrightAnswer = wrightAnswer;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionLanguage() {
        return questionLanguage;
    }

    public void setQuestionLanguage(String questionLanguage) {
        this.questionLanguage = questionLanguage;
    }
}

