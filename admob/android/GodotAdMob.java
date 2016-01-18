
package org.godotengine.godot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.android.gms.ads.*;

import android.app.Activity;
import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;
import android.provider.Settings;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.os.Bundle;

public class GodotAdMob extends Godot.SingletonBase
{
	private Activity		activity			= null;
	private AdView		adView			= null;
	private AdView		adViewW			= null;
	private AdView		adViewH			= null;
	private AdRequest		request			= null;
	private AdListener		adListener			= null;
	private boolean		isRealAd			= false;
	private boolean		isAdOnTop		= true;
	private String			adUnitID			= null;
	private boolean		isShowBanner		= false;
	private boolean		isShowBannerArg	= false;
		
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
		Log.d("godot", "AdMob: init");
		
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				//if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN)
				//{
				//	activity.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				//		android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
				//}

				AdRequest.Builder	adBuilder	= new AdRequest.Builder();
				adBuilder.tagForChildDirectedTreatment(true);
				if(!isRealAd)
				{
					adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
					adBuilder.addTestDevice(getAdmobDeviceId());
				}
				request		= adBuilder.build();
				
				adListener		= new AdListener()
				{
					@Override public void onAdLoaded()
					{
						Log.d("godot", "AdMob: OnAdLoaded");
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
				
				setBannerView();
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
				if(isShowBanner == isShowBannerArg)	return;
				
				isShowBanner	= isShowBannerArg;
				setShowBanner(isShowBanner);
			}
		});
	}
	
	public void resize(boolean isTop)
	{
		isAdOnTop	= isTop;
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				setBannerView();
			}
		});
	}
	
	private void setShowBanner(boolean isShow)
	{
		if(isShow)
		{
			//adView.loadAd(request);
			adView.setVisibility(View.VISIBLE);
			adView.resume();
			Log.d("godot", "AdMob: Show Banner");
		}
		else
		{
			adView.setVisibility(View.GONE);
			//adView.destroy();
			adView.pause();
			Log.d("godot", "AdMob: Hide Banner");
		}
	}
	
	private AdView setAdView()
	{
		int		w		= activity.getWindow().getDecorView().getWidth();
		int		h		= activity.getWindow().getDecorView().getHeight();
		boolean	isCreate	= true;
		
		if(w > h)	// Landscape
		{
			if(null != adViewW)
			{
				isCreate	= false;
				adView	= adViewW;
			}
		}
		else		// Portrait
		{
			if(null != adViewH)
			{
				isCreate	= false;
				adView	= adViewH;
			}
		}
	
		if(isCreate)
		{
			adView	= new AdView(activity);
			adView.setBackgroundColor(Color.TRANSPARENT);
			adView.setAdUnitId(adUnitID);
			adView.setAdSize(AdSize.SMART_BANNER);
			adView.setAdListener(adListener);
			adView.loadAd(request);
			setShowBanner(false);
			
			if(w > h)	adViewW		= adView;
			else		adViewH		= adView;

			//Log.d("godot", "AdMob: View is hardware = " + adView.isHardwareAccelerated());
		}
		
		return adView;
	}
	
	private void setBannerView()
	{	
		FrameLayout	layout	= ((Godot)activity).layout;
		
		FrameLayout.LayoutParams	adParams	= new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.WRAP_CONTENT);
			
		if(isAdOnTop)	adParams.gravity	= Gravity.TOP;
		else			adParams.gravity	= Gravity.BOTTOM;
		
		if(null != adView)
		{
			setShowBanner(false);
			layout.removeView(adView);
		}

		setAdView();
		layout.addView(adView, adParams);
		setShowBanner(isShowBanner);
	}
	
	/*@Override protected void onMainDestroy()
	{
		Log.d("godot", "AdMob: Destroy");
	}*/
	
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
			"init", "showBanner", "getAdWidth", "getAdHeight", "resize"
		});
		activity		= p_activity;
	}
}

