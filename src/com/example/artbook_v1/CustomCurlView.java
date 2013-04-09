package com.example.artbook_v1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-7
 * Time: 下午8:35
 * To change this template use File | Settings | File Templates.
 */
public class CustomCurlView extends CurlView{
    public CustomCurlView(Context ctx) {
        super(ctx);
    }

    public CustomCurlView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public CustomCurlView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
    }

    @Override
    public boolean onTouch(View view, MotionEvent me) {
        return false;
    }
}
