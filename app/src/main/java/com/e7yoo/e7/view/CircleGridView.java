package com.e7yoo.e7.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by Administrator on 2017/10/12.
 */

public class CircleGridView extends GridView {
    private OnTouchInvalidPositionListener onTouchInvalidPositionListener;

    public CircleGridView(Context context) {
        super(context);
    }

    public CircleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //先创建一个监听接口，一旦点击了无效区域，便实现onTouchInvalidPosition方法，返回true or false来确认是否消费了这个事件
        if(onTouchInvalidPositionListener!=null){
            if(!isEnabled()){
                return isClickable()||isLongClickable();
            }
            int motionPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
            if(ev.getAction()==MotionEvent.ACTION_UP&&motionPosition == INVALID_POSITION){
                super.onTouchEvent(ev);
                return onTouchInvalidPositionListener.onTouchInvalidPosition(motionPosition);
            }
        }
        return super.onTouchEvent(ev);
    }

    public void setOnTouchInvalidPositionListener(
            OnTouchInvalidPositionListener onTouchInvalidPositionListener) {
        this.onTouchInvalidPositionListener = onTouchInvalidPositionListener;
    }

    public interface OnTouchInvalidPositionListener{
        boolean onTouchInvalidPosition(int motionEvent);
    }
}
