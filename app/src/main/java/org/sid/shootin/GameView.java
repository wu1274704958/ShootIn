package org.sid.shootin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends View{
    private static String LT = "GV";
    private static final float BALL_X = 0.1f;

    private int Width;
    private int Height;
    private float bfy30; // 百分之30 高
    private float bfy90; // 百分之90 高
    private float bfx5;  // 百分之5 W
    private float bfx66;  // 百分之66 W
    private float bfy22;  // 百分之22 H
    private float bfx30;
    private float bfy80;
    private float mid_x;
    private float mid_y;
    private float bfy5;
    private Paint paint_line;
    private Paint paint_ball;
    private Vec2 ball_pos;
    private Vec2 bvpos;
    private Vec2 begin_pos;
    private int delatime;
    private long lastTimeTick;
    private MyHandler handler;
    private Timer timer;
    private boolean inThere;
    private boolean isFZ;
    private RectF beginRect;
    private RectF beginRect_pressed;
    State state = State.Pause;
    private Paint paint_begin;
    private boolean beginIsPressed = false;
    private boolean isHand = false;
    private RectF handRect;
    private Vec2 handPos;
    private Vec2 hvpos;
    private Vec2 lastPos;
    private float zdgdc; //最低高度差
    private float zdkdc; //最低宽度差
    public enum State{
        Pause,
        Playing,
        Finish
    }

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init()
    {
        Width = Converter.getInstance().getW();
        Height = Converter.getInstance().getH();

        isFZ = true;
        inThere = isFZ;

        bfy30 = (float)Height * 0.3f;
        bfy90 = (float)Height * 0.9f;
        bfx5 = (float)Width * 0.05f;
        bfx66 = (float)Width * 0.66f;
        bfy22 = (float)Height * 0.22f;
        bfx30 = (float)Width * 0.3f;
        bfy5 = (float)Height * 0.05f;
        bfy80 = (float)Height * 0.8f;
        float bx = (Width - bfx66) / 2.f;
        float by = (Height - bfy22) / 2.f;
        mid_x = Width / 2.0f;
        mid_y = Height / 2.0f;
        beginRect = new RectF( bx, by, bx + bfx66 , by + bfy22);
        beginRect_pressed = new RectF(beginRect.left + 5.f , beginRect.top + 5.f ,
                beginRect.right - 5.f , beginRect.bottom - 5.f);


        ball_pos = new Vec2(mid_x,mid_y);

        hvpos = new Vec2();
        lastPos = new Vec2();


        paint_line = new Paint();
        paint_line.setColor(Color.RED);
        paint_line.setStyle(Paint.Style.STROKE);

        paint_ball = new Paint();
        paint_ball.setAntiAlias(true);
        paint_ball.setDither(true);
        paint_ball.setColor(0xFF00aaaa);

        paint_begin = new Paint();
        paint_begin.setAntiAlias(true);
        paint_begin.setDither(true);
        paint_begin.setColor(0xFF00aaaa);
        paint_begin.setStyle(Paint.Style.FILL);
        paint_begin.setTextAlign(Paint.Align.CENTER);
        float begin_height = (bfx66 * 0.7f) / 4.0f;
        paint_begin.setTextSize( begin_height );
        Paint.FontMetrics fm = paint_begin.getFontMetrics();
        Log.e(LT, " " + begin_height );
        //Log.e(LT,""+ fm.descent + "  " + fm.ascent + "   " + fm.bottom + "  " + fm.top + " " + fm.leading);
        begin_pos = new Vec2(mid_x,mid_y + fm.descent);
        bvpos = new Vec2(1.2f,2.0f);

        handRect = new RectF(-bfx30 / 2.f,-bfy5 / 2.f,bfx30 / 2.f,bfy5 / 2.f );
        handPos = new Vec2(mid_x,bfy80);

        zdgdc = -handRect.top + bfx5;
        zdkdc = -handRect.left + bfx5;

        lastTimeTick = System.currentTimeMillis();
        handler = new MyHandler(new SoftReference<GameView>(this));
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },16,16);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = System.currentTimeMillis();
        delatime = (int)(now - lastTimeTick);

        canvas.drawARGB(255,0,0,0);
        switch (state)
        {
            case Playing:
                canvas.drawLine(0.f,bfy30,(float)Width,bfy30,paint_line);
                canvas.drawLine(0.f,bfy90,(float)Width,bfy90,paint_line);
                if(inThere) {
                    drawBall(canvas);
                    drawHand(canvas);
                    step(canvas);
                }
                break;
            case Pause:
                drawPause(canvas);
                break;
            case Finish:

                break;
        }
        lastTimeTick = System.currentTimeMillis();
    }

    void drawBall(Canvas canvas)
    {
        paint_ball.setStyle(Paint.Style.FILL);
        canvas.drawCircle(ball_pos.x,ball_pos.y,bfx5,paint_ball);
    }

    void drawHand(Canvas canvas)
    {
        int save = canvas.save();

        canvas.translate(handPos.x,handPos.y);
        canvas.drawRoundRect(handRect,10,10,paint_ball);

        canvas.restoreToCount(save);
    }

    void drawPause(Canvas canvas)
    {
        paint_begin.setStyle(Paint.Style.STROKE);
        if(!beginIsPressed)
            canvas.drawRoundRect(beginRect,20,20,paint_begin);
        else
            canvas.drawRoundRect(beginRect_pressed,20,20,paint_begin);
        //paint_begin.setStyle(Paint.Style.STROKE);

        canvas.drawText("开始游戏",begin_pos.x,begin_pos.y,paint_begin);
    }

    void step(Canvas canvas)
    {
        ball_pos.x += bvpos.x * BALL_X * delatime;
        ball_pos.y += bvpos.y * BALL_X * delatime;



        if(bvpos.x >= 0 && ball_pos.x + bfx5 > Width)
            bvpos.x = -bvpos.x;
        if(bvpos.x <= 0 && ball_pos.x < bfx5)
            bvpos.x = -bvpos.x;
        if(bvpos.y >= 0 && ball_pos.y + bfx5 > Height)
            bvpos.y = -bvpos.y;
        if(bvpos.y <= 0 && ball_pos.y < bfx5)
            bvpos.y = -bvpos.y;

        RectF temp_ball = new RectF(ball_pos.x - bfx5,ball_pos.y - bfx5 ,
                ball_pos.x + bfx5 , ball_pos.y + bfx5);

        RectF temp_hand = new RectF(handPos.x + handRect.left,handPos.y + handRect.top ,
                handPos.x + handRect.right , handPos.y + handRect.bottom);



        if(temp_ball.bottom > temp_hand.top && temp_ball.top < temp_hand.bottom
                && temp_ball.right > temp_hand.left && temp_ball.left < temp_hand.right)
        {
//            paint_line.setColor(Color.YELLOW);
//            canvas.drawRect(temp_ball,paint_line);
//            canvas.drawRect(temp_hand,paint_line);

            float gdc = temp_hand.top - temp_ball.top;
            float kdc = temp_hand.left - temp_ball.left;
            if(gdc > 0 && kdc > 0)
            {
                float t_gdc = temp_ball.bottom - temp_hand.top;
                float t_kdc = temp_ball.right - temp_hand.left;
                if(Math.abs(t_gdc - t_kdc) <= 0.001)
                {
                    bvpos.x = -bvpos.x;
                    bvpos.y = -bvpos.y;
                }
                if(t_kdc > t_gdc)
                {
                    bvpos.x = -bvpos.x;
                }else {
                    bvpos.y = -bvpos.y;
                }

                ball_pos.x -= t_kdc;
                ball_pos.y -= t_gdc;
            }else {
                float t_gdc = temp_hand.bottom - temp_ball.top;
                float t_kdc = temp_hand.right - temp_ball.left;
                if(Math.abs(t_gdc - t_kdc) <= 0.001)
                {
                    bvpos.x = -bvpos.x;
                    bvpos.y = -bvpos.y;
                }
                if(t_kdc > t_gdc)
                {
                    bvpos.x = -bvpos.x;
                }else {
                    bvpos.y = -bvpos.y;
                }
                ball_pos.x += t_kdc;
                ball_pos.y += t_gdc;
            }
        }

        paint_line.setColor(Color.RED);
        //bvpos.x -= 0.001f;
        //bvpos.y -= 0.001f;
    }
    static class MyHandler extends Handler{
        SoftReference<GameView> gv;
        public MyHandler(SoftReference<GameView> gv){
            this.gv = gv;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                {
                    GameView v = gv.get();
                    if(v != null)
                    {
                        v.invalidate();
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(state == State.Pause) {
                    if (beginRect.contains(event.getX(), event.getY())) {
                        beginIsPressed = true;
                    }
                }else if(state == State.Playing){
                    RectF temp_hand = new RectF(handPos.x + handRect.left,handPos.y + handRect.top ,
                            handPos.x + handRect.right , handPos.y + handRect.bottom);
                    if(temp_hand.contains(event.getX(),event.getY()))
                    {
                        isHand = true;
                        lastPos.x = event.getX();
                        lastPos.y = event.getY();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(state == State.Pause) {
                    if (beginRect.contains(event.getX(), event.getY())) {
                        beginIsPressed = false;
                        Log.e(LT,"Playing");
                        state = State.Playing;
                    }
                }else if(state == State.Playing)
                {
                    hvpos.x = 0.f;
                    hvpos.y = 0.f;
                    isHand = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isHand)
                {
                    hvpos.x = lastPos.x -  handPos.x ;
                    hvpos.y = lastPos.y - handPos.y;
//                    Log.e(LT," " + handPos.x + "  " + handPos.y);
//                    Log.e(LT," " + lastPos.x + "  " + lastPos.y);
//                    Log.e(LT," " + hvpos.x + "  " + hvpos.y);
                    handPos.x += hvpos.x;
                    handPos.y += hvpos.y;

                    lastPos.x = event.getX();
                    lastPos.y = event.getY();
                }
                break;
        }
        return true;
    }

    public void stop()
    {
        state = State.Finish;
    }
    public void pause()
    {
        state = State.Pause;
    }
    public void destroy()
    {
        timer.cancel();
    }
}
