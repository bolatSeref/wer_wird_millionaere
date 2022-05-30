package com.creactivestudio.themillionare.admin;

public class QuestionFromAdmin {
    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String questionLanguage;
    private int questionLevel;
    private String adminEmail;
    private String rightChoice;

    public QuestionFromAdmin() {

    }

    public QuestionFromAdmin(String question, String choiceA, String choiceB, String choiceC, String choiceD, String questionLanguage, int questionLevel, String adminEmail, String rightChoice) {
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.questionLanguage = questionLanguage;
        this.questionLevel = questionLevel;
        this.adminEmail = adminEmail;
        this.rightChoice = rightChoice;
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

    public String getQuestionLanguage() {
        return questionLanguage;
    }

    public void setQuestionLanguage(String questionLanguage) {
        this.questionLanguage = questionLanguage;
    }

    public int getQuestionLevel() {
        return questionLevel;
    }

    public void setQuestionLevel(int questionLevel) {
        this.questionLevel = questionLevel;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getRightChoice() {
        return rightChoice;
    }

    public void setRightChoice(String rightChoice) {
        this.rightChoice = rightChoice;
    }
}
