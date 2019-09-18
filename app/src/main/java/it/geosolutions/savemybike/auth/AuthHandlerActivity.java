package it.geosolutions.savemybike.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class AuthHandlerActivity extends AppCompatActivity
{
	public static int AUTHORIZATION_REQUEST_CODE = 0x2112;

	protected void onAuthenticationSucceeded()
	{

	}

	protected void onAuthenticationFailed(String sError)
	{

	}

	@Override
	protected void onResume()
	{
		AuthenticationManager.instance().setCurrentActivity(this);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == AUTHORIZATION_REQUEST_CODE)
		{
			AuthenticationManager.instance().onAuthRequestCompleted(this,resultCode,data);
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
