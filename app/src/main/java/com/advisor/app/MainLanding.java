package com.advisor.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.advisor.app.db.AdvisorDB;
import com.advisor.app.disclaimer.DisclaimerActivity;
import com.advisor.app.login.RegisterMe;
import com.advisor.app.login.SigninActivity;
import com.advisor.app.phone.AsyncHelper;
import com.advisor.app.phone.Constants;
import com.advisor.app.util.UtilHelper;

public class MainLanding extends Activity
{

	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private AdvisorDB dataBase;
	private ProgressDialog progress;

	private String rates = null;
	private int minutes = 0;
	private AsyncHelper async = null;

	private SharedPreferences sharedPref;
	private Editor editor;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.gmail_style_navigation );
		dataBase = new AdvisorDB( this );
		progress = new ProgressDialog( this );
		progress.setMessage( "Loading..." );
		progress.setCancelable( false );
		
		async = new AsyncHelper( progress );

		sharedPref = getSharedPreferences( Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE );
		editor = sharedPref.edit();
		StringBuffer buff = new StringBuffer();

		buff.append( "\n\nCoaching focuses on helping clients on a deeply personal level. It is a rapport built on teamwork. A Life Coach believes in you and will inspire you to believe in yourself.\n\n" )
		.append( "Our role is to motivate and help you become confident and self assured. It is based on positive psychology, a form of humanistic psychology. ")
        .append("From the simplest task to the most complicated one, we are here to advise you at the level you need us to be.\n\n" )
		.append( "As your Personal Coach, I am fully present. Together, we will build a personal toolkit, designed to reshape your thinking process and need for adaptation and thus, changing")
        .append("your future. You will emerge as the architect and designer of your own “Life-House!”\n\n" )
        .append("Copyright 2015");


        TextView des = (TextView) findViewById(R.id.relativeLayout_description_ml);
        des.setText(buff.toString());

        TextView intro = (TextView)findViewById(R.id.relativeLayout_intro_ml);
        intro.setText("Personal and Professional Coaching");

		drawerListViewItems = getResources().getStringArray( R.array.nav_drawer_items );
		drawerListView = (ListView) findViewById( R.id.left_drawer );
		drawerListView.setAdapter( new ArrayAdapter<String>( this, R.layout.drawer_listview_item, drawerListViewItems ) );

		drawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );

		actionBarDrawerToggle = new ActionBarDrawerToggle( MainLanding.this, drawerLayout, R.drawable.ic_drawer, R.string.siane, R.string.app_name );
		drawerLayout.setDrawerListener( actionBarDrawerToggle );

		getActionBar().setDisplayHomeAsUpEnabled( true );

		drawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
		drawerListView.setOnItemClickListener( new DrawerItemClickListener() );
		
		getRate();

	}

	public void getRate()
	{
		try
		{
			if( rates == null )
			{
				String[] result = async.execute( "mainpage" ).get();
				rates = result[Constants.RATE];
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
		actionBarDrawerToggle.onConfigurationChanged( newConfig );
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		actionBarDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		if( actionBarDrawerToggle.onOptionsItemSelected( item ) )
		{
			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@SuppressWarnings( "rawtypes" )
		@Override
		public void onItemClick( AdapterView parent, View view, int position, long id )
		{
			String option = (String) ((TextView) view).getText();

			if( option.equalsIgnoreCase( "Add Minutes" ) )
			{
				Intent intent = new Intent( getBaseContext(), com.advisor.app.payment.PayPalActivity.class );
				startActivity( intent );
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
				drawerLayout.closeDrawer( drawerListView );
			}
			else if( option.equalsIgnoreCase( "Call Advisor" ) )
			{
				minutes = UtilHelper.getMinutesRemaining( dataBase.getAvailableMinutes(), rates );
				if( Integer.valueOf( minutes ) > 0 )
				{
					Intent intent = new Intent( getBaseContext(), CallActivity.class );
					startActivity( intent );
					drawerLayout.closeDrawer( drawerListView );
					overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
				}
				else
				{
					drawerLayout.closeDrawer( drawerListView );
					Toast.makeText( getApplicationContext(), "Please buy minutes!!", Toast.LENGTH_LONG ).show();
				}
			}
			else if( option.equalsIgnoreCase( "Login" ) )
			{
				Intent login = new Intent( getBaseContext(), SigninActivity.class );
				startActivity( login );
				drawerLayout.closeDrawer( drawerListView );
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			}
			else if( option.equalsIgnoreCase( "Register" ) )
			{
				Intent register = new Intent( getBaseContext(), RegisterMe.class );
				startActivity( register );
				drawerLayout.closeDrawer( drawerListView );
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			}
			else if( option.equalsIgnoreCase( "Log Out" ) )
			{

				String[] shared = UtilHelper.sharedPrefExpand( sharedPref.getString( Constants.EDITOR_EMAIL, Constants.SP_DEFAULT ) );
				shared[Constants.SP_EMAIL] = Constants.SP_BLANK;
				editor.putString( Constants.EDITOR_EMAIL, UtilHelper.sharedPrefContract( shared ) );
				editor.commit();
				drawerLayout.closeDrawer( drawerListView );
			}
			else if( option.equalsIgnoreCase( "Disclaimer" ) )
			{
				Intent disclaimer = new Intent( getBaseContext(), DisclaimerActivity.class );
				startActivity( disclaimer );
				drawerLayout.closeDrawer( drawerListView );
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			}
			else
			{
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			}
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
