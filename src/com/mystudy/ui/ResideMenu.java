package com.mystudy.ui;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.provider.OpenableColumns;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ResideMenu extends FrameLayout {

	/**
	 * the draghelper is controller that controls the view group drags
	 */
	private ViewDragHelper mDragHelper;

	/**
	 * the first viewgroup(the left panel)
	 */
	private ViewGroup leftGroup;

	/**
	 * the second viewgroup(the main panel)
	 */
	private ViewGroup mainGroup;

	/**
	 * the first viewgroup's state
	 */
	private State state;

	/**
	 * the control's width
	 */
	private int mWidth;
	/**
	 * the control's height
	 */
	private int mHeight;

	/**
	 * the 60% control's width
	 */
	private int mRangeWidth;
	/**
	 * the OnDragEventListener instance
	 */
	private OnDragEventListener dragEventListener;

	/**
	 * the interface that observe the panel's state change while do sth
	 */
	public interface OnDragEventListener {
		/**
		 * do sth while the state is open
		 */
		void onOpen();

		/**
		 * do sth while the state is close
		 */
		void onClose();

		/**
		 * do sth while the state is dragging
		 */
		void onDragging(float per);
	}

	/**
	 * the state enumration that reprents the first panel's state close the
	 * first viewgroup state is closed open the first viewgroup's state is open
	 * drag the first viewgroup's state is draging
	 * 
	 * @author Administrator
	 * 
	 */
	public enum State {
		close, open, draging
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public OnDragEventListener getDragEventListener() {
		return dragEventListener;
	}

	public void setDragEventListener(OnDragEventListener dragEventListener) {
		this.dragEventListener = dragEventListener;
	}

	/**
	 * the one paremeter constructor that called two parameter constructor
	 * 
	 * @param context
	 *            the parameter context
	 */
	public ResideMenu(Context context) {
		this(context, null);
	}

	/**
	 * the three parameter contructor that initializing data
	 * 
	 * @param context
	 *            the parameter context
	 * @param attrs
	 *            the parameter that represent attributes
	 * @param defStyle
	 *            the parameter that reprensent default style
	 */
	public ResideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * the function that initialize data
	 */
	private void init() {
		mDragHelper = ViewDragHelper.create(this, new Callback() {

			@Override
			public boolean tryCaptureView(View arg0, int arg1) {
				return true;
			}

			/**
			 * the function that return the horizontal-drag's range the result
			 * is 60% screen-width
			 */
			@Override
			public int getViewHorizontalDragRange(View child) {
				return mRangeWidth;
			}
			@Override
			public void onViewCaptured(View capturedChild, int activePointerId) {
				super.onViewCaptured(capturedChild, activePointerId);
			}
			/**
			 * the function that control the panel's range width is 0-60% width
			 */
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				// if the child view is main group control the range width is
				// 0-60% width
				if (child == mainGroup) {
					left = fixLeft(left);
				}
				return left;
			}

			/**
			 * the function is main function that controls the main panel
			 * range's width is 0-60% width with follow animaion and redraw the
			 * canvas so that do function below the android api level 10
			 */
			@Override
			public void onViewPositionChanged(View changedView, int left,
					int top, int dx, int dy) {
				super.onViewPositionChanged(changedView, left, top, dx, dy);
				int newleft = left;
				if (changedView == leftGroup) {
					newleft = mainGroup.getLeft() + dx;
					newleft = fixLeft(newleft);
					// force the first viewgroup layout 0,0
					leftGroup.layout(0, 0, mWidth, mHeight);
					// the main panel dyamic range is 0 -60%
					mainGroup.layout(newleft, 0, newleft + mWidth, mHeight);
				}
				// follow animation and event
				dispatchAnimation(newleft);
				// redraw the canvas
				invalidate();
			}

			/**
			 * the function that call when your hand released
			 */
			@Override
			public void onViewReleased(View releasedChild, float xvel,
					float yvel) {
				super.onViewReleased(releasedChild, xvel, yvel);
				// when main panel's translation beyond 30% width or xval beyond
				// 0 open the left panel
				if ((xvel == 0 && mainGroup.getLeft() > mRangeWidth / 2)
						|| xvel > 0) {
					open();
					// otherwise close the left panel
				} else if (xvel < 0) {
					close();
				}
			}
			@Override
			public void onViewDragStateChanged(int state) {
				// TODO Auto-generated method stub
				super.onViewDragStateChanged(state);
			}
		});

	}

	/**
	 * the function that compute's scroll with the smooth animation
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		// if the drag helper continue sett
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/**
	 * the function that close left panel
	 */
	protected void close() {
		close(true);
	}

	/**
	 * the function that close left panel with animation
	 * 
	 * @param b
	 */
	protected void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			// 1. start the smooth animation
			if (mDragHelper.smoothSlideViewTo(mainGroup, finalLeft, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			// the main group's layoutt
			mainGroup.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	/**
	 * the function that open left panel
	 */
	protected void open() {
		open(true);
	}

	/**
	 * the function that open left panel with animation
	 */
	private void open(boolean isSmooth) {
		int finalLeft = mRangeWidth;
		if (isSmooth) {
			// 1. start the smooth animation
			if (mDragHelper.smoothSlideViewTo(mainGroup, finalLeft, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			// the main group's layoutt
			mainGroup.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	/**
	 * the function that control follow-animation and event
	 * 
	 * @param newleft
	 *            the left width is dynamic number
	 */
	protected void dispatchAnimation(int newleft) {
		// calc the new left width percent
		float percent = newleft * 1.0f / mRangeWidth;
		// call the onDragging function
		if (dragEventListener != null) {
			dragEventListener.onDragging(percent);
		}
		State curState = state;

		
		// judge the left panel state
		// whether equals the last state
		// if the state is closed call the close function
		// or the state is open that call open funtion
		state = updateState(percent);
		if (curState != state) {
			if (state == State.close) {
				if (dragEventListener != null) {
					dragEventListener.onClose();
				}
			} else if (state == State.open) {
				if (dragEventListener != null) {
					dragEventListener.onOpen();
				}
			}
		}
		setAnimation(percent);
	}

	/**
	 * start any animation through percent
	 * 
	 * @param percent
	 *            the percent start any animation
	 */
	private void setAnimation(float percent) {

		// the left panel's animation
		// the left panel's scale of x is 0.5-1
		ViewHelper.setScaleX(leftGroup, evalute(percent, 0.5f, 1.0f));
		// the left panel's scale of y is 0.5-1
		ViewHelper.setScaleY(leftGroup, evalute(percent, 0.5f, 1.0f));
		// the left panel's tranlation range is -rangewidth-0
		ViewHelper
				.setTranslationX(leftGroup, evalute(percent, -mRangeWidth, 0));
		// the left panel's alpha range is 0.5-1
		ViewHelper.setAlpha(leftGroup, evalute(percent, 0.5f, 1.0f));

		// the main panel's animation
		// the main panel's scale of x is 1.0-0.8
		ViewHelper.setScaleX(mainGroup, evalute(percent, 1.0f, 0.8f));
		// the main panel's scale of y is 1.0-0.8
		ViewHelper.setScaleY(mainGroup, evalute(percent, 1.0f, 0.8f));
		// the background's animation
		getBackground()
				.setColorFilter(
						(Integer) evaluateColor(percent, Color.BLACK,
								Color.TRANSPARENT), Mode.SRC_OVER);
	}

	/**
	 * the function that evaluates the value
	 */
	public float evalute(float firction, Number start, Number end) {
		return start.floatValue() + firction
				* (end.floatValue() - start.floatValue());
	}

	/**
	 * the function that evalute the color's value
	 */
	public Object evaluateColor(float fraction, Object startValue,
			Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	/**
	 * get the left panel's state
	 * 
	 * @param percent
	 * @return
	 */
	private State updateState(float percent) {
		if (percent == 0) {
			return State.close;
		} else if (percent == 1) {
			return State.open;
		}
		return State.draging;
	}

	/**
	 * control the width is 0-60% screen width
	 * 
	 * @param left
	 *            the left width
	 * @return the adjustment's width
	 */
	protected int fixLeft(int left) {
		if (left < 0) {
			return 0;
		} else if (left > mRangeWidth) {
			return mRangeWidth;
		}
		return left;
	}

	/**
	 * the two parameter contructor that called three parameter contructor
	 * 
	 * @param context
	 *            the parameter that represent context
	 * @param attrs
	 *            the parameter that represents attributes
	 */
	public ResideMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * the function that initialize the first viewgroup and second viewgroup
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// get children's count
		int count = getChildCount();
		if (count < 2) {
			throw new IllegalStateException(" the control must have 2 children");
		} else if (getChildAt(0) instanceof ViewGroup
				&& getChildAt(1) instanceof ViewGroup) {
			// get the first viewgroup
			leftGroup = (ViewGroup) getChildAt(0);
			// get the second viewgroup
			mainGroup = (ViewGroup) getChildAt(1);
		} else {
			throw new IllegalStateException(
					"the children must be the viewgroup");
		}
	}

	/**
	 * the function that get the screen's width and the screen's height
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// get the screen's height
		this.mHeight = getMeasuredHeight();
		// get the screen's width
		this.mWidth = getMeasuredWidth();
		// calc the 60% screen's width
		this.mRangeWidth = (int) (this.mWidth * 0.6f);
	}
	
	// deliver the touch event
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//deliver the touch event to drag helper 
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	//the drag helper process the touch event 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// if the the helper process the touch event continuos
		return true;
	}
}
