package com.example.eatFish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class FishView extends View{
    private Fish myFish = new Fish(); 
    private boolean bePressed = false;
    private int vwidth;
    private int vheight; 
    private ArrayList<Fish> fishList = new ArrayList<Fish>(); 
    private int score;
    private int fishSize[][]={{145,90},{179,136},{196,170},{261,164},{299,134},{242,176},{344,214}};
    private Paint paint0 = new Paint();
    private boolean gameStopped = true;
    public MainActivity ma;
    private Bitmap myFishBmp;
    private Matrix matrix = new Matrix();
    private Thread thread;


    public FishView(Context context){
        super(context);
    }

    public FishView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public FishView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public void start(){
        gameStopped = false;
        init();
    }
    private void init(){
        score = 0;
        vwidth = getWidth();
        vheight = getHeight();
        myFish = new Fish();
        myFish.isRight=true;
        myFish.size=2;
        myFishBmp =  getBmp(R.drawable.fp);
        myFish.width=210;
        myFish.height=170;
        ma.setScore(0);
        myFish.x=(vwidth/2-myFish.width/2);
        myFish.y=(vheight/2-myFish.height/2);
        for(int i=1; i<=7; ++i) {
            fishList.add( createFish() ) ;
        }
        thread = new Thread() {
            @Override
            public void run(){
                while(gameStopped!=true) {
                    fishMove();
                    handler.sendMessage(Message.obtain());
                    SystemClock.sleep(50);
                }
            }
        };
        thread.start();
    }
    public void stop(){
      gameStopped = true;
      fishList.clear();
        invalidate();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	check();
            invalidate();
            super.handleMessage(msg);
        }
    };
    
    private void fishMove(){
        synchronized (fishList){
            for (Fish fish : fishList){
                int x = fish.x;
                if (fish.isRight){
                    x+= fish.speed;
                    if (x > vwidth){
                    	setFish(fish);
                    }
                } else{
                    x-= fish.speed;
                    if (x + fish.width < 0) {
                    	setFish(fish);
                    }
                }
                fish.x=x;
            }
        }
    }
    
    private int getRandom(int i,int j){
    	return (int)(Math.random()*(j-i+1))+i;
    }
    private void setFish(Fish fish){
    	fish.size=getRandom(0,6);
        fish.isRight=(getRandom(0,1)==1);
        fish.width=(int) (fishSize[fish.size][0]);
        fish.height=fishSize[fish.size][1];
        fish.y=(getRandom(0, vheight));
        fish.speed=getRandom(5, 20);
        fish.x=fish.isRight?(-fish.width): vwidth;
    }
    
    private Fish createFish(){
        Fish fish = new Fish();
        setFish(fish);
        return fish;
    }

    private void check(){
        Rect rect = new Rect(myFish.x,myFish.y, myFish.x+myFish.width,myFish.y+myFish.height);
        synchronized (fishList) {
            for (Fish fish : fishList){
                if (rect.intersect(fish.x,fish.y,fish.x+fish.width,fish.y+fish.height)) {
                    if (fish.size <= myFish.size) {
                        score += fish.size+1;
                        setFish(fish);
                        if (ma != null){
                        	ma.setScore(score);
                        }
                    } else {
                            stop();
                            if (ma != null){
                            	ma.stopGame();
                                break;
                            }
                    }
                }
            }
        }
    }
    Bitmap getBmp(int id){
        return BitmapFactory.decodeResource(getResources(), id);
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(gameStopped==true){
            return;
        }
        if(myFish.isRight) {
        	matrix.setScale(-1f*myFish.width/myFishBmp.getWidth(),1f*myFish.height/myFishBmp.getHeight());
        	matrix.postTranslate(myFish.width,0);
        }else  {
        	matrix.setScale(1f*myFish.width/myFishBmp.getWidth(),1f*myFish.height/myFishBmp.getHeight());
        }
        matrix.postTranslate(myFish.x,myFish.y);
        canvas.drawBitmap(myFishBmp, matrix,paint0);
        synchronized (fishList){ 
        	Bitmap bmp;
            for (Fish fish : fishList) {
            	bmp= getBmp(R.drawable.f0+fish.size);
                if(fish.isRight) {
                	matrix.setScale(-1f*fish.width/bmp.getWidth(),1f*fish.height/bmp.getHeight());
                	matrix.postTranslate(fish.width,0);
                }else  {
                	matrix.setScale(1f*fish.width/bmp.getWidth(),1f*fish.height/bmp.getHeight());
                }
                matrix.postTranslate(fish.x,fish.y);
                canvas.drawBitmap(bmp, matrix,paint0);
                bmp.recycle();
            }
          
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Rect rect = new Rect(myFish.x,myFish.y, myFish.x +myFish.width,myFish.y+myFish.height);
                bePressed=rect.contains((int)event.getX(),(int)event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            	bePressed = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(bePressed){
                    myFish.move((int)event.getX(),(int)event.getY());
                    check();
                    invalidate();
                }
                break;
            default:
                break;
        }
        return true;
    }
    public class Fish {
        public int speed = 1; 
        public boolean isRight= true; 
        public int x= 0; 
        public int y= 0;
        public int width = 0;
        public int height = 0;
        public int size = 10; 

        public void move(int x0,int y0){
            int x1 = x0-width/2;
            y = y0-height/2;
            isRight = x1>x;
            x = x1;
        }
    }
}
