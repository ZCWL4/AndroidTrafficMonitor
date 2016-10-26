package com.example.yp.androidtrafficmonitor.ui;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yp.androidtrafficmonitor.R;

/**
 * Created by yp on 2016/10/20.
 */
public class FloatView {

    WindowManager mWManger;
    WindowManager.LayoutParams mWManParams;

    public View view;

    //初始位置
    private float startX;
    private float startY;

    //坐标
    private float x;
    private float y;

    //
    private float mTouchSatrtX;
    private float mTouchStartY;

    //组件
    public TextView tv_show;

    Context mContext;

    public FloatView(Context context) {
        this.mContext = context;
    }

    public void destroy(){
        mWManger.removeView(view);
    }

    /**
     * 初始化mWManger,mWManParams
     */

    public void setSpeedView(String speed){
        tv_show.setText(speed);
    }

    public void show(){
        mWManger = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWManParams = new WindowManager.LayoutParams();

        //设置LayoutParams的参数
        mWManParams.type = 2002;//设置系统级窗口
        mWManParams.flags |= 8;
        //调整悬浮窗到左上角
        mWManParams.gravity = Gravity.TOP|Gravity.LEFT;

        //以屏幕左上角为源点，设置x，y
        mWManParams.x = 100;
        mWManParams.y = 100;

        //悬浮窗的长宽数据
        mWManParams.width = 200;
        mWManParams.height = 50;



        mWManParams.format = -3;//透明

        //加载悬浮窗布局文件
        view = LayoutInflater.from(mContext).inflate(R.layout.traffic_float_view, null);


        mWManger.addView(view, mWManParams);


        view.setOnTouchListener(new View.OnTouchListener() {
            /**
             * 改变悬浮窗位置
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取相对屏幕的位置，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY() - 25;//25为系统状态栏的高度
                //Log.e("初始位置", x+"======="+y);

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;

                        //获取相对View的坐标,以view的左上角为原点
                        mTouchSatrtX = event.getX();
                        mTouchStartY = event.getY();

                        //Log.e("相对view的位置", mTouchSatrtX+"--------"+mTouchStartY);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatePosition();

                        break;

                    case MotionEvent.ACTION_UP:
                        updatePosition();

                        //show_img_close();

                        mTouchSatrtX = mTouchStartY =0;

                        break;
                }



                return true;
            }
        });

        //img_close = (ImageView) view.findViewById(R.id.img_close);
        /**
         * 关闭悬浮窗图标点击事件
         */
        /*img_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext,Traffic_Service.class);
                mContext.stopService(intent);
                view.setVisibility(View.GONE);
            }
        });
*/

        tv_show = (TextView) view.findViewById(R.id.floatTv);



    }

    /*
    消除悬浮窗
     */
    public void remove(){
        mWManger.removeView(view);
    }

    /**
     * 更新悬浮窗的位置
     *
     */
    public void updatePosition(){
        mWManParams.x = (int) (x - mTouchSatrtX);
        mWManParams.y = (int) (y - mTouchStartY);

        mWManger.updateViewLayout(view, mWManParams);
    }

    /**
     * 控制关闭悬浮窗按钮的显示与隐藏
     */
    /*public void show_img_close() {
        if (Math.abs(x - startX) < 1.5 && Math.abs(y - startY) < 1.5
                && !img_close.isShown()) {
            img_close.setVisibility(View.VISIBLE);
        } else if (img_close.isShown()) {
            img_close.setVisibility(View.GONE);
        }
    }*/

}