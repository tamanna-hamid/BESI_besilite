package com.uva.inertia.besilite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class noSwipeViewPager extends android.support.v4.view.ViewPager {

    public noSwipeViewPager(Context con, AttributeSet attr){
        super(con, attr);
    }
    @Override
    public boolean onTouchEvent(MotionEvent evt){
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent evt) {
        return false;
    }
}