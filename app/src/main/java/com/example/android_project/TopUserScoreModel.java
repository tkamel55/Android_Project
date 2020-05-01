package com.example.android_project;

public class TopUserScoreModel {
    private User user;
    private Scores score;

    public TopUserScoreModel(User user, Scores score) {
        this.user = user;
        this.score = score;
    }

    public TopUserScoreModel() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scores getScore() {
        return score;
    }

    public void setScore(Scores score) {
        this.score = score;
    }
}
