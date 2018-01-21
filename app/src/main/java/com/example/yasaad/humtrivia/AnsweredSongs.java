package com.example.yasaad.humtrivia;
import java.util.Date;
/**
 * Created by Patri on 1/21/2018.
 */

public class AnsweredSongs {
    private String mainUser;
    private String Answers;
    private long messageTime;

    public AnsweredSongs(String mainUser, String Answers) {
        this.mainUser = mainUser;
        this.Answers = Answers;

        messageTime = new Date().getTime();
    }
    public AnsweredSongs() {
    }


    public void setMainUser(String mainUser) {
        this.mainUser = mainUser;
    }
    public String getAnswers() {
        return Answers;
    }
    public void setAnswers(String Answers) {
        this.Answers = Answers;
    }
}

