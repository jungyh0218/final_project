package knu.mobile.lookatmyenglish;

import java.sql.Date;

/**
 * Created by sec on 2015-12-17.
 */
public class QuestionContent {
    private String title;
    private String content;
    private String date;
    private String questioner;
    int vote;

    public QuestionContent(String title, String content, String date, String questioner, int vote){
        this.title = title;
        this.content = content;
        this.date = date;
        this.questioner = questioner;
        this.vote = vote;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public String getDate(){
        return date;
    }

    public String getQuestioner(){
        return questioner;
    }

    public int getVote(){
        return vote;
    }
}
