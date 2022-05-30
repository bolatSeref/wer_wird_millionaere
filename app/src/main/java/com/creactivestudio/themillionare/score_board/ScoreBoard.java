package com.creactivestudio.themillionare.score_board;

public class ScoreBoard {
    private int score;
    private String email;
    private String docId;
    private String userName;

    // TODO: 10.02.2021 tarih eklenecek

    public ScoreBoard() {
    }

    public ScoreBoard(int score, String email,String docId, String userName) {
        this.score = score;
        this.email = email;
        this.docId=docId;
        this.userName=userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDocId(){
        return docId;
    }
    public void setDocId(String docId){
        this.docId=docId;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
