package com.example.eatFish;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
public class MainActivity extends  Activity{
    private TextView score;
    private TextView start;
    private LinearLayout end;
    private TextView fscore;
    private FishView fishView;
    private int s=0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
        setContentView(R.layout.activity_main);
        BtnListener listener=new BtnListener();
        findViewById(R.id.start).setOnClickListener(listener);
        findViewById(R.id.restart).setOnClickListener(listener);
        start = (TextView) findViewById(R.id.start);
        score = (TextView) findViewById(R.id.score);
        fscore=(TextView) findViewById(R.id.finalscore);
        end = (LinearLayout) findViewById(R.id.gamestop);
        fishView = (FishView) findViewById(R.id.fishView);
        fishView.ma=this;
    }
    
class BtnListener implements View.OnClickListener{
    public void onClick(View v){
        switch(v.getId()){
            case R.id.start:
            	starGame();
                break;
            case R.id.restart:
            	restartGame();
                break;
        }
    }
}
	private void starGame(){
	    fishView.start();
	    start.setVisibility(View.GONE);
	    score.setText("得分:0");
	    score.setVisibility(View.VISIBLE);
	}    
    private void restartGame(){
        fishView.start();
        end.setVisibility(View.GONE);
        score.setText("得分:0");
        score.setVisibility(View.VISIBLE);
    }
    public void setScore(int s) {
    	this.s=s;
        score.setText("得分:"+s);
    }
    public void stopGame(){
    	score.setVisibility(View.GONE);
    	fscore.setText("最终得分:"+s);
        end.setVisibility(View.VISIBLE);
    }
}
