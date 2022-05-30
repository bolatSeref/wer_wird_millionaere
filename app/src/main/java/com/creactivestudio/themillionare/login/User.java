package com.creactivestudio.themillionare.login;

public class User {
    private String firebaseDocId;
    private String userEmail;
    private int score;
    private int totalGem;
    private String userName;

    public User() {

    }

    public User(String firebaseDocId, String userEmail, int score, int totalGem, String userName) {
        this.firebaseDocId = firebaseDocId;
        this.userEmail = userEmail;
        this.score = score;
        this.totalGem = totalGem;
        this.userName=userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirebaseDocId() {
        return firebaseDocId;
    }

    public void setFirebaseDocId(String firebaseDocId) {
        this.firebaseDocId = firebaseDocId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalGem() {
        return totalGem;
    }

    public void setTotalGem(int totalGem) {
        this.totalGem = totalGem;
    }
}
