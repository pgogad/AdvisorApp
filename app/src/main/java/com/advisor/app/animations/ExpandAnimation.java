package com.advisor.app.animations;

import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

public class ExpandAnimation extends Animation implements Animation.AnimationListener
{
	private View view;
	private static int ANIMATION_DURATION;
	private int LastWidth;
	@SuppressWarnings( "unused" )
	private int FromWidth;
	private int ToWidth;
	@SuppressWarnings( "unused" )
	private static int STEP_SIZE = 30;

	public ExpandAnimation(View v, int FromWidth, int ToWidth, int Duration)
	{
		this.view = v;
		ANIMATION_DURATION = 1;
		this.FromWidth = FromWidth;
		this.ToWidth = ToWidth;
		setDuration(ANIMATION_DURATION);
		setRepeatCount(20);
		setFillAfter(false);
		setInterpolator(new AccelerateInterpolator());
		setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation arg0)
	{
	}

	@Override
	public void onAnimationRepeat(Animation arg0)
	{
		LayoutParams lyp = view.getLayoutParams();
		lyp.width = LastWidth += ToWidth / 20;
		view.setLayoutParams(lyp);
	}

	@Override
	public void onAnimationStart(Animation arg0)
	{
		LayoutParams lyp = view.getLayoutParams();
		lyp.width = 0;
		view.setLayoutParams(lyp);
		LastWidth = 0;
	}

}
