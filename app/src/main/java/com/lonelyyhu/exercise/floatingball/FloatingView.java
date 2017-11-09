package com.lonelyyhu.exercise.floatingball;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by hulonelyy on 2017/11/2.
 *
 */

public class FloatingView implements View.OnTouchListener {

    private static final int MAX_ELEVATION = 64;
    private Params params;

    private Context context;
    private View fv;
    private int maxMarginLeft;
    private int maxMarginTop;
    private int minMarginLeft;
    private int downX;
    private int downY;
    private int xDelta;
    private int yDelta;
    private DisplayMetrics dm;
    private boolean isAddedToView;

    public FloatingView(Params params) {
        this.params = params;
        this.context = params.context;
        init();
    }

    public View getFloatingView() {
        return fv;
    }

    public void show() {

        if (fv == null) {
            return;
        }

        if (isAddedToView) {
            fv.setVisibility(View.VISIBLE);
        } else {
            params.rootView.addView(fv);
            isAddedToView = true;
        }
    }

    public void hide() {
        if (fv == null) {
            return;
        }

        if (isAddedToView) {
            fv.setVisibility(View.GONE);
            Log.wtf("FloatingView", "hide: set to gone");
        }
    }


    private void init() {
        if (params.customeView == null) {
            fv = new View(context);
        } else {
            fv = params.customeView;
        }

        if (params.resId != 0) {
            fv.setBackgroundResource(params.resId);
        }

        fv.setOnTouchListener(this);
        ViewCompat.setElevation(fv, MAX_ELEVATION);

        ViewGroup.MarginLayoutParams layoutParams;

        if (params.rootView instanceof FrameLayout) {
            layoutParams = new FrameLayout.LayoutParams(params.width, params.height);
        } else if (params.rootView instanceof RelativeLayout) {
            layoutParams = new RelativeLayout.LayoutParams(params.width, params.height);
        } else {
            layoutParams = new ViewGroup.MarginLayoutParams(params.width, params.height);
        }

        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels;

        maxMarginLeft = screenWidth - params.width - params.rightMargin;
        minMarginLeft = params.leftMargin;
        layoutParams.width = params.width;
        layoutParams.height = params.height;
        layoutParams.leftMargin = maxMarginLeft - params.rightMargin;

        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.rightMargin = 0;


        fv.setLayoutParams(layoutParams);

        ViewTreeObserver observer = params.rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Log.wtf("FloatViewSample1", "onGlobalLayout !!");

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    params.rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    params.rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                int rootViewHeight = params.rootView.getMeasuredHeight();
                maxMarginTop = rootViewHeight - params.height;

                ViewGroup.MarginLayoutParams layoutParams;

                if (params.rootView instanceof FrameLayout) {
                    layoutParams = (FrameLayout.LayoutParams) fv.getLayoutParams();
                } else if (params.rootView instanceof RelativeLayout) {
                    layoutParams = (RelativeLayout.LayoutParams) fv.getLayoutParams();
                } else {
                    layoutParams = (ViewGroup.MarginLayoutParams) fv.getLayoutParams();
                }

                layoutParams.topMargin = maxMarginTop - params.bottomMargin;

                fv.setLayoutParams(layoutParams);

                if (params.showOnStart) {
                    show();
                } else {
                    hide();
                }

                fv.getRootView().invalidate();

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int touchX = (int) event.getRawX();
        final int touchY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = touchX;
                downY = touchY;
                ViewGroup.MarginLayoutParams lParams = (ViewGroup.MarginLayoutParams) fv
                        .getLayoutParams();
                xDelta = touchX - lParams.leftMargin;
                yDelta = touchY - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                if (downX == touchX && downY == touchY) {
                    if (params.onClickListener != null) {
                        params.onClickListener.onClick(fv);
                    }
                } else {
                    Animation animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) fv.getLayoutParams();

                            int curLeftMargin = layoutParams.leftMargin;

                            if (touchX < dm.widthPixels / 2) {
                                layoutParams.leftMargin = (int) (curLeftMargin - (curLeftMargin - minMarginLeft) * interpolatedTime);
                            } else {
                                layoutParams.leftMargin = (int) (curLeftMargin + (maxMarginLeft - curLeftMargin) * interpolatedTime);
                            }

                            fv.setLayoutParams(layoutParams);
                        }
                    };
                    animation.setDuration(params.duration);
                    fv.startAnimation(animation);
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                ViewGroup.MarginLayoutParams layoutParams;

                if (params.rootView instanceof FrameLayout) {
                    layoutParams = (FrameLayout.LayoutParams) fv.getLayoutParams();
                } else if (params.rootView instanceof RelativeLayout) {
                    layoutParams = (RelativeLayout.LayoutParams) fv.getLayoutParams();
                } else {
                    layoutParams = (ViewGroup.MarginLayoutParams) fv.getLayoutParams();
                }

                int leftMargin;

                if (touchX - xDelta <= minMarginLeft) {
                    leftMargin = minMarginLeft;
                } else if (touchX - xDelta < maxMarginLeft) {
                    leftMargin = touchX - xDelta;
                } else {
                    leftMargin = maxMarginLeft;
                }

                layoutParams.leftMargin = leftMargin;

                int topMargin;

                if (touchY - yDelta <= 0) {
                    topMargin = 0;
                } else if (touchY - yDelta < maxMarginTop) {
                    topMargin = touchY - yDelta;
                } else {
                    topMargin = maxMarginTop;
                }

                layoutParams.topMargin = topMargin;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                fv.setLayoutParams(layoutParams);
                break;
        }

        fv.getRootView().invalidate();

        return true;
    }

    public static class Builder {
        protected Params P;

        public Builder(Context context, ViewGroup rootView) {
            P = new Params(context);
            P.rootView = rootView;
        }

        public Builder setRightMargin(int rightMargin) {
            P.rightMargin = rightMargin;
            return this;
        }

        public Builder setLeftMargin(int leftMargin) {
            P.leftMargin = leftMargin;
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

        public Builder setView(View view) {
            P.customeView = view;
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

        public Builder setShowOnStart(boolean showOnStart) {
            P.showOnStart = showOnStart;
            return this;
        }


        public FloatingView build() {
            FloatingView floatBall = new FloatingView(P);
            return floatBall;
        }
    }

    protected static class Params {
        public static final int DEFAULT_BALL_WIDTH = 180;
        public static final int DEFAULT_BALL_HEIGHT = 180;
        public static final int DEFAULT_DURATION = 500;
        private int duration = DEFAULT_DURATION;
        private Context context;
        private int leftMargin;
        private int rightMargin;
        private int bottomMargin;
        private int resId;
        private int width = DEFAULT_BALL_WIDTH;
        private int height = DEFAULT_BALL_HEIGHT;
        private ViewGroup rootView;
        private View.OnClickListener onClickListener;
        private View customeView;
        private boolean showOnStart;

        public Params(Context context) {
            this.context = context;
        }
    }


}
