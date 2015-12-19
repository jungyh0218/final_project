package knu.mobile.lookatmyenglish;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        final EditText keywordText = (EditText)findViewById(R.id.editTextSearch);
        ImageButton searchButton = (ImageButton)findViewById(R.id.imageButtonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordText.getText().toString();
                DBContentProvider dbContentProvider = new DBContentProvider(SearchResultActivity.this);
                //ArrayList<QuestionContent> result
                //= (ArrayList<QuestionContent>)dbContentProvider.searchQuestion(keyword);
                searchQuestion(keyword);

            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHome: //홈
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;

            case R.id.buttonQuestion: //질문
                i = new Intent(this, QuestionActivity.class);
                if(!SignInActivity.isLoggedIn)
                    i = new Intent(this, SignInActivity.class);
                startActivity(i);
                break;
        }
    }

    private class PHPDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String link = args[1];
            link += "?keyword="+args[2];
            try {
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    // conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("Async", "Returned");
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            String id;
            try {
                if(str.equals("no data\n")){
                    Toast toast = Toast.makeText(SearchResultActivity.this, "검색결과가 존재하지 않습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                JSONArray jsonArray = new JSONArray(str);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject job = jsonArray.getJSONObject(i);
                    QuestionContent content = new QuestionContent(
                            Integer.parseInt(job.get("question_id").toString()),
                            job.get("title").toString(),
                            job.get("content").toString(),
                            job.get("questioner").toString(),
                            job.get("upload_date").toString(),
                            Integer.parseInt(job.get("vote").toString())
                    );
                    questionList.add(content);
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            ImageView imageViewMark = (ImageView) findViewById(R.id.imageViewMark);
            TextView textView = (TextView) findViewById(R.id.textViewInfo);

        //ListView 출력
            ListView listView = (ListView) findViewById(R.id.listView);
            QuestionListAdapter adapter = new QuestionListAdapter(mContext,0,questionList);
            listView.setAdapter(adapter);

            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageViewMark.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SearchResultActivity.this, QuestionViewActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private class QuestionListAdapter extends ArrayAdapter<QuestionContent> {

        private ArrayList<QuestionContent> mQuestionData;

        public QuestionListAdapter(Context context, int resource, ArrayList<QuestionContent> questionData) {
            super(context, resource, questionData);

            mQuestionData = questionData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View rowView= inflater.inflate(R.layout.item, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewTitle);
            TextView txtContent = (TextView) rowView.findViewById(R.id.textViewContent);
            TextView txtDate = (TextView) rowView.findViewById(R.id.textViewTime);

            txtTitle.setText(mQuestionData.get(position).getTitle());
            if(mQuestionData.get(position).getContent().length()>30)
                txtContent.setText(mQuestionData.get(position).getContent().substring(0,30)+"...");
            else
                txtContent.setText(mQuestionData.get(position).getContent());
            txtDate.setText(mQuestionData.get(position).getDate());

            return rowView;
        }
    }


    ArrayList<QuestionContent> questionList;
    PHPDown task2;
    public void searchQuestion(String keyword){
        questionList = new ArrayList<QuestionContent>();
        String link = "http://knucsewiki.ivyro.net/select_question.php";
        task2 = new PHPDown();
        task2.execute("Query", link, keyword);
    }
}
