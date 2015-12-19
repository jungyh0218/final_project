package knu.mobile.lookatmyenglish;

/**
 * Created by sec on 2015-12-19.
 */
public class AnswerContent{
    private int answer_id;
    private int question_id;
    private String title;
    private String content;
    private String answerer;
    private String date;
    private int vote;

    public AnswerContent(int answer_id, int question_id, String title, String content, String answerer,
                         String date, int vote){
        this.answer_id = answer_id;
        this.question_id=question_id;
        this.title = title;
        this.content = content;
        this.answerer = answerer;
        this.date = date;
        this.vote = vote;
    }

    public int getAnswerId(){
        return answer_id;
    }

    public int getQuestionId(){
        return question_id;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public String getAnswerer(){
        return answerer;
    }

    public String getDate(){
        return date;
    }

    public int getVote(){
        return vote;
    }


}
