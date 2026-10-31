package com.skartivpn.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.skartivpn.app.activities.BaseActivity;
import com.skartivpn.app.util.ConfigUtil;
import com.skarti.premium.v2.R;

public class LauncherActivity extends BaseActivity {
    private static final String TAG = "LauncherActivity";
    private static final String REMOD_KEY = "ReMod";
    private static final String PREMIUM_PACKAGE = "com.skarti.premium.v2";
    private static final String PREMIUM_APP_NAME = "SKARTI PRO DESIGN V2";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if this is the original app or modified version
        boolean isOriginalApp = checkIfOriginalApp();
        String reModStatus = isOriginalApp ? "Original" : "Modified";
        
        // Setup UI
        setupSplashScreen();
        
        // Start main activity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(LauncherActivity.this, PhreakersMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra(REMOD_KEY, reModStatus);
            startActivity(intent);
            finish();
        }, ConfigUtil.dur);
    }
    
    private boolean checkIfOriginalApp() {
        String currentAppName = getPackageManager().getApplicationLabel(getApplicationInfo()).toString();
        String currentPackage = getPackageName();
        
        return currentAppName.equalsIgnoreCase(PREMIUM_APP_NAME) && 
               currentPackage.equalsIgnoreCase(PREMIUM_PACKAGE);
    }
    
    private void setupSplashScreen() {
        // Create main layout
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        
        // Add app icon
        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        icon.setImageResource(R.drawable.icon);
        
        // Add app name text
        TextView appNameText = new TextView(this);
        appNameText.setText(R.string.app_name);
        appNameText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        appNameText.setTextColor(Color.WHITE);
        appNameText.setGravity(Gravity.CENTER);
        appNameText.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT));
        appNameText.setPadding(0, 10, 0, 0);
        
        // Add views to layout
        layout.addView(icon);
        layout.addView(appNameText);
        
        setContentView(layout);
    }
}