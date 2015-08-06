package com.mystudy.ui;

import com.mystudy.ui.DragLayOut.Status;
import com.mystudy.ui.ResideMenu.State;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	private ResideMenu myDragLayOut;

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyLinearLayout(Context context) {
		this(context, null);
	}

	public ResideMenu getMyDragLayOut() {
		return myDragLayOut;
	}

	public void setMyDragLayOut(ResideMenu myDragLayOut) {
		this.myDragLayOut = myDragLayOut;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (myDragLayOut != null && myDragLayOut.getState() == State.close) {
			return super.onInterceptHoverEvent(ev);
		} else {
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (myDragLayOut != null && myDragLayOut.getState()  == State.close) {
			return super.onTouchEvent(event);
		} else {
			if (myDragLayOut != null
					&& event.getAction() == MotionEvent.ACTION_UP) {
				myDragLayOut.close(true);
			}
			return true;
		}
	}

}
