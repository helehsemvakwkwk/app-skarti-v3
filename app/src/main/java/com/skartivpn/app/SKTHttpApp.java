package com.skartivpn.app;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.skartivpn.app.preference.SettingsPreference;
import com.skartivpn.inject.util.SkProtect;
import com.skartivpn.inject.logger.SkStatus;
import android.content.Context;
import android.content.pm.PackageInfo;
import com.skartivpn.app.util.Utils;
import android.os.Build;
import android.content.pm.PackageManager;
import com.skartivpn.inject.SocksHttpCore;
import com.skartivpn.inject.config.Settings;
import android.util.Log;
import android.content.res.Configuration;
import com.skartivpn.app.preference.LocaleHelper;
import android.graphics.Matrix;
import android.app.Application.ActivityLifecycleCallbacks;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import android.app.Activity;
import android.os.Bundle;
import java.util.Date;
import com.skarti.premium.v2.R;

/**
* App
*/
public class SKTHttpApp extends Application implements ActivityLifecycleCallbacks, LifecycleObserver
{
	
	private static SKTHttpApp mApp;
	private Activity currentActivity;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		String night = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).getString("modeNight", "off");
		AppCompatDelegate.setDefaultNightMode("on".equals(night) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
		mApp = this;
		// captura dados para análise
		/*new FlurryAgent.Builder()
			.withCaptureUncaughtExceptions(true)
            .withIncludeBackgroundSessionsInMetrics(true)
            .withLogLevel(Log.VERBOSE)
            .withPerformanceMetrics(FlurryPerformance.ALL)
			.build(this, APP_FLURRY_KEY);*/
			
		// inicia
		SocksHttpCore.init(this);
		
		// protege o app
		SkProtect.init(this);
		
		// Initialize the Mobile Ads SDK.
        
        
        //tangina mo
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(mApp)); 
		
		this.registerActivityLifecycleCallbacks(this);
		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
		
		//	appOpenAdManager.showAdIfAvailable(currentActivity);
	}
	@OnLifecycleEvent(Event.ON_START)
	protected void onMoveToForeground() {
		// Show the ad (if available) when the app moves to foreground.
		
	}

	/** ActivityLifecycleCallback methods. */
	@Override
	public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

	@Override
	public void onActivityStarted(@NonNull Activity activity) {
		// An ad activity is started when an ad is showing, which could be AdActivity class from
		// Google
		// SDK or another activity class implemented by a third party mediation partner. Updating
		// the
		// currentActivity only when an ad is not showing will ensure it is not an ad activity, but
		// the
		// one that shows the ad.
		
	}

	@Override
	public void onActivityResumed(@NonNull Activity activity) {}

	@Override
	public void onActivityPaused(@NonNull Activity activity) {}

	@Override
	public void onActivityStopped(@NonNull Activity activity) {}

	@Override
	public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

	@Override
	public void onActivityDestroyed(@NonNull Activity activity) {}

	
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		//LocaleHelper.setLocale(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//LocaleHelper.setLocale(this);
	}
	public static SKTHttpApp getApp() {
		return mApp;
	}
}
