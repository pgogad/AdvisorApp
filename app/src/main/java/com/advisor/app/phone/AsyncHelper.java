package com.advisor.app.phone;

import java.math.BigDecimal;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.advisor.app.util.UtilHelper;

public class AsyncHelper extends AsyncTask<String, Long, String[]>
{

	private ProgressDialog progress;

	public AsyncHelper( ProgressDialog progress )
	{
		this.progress = progress;
	}

	protected void onPostExecute( String[] result )
	{
		super.onPostExecute( result );
		if( null != progress )
		{
			if( progress.isShowing() )
			{
				progress.dismiss();
			}
		}
	}

	protected void onPreExecute()
	{
		if( null != progress && !progress.isShowing() )
		{
			progress.show();
		}
	}

	@Override
	protected String[] doInBackground( String... params )
	{
		String[] results = new String[5];

		String args = params[0];
		if( args.equalsIgnoreCase( "mainpage" ) )
		{
			results[Constants.RATE] = HttpHelper.getRates();
		}
		else if( args.equalsIgnoreCase( "paypalapproval" ) )
		{
			results[Constants.PAY_PAL] = HttpHelper.postPayPalApproval( params[1] );

		}
		else if( args.equalsIgnoreCase( "login" ) )
		{
			results[Constants.LOGIN] = HttpHelper.validateLogin( params[1], params[2] );
		}
		else
		{
			String amount = params[1];
			results[Constants.RATE] = HttpHelper.getRates();

			int minutes = UtilHelper.getMinutesRemaining( new BigDecimal( amount ), results[Constants.RATE] );

			results[Constants.CAPABILITY_TOKEN] = HttpHelper.requestWebService( minutes );
			results[Constants.PHONE_NUMBER] = HttpHelper.getPhoneNumber();
		}
		return results;
	}
}
