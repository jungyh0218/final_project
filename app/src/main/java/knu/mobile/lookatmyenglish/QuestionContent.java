package knu.mobile.lookatmyenglish;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by sec on 2015-12-17.
 */
public class QuestionContent implements Serializable{
    private int question_id;
    private String title;
    private String content;
    private String date;
    private String questioner;
    private int vote;

    public QuestionContent(int question_id, String title, String content, String questioner, String date, int vote){
        this.question_id = question_id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.questioner = questioner;
        this.vote = vote;
    }

    public int getQuestionId(){
        return this.question_id;
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
