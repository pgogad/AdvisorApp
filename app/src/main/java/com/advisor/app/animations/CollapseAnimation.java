package com.advisor.app.animations;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

public class CollapseAnimation extends Animation implements Animation.AnimationListener
{

	private View view;
	private static int ANIMATION_DURATION;
	@SuppressWarnings( "unused" )
	private int LastWidth;
	@SuppressWarnings( "unused" )
	private int FromWidth;
	private int ToWidth;
	@SuppressWarnings( "unused" )
	private static int STEP_SIZE = 30;

	public CollapseAnimation(View v ,int FromWidth, int ToWidth, int Duration)
	{
		this.view = v;
		LayoutParams lyp = view.getLayoutParams();
		ANIMATION_DURATION = 1;
		this.FromWidth = lyp.width;
		this.ToWidth = lyp.width;
		setDuration(ANIMATION_DURATION);
		setRepeatCount(20);
		setFillAfter(false);
		setInterpolator(new AccelerateInterpolator());
		setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation animation)
	{
	}

	@Override
	public void onAnimationRepeat(Animation animation)
	{
		LayoutParams lyp = view.getLayoutParams();
		lyp.width = lyp.width - ToWidth / 20;
		view.setLayoutParams(lyp);
	}

	@Override
	public void onAnimationStart(Animation animation)
	{
		LayoutParams lyp =  view.getLayoutParams();
		LastWidth = lyp.width;
	}

}
