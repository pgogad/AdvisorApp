package com.advisor.app.login;

import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.advisor.app.R;
import com.advisor.app.util.UtilHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ForgotPassword extends Activity
{

	private EditText emailET;
	private EditText passwordET;
	private EditText passwrd1ET;

	private ProgressDialog prgDialog;
	private AsyncHttpClient client = new AsyncHttpClient();

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.forgot_password );

		emailET = (EditText) findViewById( R.id.forgotpassword_email );
		passwordET = (EditText) findViewById( R.id.forgotpassword_password );
		passwrd1ET = (EditText) findViewById( R.id.forgotpassword_password_again );

		prgDialog = new ProgressDialog( this );

	}

	public void changePassword( View view )
	{
		String email = emailET.getText().toString().trim();
		String password = passwordET.getText().toString().trim();
		String again = passwrd1ET.getText().toString().trim();

		if( UtilHelper.isNotNull( email ) && UtilHelper.isNotNull( password ) && UtilHelper.isNotNull( again ) )
		{
			if( password.equals( again ) )
			{
				if( UtilHelper.validate( email ) )
				{
					try
					{
						prgDialog.setMessage( "Please wait..." );
						prgDialog.setCancelable( false );
						prgDialog.show();
						client.addHeader( "content-type", "application/json" );
						String url = "http://dry-dusk-8611.herokuapp.com/resetpassword/" + URLEncoder.encode( email, "UTF-8" ) + "/"
								+ URLEncoder.encode( password, "UTF-8" );
						client.get( this.getApplicationContext(), url, new AsyncHttpResponseHandler()
						{
							@Override
							public void onSuccess( String response )
							{
								Log.d( "HTTP", "onSuccess: " + response );
								prgDialog.dismiss();

								if( response.equalsIgnoreCase( "Email does not exist" ) )
								{
									Toast.makeText( getApplicationContext(), "The email address you entered does not exist", Toast.LENGTH_LONG ).show();
								}
								else if( response.equalsIgnoreCase( "Success" ) )
								{
									Toast.makeText( getApplicationContext(), "Password has changed!!", Toast.LENGTH_LONG ).show();
								}
								else
								{
									Toast.makeText( getApplicationContext(), "There was problem chaging your password", Toast.LENGTH_LONG ).show();
								}
							}

							@Override
							public void onFailure( int statusCode, Throwable error, String content )
							{
								prgDialog.dismiss();
								if( statusCode == 404 )
								{
									Toast.makeText( getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG ).show();
								}
								else if( statusCode == 500 )
								{
									Toast.makeText( getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG ).show();
								}
								else
								{
									Toast.makeText( getApplicationContext(), "Unexpected Error occcured! Please try again later", Toast.LENGTH_LONG ).show();
								}
							}
						} );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
				}
				else
				{
					Toast.makeText( getApplicationContext(), "Please enter valid email address", Toast.LENGTH_LONG ).show();
				}
			}
			else
			{
				Toast.makeText( getApplicationContext(), "Passwords you have entered do not match", Toast.LENGTH_LONG ).show();
			}
		}
		else
		{
			Toast.makeText( getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG ).show();
		}

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
