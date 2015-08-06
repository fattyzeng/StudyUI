package com.mystudy.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

public class DragLayOut extends FrameLayout {

	private ViewDragHelper mDragHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private OnDragStatusChangeListener mListener;
	private Status mStatus = Status.Close;
	
	/**
	 * ״̬ö��
	 */
	public static enum Status {
		Close, Open, Draging;
	}
	public interface OnDragStatusChangeListener{
		void onClose();
		void onOpen();
		void onDraging(float percent);
	}
	
	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public void setDragStatusListener(OnDragStatusChangeListener mListener){
		this.mListener = mListener;
	}
	
	public DragLayOut(Context context) {
		this(context, null);
	}

	public DragLayOut(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

    
	
	public DragLayOut(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// a.��ʼ�� (ͨ����̬����) 
		mDragHelper = ViewDragHelper.create(this , mCallback);
		
	}
	
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
		// c. ��д�¼�
		
		// 1. ���ݷ��ؽ��������ǰchild�Ƿ������ק
		// child ��ǰ����ק��View
		// pointerId ���ֶ�㴥����id
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		};
		
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			// ��capturedChild������ʱ,����.
			super.onViewCaptured(capturedChild, activePointerId);
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			// ������ק�ķ�Χ, ������ק��������������. ���������˶���ִ���ٶ�
			return mRange;
		}
		
		// 2. ���ݽ���ֵ ������Ҫ�ƶ�����(����)λ��   (��Ҫ)
		// ��ʱû�з����������ƶ�
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// child: ��ǰ��ק��View
			// left �µ�λ�õĽ���ֵ, dx λ�ñ仯��
			// left = oldLeft + dx;
				if(child == mMainContent){
				left = fixLeft(left);
			}
			return left;
		}

		// 3. ��Viewλ�øı��ʱ��, ����Ҫ�������� (����״̬, ���涯��, �ػ����)
		// ��ʱ,View�Ѿ�������λ�õĸı�
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// changedView �ı�λ�õ�View
			// left �µ����ֵ
			// dx ˮƽ����仯��
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			
			int newLeft = left;
			if(changedView == mLeftContent){
				// �ѵ�ǰ�仯�����ݸ�mMainContent
				newLeft = mMainContent.getLeft() + dx;
			}
			
			// ��������
			newLeft = fixLeft(newLeft);
			
			if(changedView == mLeftContent) {
				// ��������ƶ�֮��, ��ǿ�ƷŻ�ȥ.
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}
			// ����״̬,ִ�ж���
			dispatchDragEvent(newLeft);
			
			// Ϊ�˼��ݵͰ汾, ÿ���޸�ֵ֮��, �����ػ�
			invalidate();
		}

		// 4. ��View���ͷŵ�ʱ��, ���������(ִ�ж���)
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// View releasedChild ���ͷŵ���View 
			// float xvel ˮƽ������ٶ�, ����Ϊ+
			// float yvel ��ֱ������ٶ�, ����Ϊ+
			super.onViewReleased(releasedChild, xvel, yvel);
			
			// �ж�ִ�� �ر�/����
			// �ȿ������п��������,ʣ�µľͶ��ǹرյ����
			if(xvel == 0 && mMainContent.getLeft() > mRange / 2.0f){
				open();
			}else if (xvel > 0) {
				open();
			}else {
				close();
			}
			
		}

		@Override
		public void onViewDragStateChanged(int state) {
			// TODO Auto-generated method stub
			super.onViewDragStateChanged(state);
		}

	};
	
	/**
	 * ���ݷ�Χ�������ֵ
	 * @param left
	 * @return
	 */
	private int fixLeft(int left) {
		if(left < 0){
			return 0;
		}else if (left > mRange) {
			return mRange;
		}
		return left;
	}
	
	protected void dispatchDragEvent(int newLeft) {
		float percent = newLeft * 1.0f/ mRange;
		//0.0f -> 1.0f
		
		if(mListener != null){
			mListener.onDraging(percent);
		}
		
		// ����״̬, ִ�лص�
		Status preStatus = mStatus;
		mStatus = updateStatus(percent);
		if(mStatus != preStatus){
			// ״̬�����仯
			if(mStatus == Status.Close){
				// ��ǰ��Ϊ�ر�״̬
				if(mListener != null){
					mListener.onClose();
				}
			}else if (mStatus == Status.Open) {
				if(mListener != null){
					mListener.onOpen();
				}
			}
		}
		
//		* ���涯��:
		animViews(percent);
		
	}

	private Status updateStatus(float percent) {
		if(percent == 0f){
			return Status.Close;
		}else if (percent == 1.0f) {
			return Status.Open;
		}
		return Status.Draging;
	}

	private void animViews(float percent) {
		//		> 1. �����: ���Ŷ���, ƽ�ƶ���, ͸���ȶ���
					// ���Ŷ��� 0.0 -> 1.0 >>> 0.5f -> 1.0f  >>> 0.5f * percent + 0.5f
			//		mLeftContent.setScaleX(0.5f + 0.5f * percent);
			//		mLeftContent.setScaleY(0.5f + 0.5f * percent);
					ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
					ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);
					// ƽ�ƶ���: -mWidth / 2.0f -> 0.0f
					ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth / 2.0f, 0));
					// ͸����: 0.5 -> 1.0f
					ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));
				
		//		> 2. �����: ���Ŷ���
					// 1.0f -> 0.8f
					ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
					ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
					
		//		> 3. ��������: ���ȱ仯 (��ɫ�仯)
					getBackground().setColorFilter((Integer)evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}
	
    /**
     * ��ֵ��
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    /**
     * ��ɫ�仯����
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
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

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }

	@Override
	public void computeScroll() {
		super.computeScroll();
		
		// 2. ����ƽ������ (��Ƶ�ʵ���)
		if(mDragHelper.continueSettling(true)){
			//  �������true, ��������Ҫ����ִ��
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	public void close(){
		close(true);
	}
	/**
	 * �ر�
	 */
	public void close(boolean isSmooth) {
		initialTranslationAnimation(isSmooth);
	}

	private void initialTranslationAnimation(boolean isSmooth) {
		int finalLeft = 0;
		if(isSmooth){
			// 1. ����һ��ƽ������
			if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				// ����true����û���ƶ���ָ��λ��, ��Ҫˢ�½���.
				// ������this(child���ڵ�ViewGroup)
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}
	
	public void open(){
		open(true);
	}
	/**
	 * ����
	 */
	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if(isSmooth){
			// 1. ����һ��ƽ������
			if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				// ����true����û���ƶ���ָ��λ��, ��Ҫˢ�½���.
				// ������this(child���ڵ�ViewGroup)
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	private int mHeight;
	private int mWidth;
	private int mRange;
	
	// b.���ݴ����¼�
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// ���ݸ�mDragHelper
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ����true, ���������¼�
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// Github
		// дע��
		// �ݴ��Լ�� (����������View, ��View������ViewGroup������)
		
		if(getChildCount() < 2){
			throw new IllegalStateException("����������������. Your ViewGroup must have 2 children at least.");
		}
		if(!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("��View������ViewGroup������. Your children must be an instance of ViewGroup");
		}
		
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// ���ߴ��б仯��ʱ�����
		
		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();
		
		// �ƶ��ķ�Χ
		mRange = (int) (mWidth * 0.6f);
		
	}
}
