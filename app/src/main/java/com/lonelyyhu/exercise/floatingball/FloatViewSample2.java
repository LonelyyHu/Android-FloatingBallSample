package com.lonelyyhu.exercise.floatingball;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by hulonelyy on 2017/10/31.
 */

public class FloatViewSample2 implements View.OnTouchListener {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private float mLastX;
    private float mLastY;
    private boolean isHiddenWhenExit = false;
    private int mLayoutGravity = Gravity.LEFT | Gravity.TOP;
    private boolean isAdded;

    private FadeOutRunnable mFadeOutRunnable = new FadeOutRunnable();
    private boolean hasTouchFloatBall = false;

    private static final int MAX_ELEVATION = 64;
    private Params params;

    private Context context;
    private View ball;
    private int maxMarginLeft;
    private int maxMarginTop;
    private float downRelativeX;
    private float downRelativeY;
    private int screenWidth;
    private int screenHeight;
    private int statusBarHeight;

    public FloatViewSample2(Params params) {
        this.params = params;
        this.context = params.context;
        init();
    }

    public View getBall() {
        return ball;
    }

    public void setVisibility(int visibility) {
        if (ball != null) {
            ball.setVisibility(visibility);
        }
    }

    public void show() {
        if (mWindowManager == null) {
            createWindowManager();
        }
        if (mWindowManager == null) {
            return;
        }
        ViewParent parent = ball.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(ball);
        }
        setVisibility(VISIBLE);
        if (!isAdded) {
            mWindowManager.addView(ball, mLayoutParams);
//            fadeOutFloatBall();
            isAdded = true;
        }

        isHiddenWhenExit = false;
    }

    public void hide() {
        setVisibility(INVISIBLE);
        isHiddenWhenExit = true;
    }

    public void destory() {
        if (isAdded && mWindowManager != null) {
            mWindowManager.removeView(ball);
        }
//        removeCallbacks(mFadeOutRunnable);
//        stopClipRunner();
//        stopScrollRunner();
        isAdded = false;
    }

    private void fadeOutFloatBall() {
        ball.removeCallbacks(mFadeOutRunnable);
        ball.postDelayed(mFadeOutRunnable, 2000);
    }

    /**
     * 设置WindowManager
     */
    private void createWindowManager() {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = mLayoutGravity;
        mLayoutParams.width = params.width;
        mLayoutParams.height = params.height;
        mLayoutParams.x=0;
        mLayoutParams.y=0;
    }

    private void init() {
        if (params.ball == null) {
            ball = new View(context);
        } else {
            ball = params.ball;
        }

        if (params.resId != 0) {
            ball.setBackgroundResource(params.resId);
        }

        ball.setBackgroundColor(Color.RED);

        ball.setOnTouchListener(this);

        if (params.onClickListener != null) {

            Log.wtf("FloatViewSample2", "init => setOnClickListener");

            ball.setOnClickListener(params.onClickListener);
        }


        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        statusBarHeight = getTopStatusBarHeight();
        maxMarginTop = screenHeight - params.height - statusBarHeight;
        maxMarginLeft = screenWidth - params.width;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.wtf("FloatView2", "onTouch => RawX: " + event.getRawX());
        Log.wtf("FloatView2", "onTouch => RawY: " + event.getRawY());
        Log.wtf("FloatView2", "onTouch => X: " + event.getX());
        Log.wtf("FloatView2", "onTouch => Y: " + event.getY());


        final int touchX = (int) event.getRawX();
        final int touchY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downRelativeX = event.getX();
                downRelativeY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                if ((touchX+params.width/2) > screenWidth/2) {
                    mLayoutParams.x = screenWidth - params.width;
                } else {
                    mLayoutParams.x = 0;
                }

                mLayoutParams.y = (int)(touchY - downRelativeY - statusBarHeight);
                if (mLayoutParams.y < 0) {
                    mLayoutParams.y = 0;
                } else if (mLayoutParams.y > maxMarginTop) {
                    mLayoutParams.y = maxMarginTop;
                }

                mWindowManager.updateViewLayout(ball, mLayoutParams);
                break;
            case MotionEvent.ACTION_MOVE:
                mLayoutParams.x = (int)(touchX-downRelativeX);
                mLayoutParams.y = (int)(touchY-downRelativeY - statusBarHeight);

                if (mLayoutParams.y < 0) {
                    mLayoutParams.y = 0;
                } else if (mLayoutParams.y > maxMarginTop) {
                    mLayoutParams.y = maxMarginTop;
                }

                if (mLayoutParams.x > maxMarginLeft) {
                    mLayoutParams.x = maxMarginLeft;
                } else if (mLayoutParams.x < 0) {
                    mLayoutParams.x = 0;
                }

//                mLayoutParams.x = 0;
//                mLayoutParams.y = 100;

                Log.wtf("FloatView2", "onTouch => mLayoutParams.x: " + mLayoutParams.x);
                Log.wtf("FloatView2", "onTouch => mLayoutParams.y: " + mLayoutParams.y);

                mWindowManager.updateViewLayout(ball, mLayoutParams);
                break;
        }

        Log.wtf("FloatView2", "getTopStatusBarHeight: " + statusBarHeight);

        return true;
    }

    private int getTopStatusBarHeight () {
//        Rect rectangle = new Rect();
//        Window window = ac.getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
//        return rectangle.top;

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;

    }

    public static class Builder {
        private Params P;

        public Builder(Context context, ViewGroup rootView) {
            P = new Params(context);
        }

        public Builder setRightMargin(int rightMargin) {
            P.rightMargin = rightMargin;
            return this;
        }

        public Builder setBottomMargin(int bottomMargin) {
            P.bottomMargin = bottomMargin;
            return this;
        }

        public Builder setWidth(int width) {
            P.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            P.height = height;
            return this;
        }

        public Builder setRes(int resId) {
            P.resId = resId;
            return this;
        }

        public Builder setBall(View view) {
            P.ball = view;
            return this;
        }

        public Builder setDuration(int duration) {
            P.duration = duration;
            return this;
        }

        public Builder setOnClickListener(View.OnClickListener onClickListener) {
            P.onClickListener = onClickListener;
            return this;
        }

        public FloatViewSample2 build() {
            FloatViewSample2 floatBall = new FloatViewSample2(P);
            return floatBall;
        }
    }

    private static class Params {
        public static final int DEFAULT_BALL_WIDTH = 180;
        public static final int DEFAULT_BALL_HEIGHT = 180;
        public static final int DEFAULT_DURATION = 500;
        private int duration = DEFAULT_DURATION;
        private Context context;
        private int rightMargin;
        private int bottomMargin;
        private int resId;
        private int width = DEFAULT_BALL_WIDTH;
        private int height = DEFAULT_BALL_HEIGHT;
        private View.OnClickListener onClickListener;
        private View ball;

        public Params(Context context) {
            this.context = context;
        }
    }

    private class FadeOutRunnable implements Runnable {

        @Override
        public void run() {
            if (!hasTouchFloatBall) {
                ball.setBackgroundResource(params.resId);
            }
        }
    }


}
