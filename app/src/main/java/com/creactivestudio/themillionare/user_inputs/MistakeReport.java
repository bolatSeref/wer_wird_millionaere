package com.creactivestudio.themillionare.user_inputs;

public class MistakeReport {
    private String userMessage;
    private String userEmail;

    public MistakeReport() {
    }

    public MistakeReport(String userMessage, String userEmail) {

        this.userMessage = userMessage;
        this.userEmail = userEmail;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
