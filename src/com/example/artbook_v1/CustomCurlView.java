package com.example.artbook_v1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
	
	private boolean isIntercept = true;
	
	private  OnViewCompleteListener onViewCompleteListener;
	
	public interface OnViewCompleteListener{
		public void onViewCompleteListener();
	}
	
	public void setOnViewCompleteListener(OnViewCompleteListener onViewCompleteListener){
		this.onViewCompleteListener = onViewCompleteListener;
	}
	
	
    public boolean isIntercept() {
		return isIntercept;
	}

	public void setIntercept(boolean isIntercept) {
		this.isIntercept = isIntercept;
	}

	public CustomCurlView(Context ctx) {
        super(ctx);
    }

    public CustomCurlView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public CustomCurlView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        this.setOnTouchListener(this);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(onViewCompleteListener!=null){
        	onViewCompleteListener.onViewCompleteListener();
        }
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent me) {
//    	Log.e("$$$$$$$$$$$$ddd$$$$$$$$$$", "时间"+isIntercept);
//    	if(!isIntercept){
//    		this.setVisibility(View.GONE);
//    		Log.e("$$$$$$$$$$$$$$$$$$$$$$$", "吟唱curlview");
//    		return true;
//    	}
//    	return super.onTouch(view, me);
//    }
}
