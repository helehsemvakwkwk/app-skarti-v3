package com.skartivpn.app.activities;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.skartivpn.app.preference.SettingsPreference;
import com.skartivpn.inject.config.Settings;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;
import com.skartivpn.app.preference.LocaleHelper;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import static android.content.pm.PackageManager.GET_META_DATA;
import com.skartivpn.app.ExceptionHandler;
/**
 * Created by Pankaj on 03-11-2017.
 */
public abstract class BaseActivity extends AppCompatActivity
{
	public static int mTheme = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setTheme(ThemeUtil.getThemeId(mTheme));
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); 
		resetTitles();
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.setLocale(base));
	}
	protected void resetTitles() {
		try {
			ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
			if (info.labelRes != 0) {
				setTitle(info.labelRes);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
