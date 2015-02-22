package com.advisor.app.disclaimer;

import com.advisor.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.widget.TextView;

public class DisclaimerActivity extends Activity
{

	private TextView text;

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.disclaimer );

		text = (TextView) findViewById( R.id.disclaimer_text_box );
		text.setMovementMethod( new ScrollingMovementMethod() );
		text.setPadding( 10, 10, 10, 10 );
		text.setGravity( Gravity.CENTER );

		String disclaim = "ALL PSYCHIC READINGS AND CONSULTATIONS ARE PROVIDED FOR ENTERTAINMENT PURPOSES ONLY "
				+ "and do not in anyway constitute medical, financial, legal, personal or any sort of professional counseling. "
				+ "By engaging in a psychic reading or using this site, you understand that we do not provide recommendations, "
				+ "or give any directions for you to follow. A psychic reader, for example, may from time to time offer a personal "
				+ "opinion. A reader will not diagnose illnesses or conditions.We do not refer, endorse, recommend, verify, evaluate "
				+ "or guarantee any services provided by readers, and nothing shall be considered as a referral, endorsement, recommendation "
				+ "or guarantee of any reader. We do not   warrant the validity, accuracy, completeness, safety, "
				+ "legality, quality, or applicability of the content or anything said   or written by any reader or any advice provided. We will not "
				+ "be liable for any damages sustained due to reliance by   you on such information provided by any reader.  "
				+ "Reader our not employees and the provision of any of the service by a reader shall be directly between you and   the reader. "
				+ "You and the reader are solely responsible for the conversations which occur while a reader is providing   any services.";

		text.setText( disclaim );

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

	}

	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
		finish();
	}
}
