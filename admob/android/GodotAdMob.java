
package com.android.godot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.android.gms.ads.*;

import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.provider.Settings;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.FrameLayout;
//import android.os.Bundle;

public class GodotAdMob extends Godot.SingletonBase
{
	private AdView		adView;
	private Activity		activity;
	private LinearLayout	layout			= null;
	private boolean		isRealAd			= false;
	private boolean		isAdOnTop		= true;
	private String			adUnitID			= null;
	private boolean		isShowBanner		= false;
	private boolean		isShowBannerArg	= false;
	
	/*public int getInt()
	{
		return 13579;
	}*/
	
	public int getAdWidth()
	{
		return AdSize.SMART_BANNER.getWidthInPixels(activity);
	}
	
	public int getAdHeight()
	{
		return AdSize.SMART_BANNER.getHeightInPixels(activity);
	}

	static public Godot.SingletonBase initialize(Activity p_activity)
	{
		return new GodotAdMob(p_activity);
	}
	
	public void init(boolean isReal, boolean isTop, String id)
	{
		isRealAd		= isReal;
		isAdOnTop	= isTop;
		adUnitID		= id;
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				AdRequest	request;

				adView	= new AdView(activity);
				adView.setBackgroundColor(Color.TRANSPARENT);
				adView.setAdUnitId(adUnitID);
				adView.setAdSize(AdSize.SMART_BANNER);
				
				if(isRealAd)
				{
					request	= new AdRequest.Builder()
						//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
						.build();
				}
				else
				{
					request	= new AdRequest.Builder()
						.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
						.addTestDevice(getAdmobDeviceId())
						.build();
				}

				AdListener		adListener	= new AdListener()
				{
					@Override public void onAdLoaded()
					{
						Log.d("godot", "AdMob: OnAdLoaded");
						if(layout == null)
						{
							activity.runOnUiThread(new Runnable()
							{
								@Override public void run()
								{
									FrameLayout.LayoutParams	adParams =
										new FrameLayout.LayoutParams(
											FrameLayout.LayoutParams.MATCH_PARENT,
											FrameLayout.LayoutParams.WRAP_CONTENT);
									if(isAdOnTop)	adParams.gravity	= Gravity.TOP;
									else			adParams.gravity	= Gravity.BOTTOM;
									layout	= new LinearLayout(activity);
									activity.addContentView(layout, adParams	);
								}
							});
						}
					}
					
					@Override public void onAdFailedToLoad(int errorCode)
					{
						String	str;
						
						switch(errorCode)
						{
						case AdRequest.ERROR_CODE_INTERNAL_ERROR:
							str	= "ERROR_CODE_INTERNAL_ERROR";
							break;
						case AdRequest.ERROR_CODE_INVALID_REQUEST:
							str	= "ERROR_CODE_INVALID_REQUEST";
							break;
						case AdRequest.ERROR_CODE_NETWORK_ERROR:
							str	= "ERROR_CODE_NETWORK_ERROR";
							break;
						case AdRequest.ERROR_CODE_NO_FILL:
							str	= "ERROR_CODE_NO_FILL";
							break;
						default:
							str	= "Code: " + errorCode;
							break;
						}
						Log.w("godot", "AdMob: onAdFailedToLoad->" + str);
					}
				};
				adView.setAdListener(adListener);
				adView.loadAd(request);
			}
		});
	}
	
	public void showBanner(boolean isShow)
	{
		if(isShowBanner == isShow)	return;
		isShowBannerArg	= isShow;
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if(null == layout)					return;
				if(isShowBanner == isShowBannerArg)	return;
				
				isShowBanner	= isShowBannerArg;
				if(isShowBanner)
				{
					layout.addView(adView);
					Log.d("godot", "AdMob: Show Banner");
				}
				else
				{
					layout.removeAllViews();
					Log.d("godot", "AdMob: Hide Banner");
				}
			}
		});
	}
	
	private String md5(final String s)
	{
		try
		{
			// Create MD5 Hash
			MessageDigest	digest	= java.security.MessageDigest.getInstance("MD5");
			
			digest.update(s.getBytes());
			byte		messageDigest[]	= digest.digest();

			// Create Hex String
			StringBuffer	hexString		= new StringBuffer();
			for(int i=0; i<messageDigest.length; i++)
			{
				String	h	= Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
				{
					h = "0" + h;
				}
				hexString.append(h);
			}
			return hexString.toString();

		}
		catch(NoSuchAlgorithmException e)
		{
			//Logger.logStackTrace(TAG,e);
		}
		return "";
	}
	
	private String getAdmobDeviceId()
	{
		String android_id	= Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId	= md5(android_id).toUpperCase();

		return deviceId;
	}

	public GodotAdMob(Activity p_activity)
	{
		registerClass("AdMob", new String[]
		{
			"init", "showBanner", "getAdWidth", "getAdHeight"//, "getInt"
		});
		activity		= p_activity;
	}
}

