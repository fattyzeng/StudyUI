package com.mystudy.ui;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mystudy.ui.ResideMenu.OnDragEventListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

@SuppressLint("ResourceAsColor")
public class DemoActivity extends Activity {

	private static final String TAG = "TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_main);
		
		final ListView mLeftList = (ListView) findViewById(R.id.lv_left);
		final ListView mMainList = (ListView) findViewById(R.id.lv_main);
		final ImageView mHeaderImage = (ImageView) findViewById(R.id.iv_header);
		MyLinearLayout mLinearLayout = (MyLinearLayout) findViewById(R.id.mll);
		
		// find residemenu
		ResideMenu mDragLayout = (ResideMenu) findViewById(R.id.dl);

		//  deliver the reside object
		mLinearLayout.setMyDragLayOut(mDragLayout);
		
		mDragLayout.setDragEventListener(new OnDragEventListener()  {
			
			@Override
			public void onOpen() {
				Utils.makeToast(DemoActivity.this, "onOpen");
				// the random 's left view 
				Random random = new Random();
				
				int nextInt = random.nextInt(50);
				mLeftList.smoothScrollToPosition(nextInt);
				
			}
			
			@Override
			public void onDragging(float percent) {
				Log.d(TAG, "onDraging: " + percent);// 0 -> 1
				// the alpha of head image
				// 1.0 -> 0.0
				ViewHelper.setAlpha(mHeaderImage, 1 - percent);
			}
			
			@Override
			public void onClose() {
				Utils.makeToast(DemoActivity.this, "onClose");
				// let head shake
//				mHeaderImage.setTranslationX(translationX)
				ObjectAnimator mAnim = ObjectAnimator.ofFloat(mHeaderImage, "translationX", 15.0f);
				mAnim.setInterpolator(new CycleInterpolator(4));
				mAnim.setDuration(500);
				mAnim.start();
			}
		});
		
		mLeftList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Datas.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView mText = ((TextView)view);
				mText.setTextColor(Color.WHITE);
				return view;
			}
		});
		
		mMainList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Datas.NAMES){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView mText = ((TextView)view);
				mText.setTextColor(Color.BLACK);
				return view;
			}
		});
		
		
		
	}
}
