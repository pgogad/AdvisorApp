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

//		String disclaim = "ALL PSYCHIC READINGS AND CONSULTATIONS ARE PROVIDED FOR ENTERTAINMENT PURPOSES ONLY "
//				+ "and do not in anyway constitute medical, financial, legal, personal or any sort of professional counseling. "
//				+ "By engaging in a psychic reading or using this site, you understand that we do not provide recommendations, "
//				+ "or give any directions for you to follow. A psychic reader, for example, may from time to time offer a personal "
//				+ "opinion. A reader will not diagnose illnesses or conditions.We do not refer, endorse, recommend, verify, evaluate "
//				+ "or guarantee any services provided by readers, and nothing shall be considered as a referral, endorsement, recommendation "
//				+ "or guarantee of any reader. We do not   warrant the validity, accuracy, completeness, safety, "
//				+ "legality, quality, or applicability of the content or anything said   or written by any reader or any advice provided. We will not "
//				+ "be liable for any damages sustained due to reliance by   you on such information provided by any reader.  "
//				+ "Reader our not employees and the provision of any of the service by a reader shall be directly between you and   the reader. "
//				+ "You and the reader are solely responsible for the conversations which occur while a reader is providing   any services.";

        String disclaim = "BY USING THIS APP OR RECEIVING COACHING SERVICES, YOU AGREE TO AND ACCEPT THE DISCLAIMER BELOW.\n\n"
                +"Responsibility\n\n"
                + "Coaching involves a relationship between the client and the coach as an aid for self improvement. Coaching is " +
                "intended to provide support and encouragement for the client’s goals and decisions based on information provided by " +
                "the client. Client is fully and solely responsible for all information provided to the coach. Client will indemnify " +
                "and hold harmless Carraro Inc and its principals, directors, officers, employees, affiliates, representatives, successors " +
                "and assigns from any claims, liability, costs or expenses in connection with any coaching services, information or materials.\n\n"
                +"Carraro Inc does not guarantee or warrant any particular outcome, result or success of coaching, and expressly disclaims any such guarantee or warranty.\n\n"
                +"Carraro Inc takes reasonable steps to keep information from coaching sessions confidential. Communications between Carraro Inc and client containing " +
                "information related to coaching sessions might be unlawfully intercepted by third parties. Carraro Inc nor any of our third party " +
                "service providers can ensure or warrant the security of any information transmitted over the internet or via The App. Any such transmission is done at own risk.\n\n"
                +"Coaching and The App are not a substitute for medical, religious, financial,psychological, psychiatric, therapeutic or other diagnosis for any medicine. " +
                "You should independently assess any decisions, actions or inactions resulting from or relating to the app or a coaching experience based on medical, " +
                "psychological, psychiatric, legal, religious, personnel, personal, financial, tax or other advice as applicable.\n\n";



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
