package com.skartivpn.app;

// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.skartivpn.app.ui.ProxySettings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.skartivpn.app.activities.BaseActivity;
import com.skartivpn.app.activities.ConfigGeralActivity;
import com.skartivpn.app.adapter.LogsAdapter;
import com.skartivpn.app.custom.CustomProfile;
import com.skartivpn.app.custom.CustomProfileStore;
import com.skartivpn.app.util.AESCrypt;
import com.skartivpn.app.util.ConfigUpdate;
import com.skartivpn.app.util.ConfigUtil;
import com.skartivpn.app.util.GoogleFeedbackUtils;
import com.skartivpn.app.util.NethPogi;
import com.skartivpn.app.util.Utils;
import com.skartivpn.app.util.VPNUtils;
import com.skartivpn.app.util.IpAddressHelper;
import com.skartivpn.inject.LaunchVpn;
import com.skartivpn.inject.SocksHttpService;
import com.skartivpn.inject.config.ConfigParser;
import com.skartivpn.inject.config.Settings;
import com.skartivpn.inject.logger.ConnectionStatus;
import com.skartivpn.inject.logger.SkStatus;
import com.skartivpn.inject.tunnel.TunnelManagerHelper;
import com.skartivpn.inject.tunnel.TunnelUtils;

// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import com.skarti.premium.v2.R;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import com.google.android.material.internal.NavigationMenu;
import android.view.MenuItem;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ImageView;
import android.text.TextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.view.animation.RotateAnimation;


    public class PhreakersMainActivity extends BaseActivity implements SkStatus.StateListener {
    
    private MaterialTextView SaputraTechOperator;
    private MaterialTextView greetingText;
    private SharedPreferences prefs;

    private MaterialTextView bytesIn;
    private MaterialTextView bytesOut;
    private MaterialTextView tvIpAddress;
    private ImageView btnRefreshIp;
    private IpAddressHelper ipAddressHelper;
    

	private Window window;
	
	


    private static final String UPDATE_VIEWS = "MainUpdate";
	public static final String OPEN_LOGS = "com.skartivpn.app:openLogs";
	private Settings mConfig;
	private Toolbar toolbar_main;
	private Handler mHandler;
	private MaterialTextView status;
	private FloatingActionButton deleteLogs;
	private RecyclerView logList;
    private LogsAdapter mLogAdapter;
    private BottomNavigationView bottomNav;
    private java.util.Timer statsTimer;
    private String lastUpText = null;
    private String lastDownText = null;
    private ConfigUtil config;
    private Button buttonnethie;
    private ViewPager vp;
    private TabLayout tabs;
	
    private TextInputEditText inputPwUser;
	private TextInputEditText inputPwPass;
    
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long mTimeLeftBtn;
    private long saved_ads_time;
    private boolean mConnected;
    private CountDownTimer mBtnCountDown;
	private boolean mTimerEnabled;
    private MaterialTextView mMaterialTextViewCountDown;

    // [FIXED] Moved inner class outside of variables section
    public class NethPH extends PagerAdapter {
        private List<String> titles;

        public NethPH(List<String> str) {
            titles = str;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View p1, Object p2) {
            return p1 == p2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int[] ids = new int[]{R.id.tab1, R.id.tab2};
            int id = ids[position];
            return findViewById(id);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    // [FIXED] Runnable untuk update greeting dipindahkan ke method yang benar
    private Runnable greetingRunnable = new Runnable() {
        @Override
        public void run() {
            if (greetingText != null) {
                updateGreeting(greetingText);
                mHandler.postDelayed(this, 60000);
            }
        }
    };

	@Override
    protected void onCreate(@Nullable Bundle $)
    {
        super.onCreate($);
		mHandler = new Handler();
		mConfig = new Settings(this);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));    
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
	    prefs = mConfig.getPrefsPrivate();
	    
	
        
		boolean showFirstTime = prefs.getBoolean("connect_first_time", true);
		int lastVersion = prefs.getInt("last_version", 0);
		
		if (showFirstTime)
        {
            SharedPreferences.Editor pEdit = prefs.edit();
            pEdit.putBoolean("connect_first_time", false);
            pEdit.apply();
			Settings.setDefaultConfig(this);
        }

		try {
			int idAtual = ConfigParser.getBuildId(this);
			if (lastVersion < idAtual) {
				SharedPreferences.Editor pEdit = prefs.edit();
				pEdit.putInt("last_version", idAtual);
				pEdit.apply();
				if (!showFirstTime) {
					if (lastVersion <= 12) {
						Settings.setDefaultConfig(this);
						Settings.clearSettings(this);
					}
				}
			}
		} catch(IOException e) {}
        String reModStatus = getIntent().getStringExtra("ReMod");

        // Periksa apakah reModStatus null sebelum memanggil .equals()
        // Anda juga bisa menambahkan logging di sini untuk membantu debugging jika suatu saat extra ini tidak ada.
        // External config files (ACTION_VIEW from WhatsApp/Telegram) do not
        // carry the splash ReMod extra, so they must not trigger the exit check.
        String launchAction = getIntent() != null ? getIntent().getAction() : null;
        if (!Intent.ACTION_VIEW.equals(launchAction) && (reModStatus == null ||
            reModStatus.equals(String.valueOf(View.GONE)) ||
            reModStatus.equals(""))) {
            android.util.Log.w("PhreakersMainActivity", "Extra 'ReMod' is missing or invalid. Exiting activity.");
			Utils.exitAll(PhreakersMainActivity.this);
		}
		doLayout();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_VIEWS);
		filter.addAction(OPEN_LOGS);
		LocalBroadcastManager.getInstance(this)
		.registerReceiver(mActivityReceiver, filter);
		doUpdateLayout();
		handleExternalConfigIntent(getIntent());
 //       doTabs();
	}
	
	 // [NEW] Method untuk update greeting
    private void updateGreeting(MaterialTextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 10) {
            greeting = "𝗦𝗲𝗹𝗮𝗺𝗮𝘁 𝗣𝗮𝗴𝗶 🌤️";
        } else if (hour >= 10 && hour < 15) {
            greeting = "𝗦𝗲𝗹𝗮𝗺𝗮𝘁 𝗦𝗶𝗮𝗻𝗴 🌞";
        } else if (hour >= 15 && hour < 19) {
            greeting = "𝗦𝗲𝗹𝗮𝗺𝗮𝘁 𝗦𝗼𝗿𝗲 ☀️";
        } else {
            greeting = "𝗦𝗲𝗹𝗮𝗺𝗮𝘁 𝗠𝗮𝗹𝗮𝗺 🌙";
        }

        textView.setText(greeting + "");
    }

    /**
     * Fetches and displays the user's public IP address
     * @param showAnimation whether to show refresh animation
     */
    private void refreshIpAddress(final boolean showAnimation) {
        if (tvIpAddress == null || ipAddressHelper == null) {
            return;
        }

        // Show loading state
        tvIpAddress.setText("Loading...");

        // Animate refresh button
        if (showAnimation && btnRefreshIp != null) {
            RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            );
            rotate.setDuration(600);
            rotate.setRepeatCount(0);
            btnRefreshIp.startAnimation(rotate);
        }

        // Fetch IP address
        ipAddressHelper.fetchIpAddress(new IpAddressHelper.IpAddressCallback() {
            @Override
            public void onIpAddressFetched(String ipAddress) {
                if (tvIpAddress != null) {
                    tvIpAddress.setText(ipAddress);
                }
            }

            @Override
            public void onError(String error) {
                if (tvIpAddress != null) {
                    tvIpAddress.setText("N/A");
                }
                if (showAnimation) {
                    VPNUtils.NethToast(PhreakersMainActivity.this, R.drawable.skarti_wrong, error);
                }
            }
        });
    }
    
	private void doLayout() {
		setContentView(R.layout.activity_main_drawer);
		toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar_main);
		// Operator View
        TelephonyManager telephonyManager = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
        String simOperatorName = telephonyManager.getSimOperatorName();
        String RegionSim = telephonyManager.getSimCountryIso();
        SaputraTechOperator = (MaterialTextView) findViewById(R.id.SaputraTechOperator);
		SaputraTechOperator.setText(simOperatorName + " ( " + RegionSim.toUpperCase() + " ) ");
        

        config = new ConfigUtil(this);
	    window = PhreakersMainActivity.this.getWindow();
        MaterialTextView configv = (MaterialTextView) findViewById(R.id.configv);
		CustomProfileStore.migrateIfNeeded(this, mConfig.getPrefsPrivate());
		CustomProfile activeProfileForStats = CustomProfileStore.getActive(this);
		configv.setText(activeProfileForStats == null ? "-" : activeProfileForStats.modeLabel());
		MaterialTextView updatet = (MaterialTextView) findViewById(R.id.update);
		updatet.setText(!mConfig.getPrefsPrivate().getString("UpdateTime", "").equals("") ? mConfig.getPrefsPrivate().getString("UpdateTime","") : "----/--/--");
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
        getSupportActionBar().setHomeButtonEnabled(true);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon);
        
		FabSpeedDial fabSpeedDial = (FabSpeedDial)findViewById(R.id.fabSpeedDial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {

				public boolean onPrepareMenu(NavigationMenu navigationMenu) {
					// TODO: Do something with yout menu items, or return false if you don't want to show them
					return true;
				}

                @Override
                public boolean onMenuItemSelected(MenuItem menuItem)
                {

                    switch (menuItem.getItemId()) {
                    
case R.id.profile:
							Intent fb = new Intent(PhreakersMainActivity.this, Profile.class);
							startActivity(fb);
							break; 

						case R.id.updConf:
                        openCustomList();
                        break;

						case R.id.miSettings:
							Intent intentSettings = new Intent(PhreakersMainActivity.this, ConfigGeralActivity.class);
							startActivity(intentSettings);
							break;

						           case R.id.offlz:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 550);
               break;

					           					           case R.id.payloadSel:
							openCustomEditor();
							break;
					           case R.id.logMenu:
							if (vp != null) vp.setCurrentItem(1);
							break;
case R.id.speedtest:
							startActivity(new Intent(PhreakersMainActivity.this, SktSpeedTest.class));
							break;
					           case R.id.pogse1:
							startActivity(new Intent(PhreakersMainActivity.this, ProxySettings.class));
							break;
					           case R.id.clearz:
							clerd();
							break;
					           case R.id.Bell:
							showReleaseNotes();
							break;

					}
                    return true;
                }


                @Override
                public void onMenuClosed() {

                }
			});
        
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLogAdapter = new LogsAdapter(layoutManager,this);
		deleteLogs = (FloatingActionButton)findViewById(R.id.clearLog);
		logList = (RecyclerView) findViewById(R.id.recyclerDrawerView);
		// Hide-log setting: reset the old true-by-default value once, so
		// existing installs get their logs back without clearing app data.
		mConfig.migrateHideLogDefault();
		updateLogVisibility();
		logList.setAdapter(mLogAdapter);
		logList.setLayoutManager(layoutManager);
		mLogAdapter.scrollToLastPosition();
		// One startup entry so the Log page always shows at least one line
		// and the user can tell at a glance that the log pipeline works.
		SkStatus.logInfo("Log siap. Atur profil di Custom, lalu tekan START.");
		deleteLogs.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					mLogAdapter.clearLog();
					deleteLogs.startAnimation(AnimationUtils.loadAnimation(PhreakersMainActivity.this, R.anim.grow));
					VPNUtils.NethToast(PhreakersMainActivity.this, R.drawable.check, "Logs is cleared");
				//	SkStatus.logInfo("<font color='green'>Logs Deleted By your Finger!</font>");
					
				}


			});
        greetingText = findViewById(R.id.greetingText);
        updateGreeting(greetingText);
		status = (MaterialTextView) findViewById(R.id.monsour_stats);

		// Initialize IP Address views
		tvIpAddress = (MaterialTextView) findViewById(R.id.tv_ip_address);
		btnRefreshIp = (ImageView) findViewById(R.id.btn_refresh_ip);
		ipAddressHelper = new IpAddressHelper();

		// Setup IP Address refresh button
		if (btnRefreshIp != null) {
		    btnRefreshIp.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            refreshIpAddress(true);
		        }
		    });
		}

		// Load IP address initially
		refreshIpAddress(false);
        bytesIn = (MaterialTextView) findViewById(R.id.download);
        bytesOut = (MaterialTextView) findViewById(R.id.upload);
        inputPwUser = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordUserEdit);
		inputPwPass = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordPassEdit);
	//	inputPwShowPass = (ImageView) findViewById(R.id.show_password);
	//	inputPwShowPass.setOnClickListener(this);
		final SharedPreferences prefsTxt = mConfig.getPrefsPrivate();
		inputPwUser.setText(prefsTxt.getString(Settings.USUARIO_KEY, ""));
		inputPwPass.setText(prefsTxt.getString(Settings.SENHA_KEY, ""));
		inputPwUser.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
					if(!s.toString().isEmpty()) {
						prefsTxt.edit().putString(Settings.USUARIO_KEY, s.toString()).apply();
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
		inputPwPass.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
					if(!s.toString().isEmpty()) {
						prefsTxt.edit().putString(Settings.SENHA_KEY, s.toString()).apply();
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
		
        
        buttonnethie = (Button) findViewById(R.id.nethiestart);
        buttonnethie.setOnClickListener(new Button.OnClickListener(){
        @Override
        public void onClick(View ugh){
            startOrStopTunnel(PhreakersMainActivity.this);
        }
        });
        if (statsTimer != null) { statsTimer.cancel(); }
        statsTimer = new Timer();
        statsTimer.schedule(new TimerTask(){@Override public void run() {new Handler(Looper.getMainLooper()).post(new Runnable() {@Override public void run() {
        getData();}});}}, 0,1000);
        // Custom profiles replace the old encrypted server/payload config.
        // Stats: Profil = jumlah profil tersimpan, Port = port profil aktif.
        refreshCustomStats();
        LinearLayout serverCardClickable = (LinearLayout) findViewById(R.id.serverCardClickable);
        if (serverCardClickable != null) {
            serverCardClickable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCustomList();
                }
            });
        }
        LinearLayout payloadCardClickable = (LinearLayout) findViewById(R.id.payloadCardClickable);
        if (payloadCardClickable != null) {
            payloadCardClickable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCustomEditor();
                }
            });
        }
    vp = (ViewPager)findViewById(R.id.viewpager);
        tabs = (TabLayout)findViewById(R.id.tablayout);
        vp.setAdapter(new NethPH(Arrays.asList(new String[]{"HOME", "LOGS"})));
        vp.setOffscreenPageLimit(2);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(vp);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

				@Override
				public void onPageScrolled(int p1, float p2, int p3) {
				}

				@Override
				public void onPageSelected(int p1) {
					switch (p1){
						case 0:
							deleteLogs.setVisibility(View.GONE);
							break;
					    case 1:
							new Handler().postDelayed(new Runnable(){
								@Override
								public void run(){
							if (!mConfig.getHideLog()) { deleteLogs.setVisibility(View.VISIBLE); }
							deleteLogs.startAnimation(AnimationUtils.loadAnimation(PhreakersMainActivity.this, R.anim.grow));
							}
							}, 100);
							break;
					}
				}

				@Override
				public void onPageScrollStateChanged(int p1) {
				}
				
			
		});
        bottomNav = (BottomNavigationView) findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            if (vp != null) vp.setCurrentItem(0);
                            return true;
                        case R.id.nav_custom:
                            openCustomList();
                            return true;
                        case R.id.nav_premium:
                            startActivity(new Intent(PhreakersMainActivity.this, SktPremium.class));
                            return true;
                        case R.id.nav_settings:
                            startActivity(new Intent(PhreakersMainActivity.this, ConfigGeralActivity.class));
                            return true;
                    }
                    return false;
                }
            });
        }
    }
    private void showReleaseNotes() {
        View inflate = LayoutInflater.from(PhreakersMainActivity.this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(PhreakersMainActivity.this);
        builer.setView(inflate);
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("Release Notes!");
        ms.setText(config.geNote());
        ok.setText("Ok, Close");
        cancel.setVisibility(View.GONE);
        final AlertDialog alert = builer.create();
        alert.setCanceledOnTouchOutside(false);
        alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
        alert.getWindow().setGravity(Gravity.CENTER);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) { alert.dismiss(); }
        });
        alert.show();
    }
    private String render_bandwidth(double bw) {
        String postfix;
        float div;
        Object[] objArr;
        float bwf = (float) bw;
        if (bwf >= 1.0E12f) {
            postfix = "TB";
            div = 1.0995116E12f;
        } else if (bwf >= 1.0E9f) {
            postfix = "GB";
            div = 1.0737418E9f;
        } else if (bwf >= 1000000.0f) {
            postfix = "MB";
            div = 1048576.0f;
        } else if (bwf >= 1000.0f) {
            postfix = "KB";
            div = 1024.0f;
        } else {
            objArr = new Object[1];
            objArr[0] = Float.valueOf(bwf);
            return String.format("%.0f", objArr);
        }
        objArr = new Object[2];
        objArr[0] = Float.valueOf(bwf / div);
        objArr[1] = postfix;
        return String.format("%.2f %s", objArr);
    }
    private void getData() {
        boolean isRunning = SkStatus.isTunnelActive();
        long mUpload, mDownload, saved_Send ,saved_Down/*,up, down*/;
        String saved_date, tDate;
        List<Long> allData;
        allData = NethPogi.findData();
        mDownload = allData.get(0);
        mUpload = allData.get(1);
        NethPogi.damn(mDownload, mUpload);
        //down = mDownload;
        //up = mUpload;
        SharedPreferences myData = getSharedPreferences("SkartiStats", MODE_PRIVATE);
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        tDate = sdf.format(ca.getTime());
        saved_date = myData.getString("today_date", "empty");
        SharedPreferences.Editor editor = myData.edit();
        if (saved_date.equals(tDate)) {
            saved_Send = myData.getLong("UP_DATA", 0);
            saved_Down = myData.getLong("DOWN_DATA", 0);
            editor.putLong("UP_DATA", mUpload + saved_Send);
            editor.putLong("DOWN_DATA", mDownload + saved_Down);
            editor.apply();
        } else {
            // Only reset the daily counters, never clear other preferences.
            editor.putLong("UP_DATA", 0);
            editor.putLong("DOWN_DATA", 0);
            editor.putString("today_date", tDate);
            editor.apply();
        }
        if(isRunning){
            // Only touch the TextViews when the rendered value actually
            // changed: setText every second forced a layout pass even when
            // the displayed bandwidth was identical, which showed up as lag.
            String upText = render_bandwidth(myData.getLong("UP_DATA", 0));
            String downText = render_bandwidth(myData.getLong("DOWN_DATA", 0));
            if (!upText.equals(lastUpText)) { bytesOut.setText(upText); lastUpText = upText; }
            if (!downText.equals(lastDownText)) { bytesIn.setText(downText); lastDownText = downText; }
        }else{
            SharedPreferences.Editor resetEditor = myData.edit();
            resetEditor.putLong("UP_DATA", 0);
            resetEditor.putLong("DOWN_DATA", 0);
            resetEditor.apply();
        }
    }
	
	private void doUpdateLayout() {
        
        setStarterButton(buttonnethie, this);
	}
	
		private synchronized void doSaveData() {
		try {
			SharedPreferences prefs = mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefs.edit();

			
			if (prefs.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				edit.putString(Settings.USUARIO_KEY, inputPwUser.getEditableText().toString());
				edit.putString(Settings.SENHA_KEY, inputPwPass.getEditableText().toString());
			}
			

			edit.apply();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void openCustomList() {
		Intent intent = new Intent(PhreakersMainActivity.this, com.skartivpn.app.custom.CustomProfileListActivity.class);
		startActivity(intent);
	}
	private void openCustomEditor() {
		Intent intent = new Intent(PhreakersMainActivity.this, com.skartivpn.app.custom.CustomProfileEditorActivity.class);
		CustomProfile active = CustomProfileStore.getActive(this);
		if (active != null) {
			intent.putExtra("PROFILE_ID", active.id);
		}
		startActivity(intent);
	}
	// Re-applies the hide-log setting to the log list. Called from doLayout
	// and from onResume, so toggling the setting in Pengaturan and coming
	// back actually shows (or hides) the log without restarting the app.
	private void updateLogVisibility() {
		if (logList == null) { return; }
		if (mConfig.getHideLog()) {
			logList.setVisibility(View.GONE);
			if (deleteLogs != null) { deleteLogs.setVisibility(View.GONE); }
		} else {
			logList.setVisibility(View.VISIBLE);
		}
	}
	private void refreshCustomStats() {
		try {
			List<CustomProfile> profiles = CustomProfileStore.load(this);
			CustomProfile active = CustomProfileStore.getActive(this);
			MaterialTextView serverCount = (MaterialTextView) findViewById(R.id.serverCount);
			if (serverCount != null) { serverCount.setText("" + profiles.size()); }
			MaterialTextView payCount = (MaterialTextView) findViewById(R.id.payCount);
			if (payCount != null) { payCount.setText(active == null ? "-" : active.effectivePort()); }
			MaterialTextView configv = (MaterialTextView) findViewById(R.id.configv);
			if (configv != null) { configv.setText(active == null ? "-" : active.modeLabel()); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Writes a custom profile into the same private preferences the old
	// _pos_() filled from the encrypted config, so LaunchVpn and the tunnel
	// engine work exactly as before. [HOST] in a payload is replaced by the
	// profile host, matching the old behaviour.
	private void applyCustomProfile(CustomProfile profile) {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false);
		edit.putString("ServerName", profile.name);
		if (profile.needsSlowDns()) {
			edit.putString(Settings.SERVIDOR_KEY, "127.0.0.1");
			edit.putString(Settings.SERVIDOR_PORTA_KEY, "2222");
			edit.putString(Settings.NAMESERVER_KEY, profile.host);
			edit.putString(Settings.CHAVE_KEY, profile.slowDnsKey);
			edit.putString(Settings.DNS_KEY, profile.dnsKey);
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SLOWDNS);
		} else {
			edit.putString(Settings.SERVIDOR_KEY, profile.host);
			edit.putString(Settings.SERVIDOR_PORTA_KEY, profile.effectivePort());
			edit.putInt(Settings.TUNNELTYPE_KEY, profile.mode);
			if (profile.needsProxy()) {
				edit.putString(Settings.PROXY_IP_KEY, profile.proxyHost);
				edit.putString(Settings.PROXY_PORTA_KEY, profile.proxyPort);
			} else {
				edit.putString(Settings.PROXY_IP_KEY, "");
				edit.putString(Settings.PROXY_PORTA_KEY, "");
			}
			if (profile.needsSni()) {
				edit.putString(Settings.CUSTOM_SNI, profile.sni);
			} else {
				edit.putString(Settings.CUSTOM_SNI, "");
			}
			if (profile.needsPayload()) {
				String payload = profile.payload == null ? "" : profile.payload;
				edit.putString(Settings.CUSTOM_PAYLOAD_KEY, payload.replace("[HOST]", profile.host));
			} else {
				edit.putString(Settings.CUSTOM_PAYLOAD_KEY, "");
			}
		}
		edit.apply();
	}
	public void doRestart() {
        finish();
        startActivity(new Intent(this,LauncherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
	}
	
	public void startOrStopTunnel(Activity activity) {
		if (SkStatus.isTunnelActive()) {
			TunnelManagerHelper.stopSocksHttp(activity);
		}
		else {
			// oculta teclado se vísivel, tá com bug, tela verde
			//Utils.hideKeyboard(activity);
            CustomProfile activeProfile = CustomProfileStore.getActive(this);
            if (activeProfile == null) {
                SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                           ConnectionStatus.LEVEL_NOTCONNECTED);
                Toast.makeText(this, "Buat profil Custom dulu.", Toast.LENGTH_SHORT).show();
                openCustomList();
                return;
            }
            if (mConfig.getAutoClearLog() && mLogAdapter != null) { mLogAdapter.clearLog(); }
            applyCustomProfile(activeProfile);
Intent intent = new Intent(activity, LaunchVpn.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			activity.startActivity(intent);
		}
	}
    String getName(String str){
      return mConfig.getPrivString(str);  
    }
    private void updateConfig(final boolean isOnCreate) {
        new ConfigUpdate(this, new ConfigUpdate.OnUpdateListener() {
                @Override
                public void onUpdateListener(String result) {
                    try {
                        if (!result.contains("Error on getting data")) {
                            String json_data = AESCrypt.decrypt(config.PASSWORD, result);
                            if (isNewVersion(json_data)) {
                              newUpdateDialog(result);
                            } else {
                                if (!isOnCreate) {
                                   noUpdateDialog();
                                }
                            }
                        } else if(result.contains("Error on getting data") && !isOnCreate){
                           errorUpdateDialog(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start(isOnCreate);

    }

    private boolean isNewVersion(String result) {
        try {
            String current = config.getVersion();
            String update = new JSONObject(result).getString("Version");
            return config.versionCompare(update, current);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
	}
    private void newUpdateDialog(final String result) throws JSONException, GeneralSecurityException{
        String json_data = AESCrypt.decrypt(config.PASSWORD, result);
        String notes = new JSONObject(json_data).getString("ReleaseNotes");
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("New Update Available");
        ms.setText(notes);
        ok.setText("Update & Restart");
        cancel.setText("Dismiss");
        cancel.setVisibility(View.VISIBLE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
         
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    try
                    {
                        File file = new File(getFilesDir(), "Config.json");
                        OutputStream out = new FileOutputStream(file);
                        out.write(result.getBytes());
                        out.flush();
                        out.close();
						mConfig.getPrefsPrivate().edit()
						.putString("UpdateTime", VPNUtils.getTime01(PhreakersMainActivity.this)).apply();
                        doRestart();
                        alert.dismiss();
                        
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }

            });
        alert.show();
    }
    private void noUpdateDialog() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("No Update");
        ms.setText("Latest config version is already installed");
        ok.setText("Ok,Close");
        cancel.setText(".");
        cancel.setVisibility(View.GONE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
         
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    alert.dismiss();
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }




            });
        alert.show();
    }
   private void clerd() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("Clear Data");
		ms.setText("You sure want to clear data? This cannot be undone!");
		ok.setText("Clear");
		cancel.setText("Cancel");
		cancel.setVisibility(View.VISIBLE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    		try {
						// clearing app data
						String packageName = getApplicationContext().getPackageName();
						Runtime runtime = Runtime.getRuntime();
						runtime.exec("pm clear "+packageName);
						Toast.makeText(getApplicationContext(), "Done! Reopen the app!", 0).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), e.getMessage(),0).show();
					}
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }




            });
        alert.show();
    }
    
    private void errorUpdateDialog(String error) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("Error");
        ms.setText(error.replace(VPNUtils.ConfigUrl, "**********"));
        ok.setText("Ok,Close");
        cancel.setText(".");
        cancel.setVisibility(View.GONE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
         
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    alert.dismiss();
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }

            });
        alert.show();
	}
 
	public void setStarterButton(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;
			
			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();

			if (ConfigParser.isValidadeExpirou(prefsPrivate
					.getLong(Settings.CONFIG_VALIDADE_KEY, 0))) {
				resId = R.string.expired;
				starterButton.setEnabled(false);

				if (isRunning) {
					startOrStopTunnel(activity);
				}
			}
			else if (prefsPrivate.getBoolean(Settings.BLOQUEAR_ROOT_KEY, false) &&
					ConfigParser.isDeviceRooted(activity)) {
			   resId = R.string.blocked;
			   starterButton.setEnabled(false);
			   Toast.makeText(activity, R.string.error_root_detected, Toast.LENGTH_SHORT)
					.show();
			   if (isRunning) {
				   startOrStopTunnel(activity);
			   }
			}
			else if (SkStatus.SSH_INICIANDO.equals(state)) {
				resId = R.string.stop;
                vp.setCurrentItem(1);
				
				starterButton.setEnabled(false);
			}
			else if (SkStatus.SSH_PARANDO.equals(state)) {
				resId = R.string.state_stopping;
				
				starterButton.setEnabled(false);
			}
			else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
			}
			starterButton.setText(resId);
		}
	}
	
	// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
	@Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }
    // Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
    
public void jointele (View view){
		 String url = "http://t.me/SaputraTech";
		 Intent openTele = new Intent(Intent.ACTION_VIEW);
		 openTele.setData(Uri.parse(url));
		 try {
             startActivity(openTele);
         } catch (Exception e) {
             Toast.makeText(this, "Telegram app not installed or URL error.", Toast.LENGTH_SHORT).show();
         }
	 }

	 // Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
	public void joinwa (View view){
		 String url = "http://t.me/PhreakersVPN"; // This is a Telegram URL, not WhatsApp
		 Intent openWa = new Intent(Intent.ACTION_VIEW);
		 openWa.setData(Uri.parse(url));
		  try {
             startActivity(openWa);
         } catch (Exception e) {
             Toast.makeText(this, "Telegram app not installed or URL error (for WA button).", Toast.LENGTH_SHORT).show();
         }
	 }

	 // Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.

    public void joinyt (View view){
		 String url = "https://wa.me/6283845974466"; // This is a WhatsApp group URL
		 Intent openYt= new Intent(Intent.ACTION_VIEW);
		 openYt.setData(Uri.parse(url));
		  try {
             startActivity(openYt);
         } catch (Exception e) {
             Toast.makeText(this, "WhatsApp not installed or URL error.", Toast.LENGTH_SHORT).show();
         }
	 }
    

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
   }
	@Override
	public void updateState(final String state, String msg, int localizedResId, final ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				doUpdateLayout();
				if(SkStatus.isTunnelActive()){
					if(level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
						status.setText(R.string.state_connected);
						// Refresh IP address when VPN connects
						refreshIpAddress(false);
                       
					}
					if(level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)){
						status.setText(R.string.state_disconnected);
						// Refresh IP address when VPN disconnects
						refreshIpAddress(false);
					}
					if(level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)){
						status.setText( R.string.state_auth);
					}
					if(level.equals(ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)){
						status.setText(R.string.state_connecting);
					}
					if(level.equals(ConnectionStatus.UNKNOWN_LEVEL)){
						status.setText(R.string.state_disconnected);
                        
					}
					/*if(level.equals(ConnectionStatus.LEVEL_RECONNECTANDO)){
					status.setText(R.string.state_reconnecting);}*/
				}
				if(level.equals(ConnectionStatus.LEVEL_NONETWORK)){
					status.setText(R.string.state_nonetwork);
				}
				if(level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)){
					status.setText(R.string.state_auth_failed);
				}

			}
		});
		
		switch (state) {
			case SkStatus.SSH_CONECTADO:
				// carrega ads banner
				
		}
	}

	private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(UPDATE_VIEWS) && !isFinishing()) {
				doUpdateLayout();
			
				}
			}
        
    };


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.miSettings:
                Intent intentSettings = new Intent(this, ConfigGeralActivity.class);
                intentSettings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSettings);
                break;

		   
        }
        
        return false;
    }
	@Override
	public void onBackPressed() {
		showExitDialog();
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 550)
        {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    if (uri == null) { throw new IOException("No file selected"); }
                    String intentData = importer(uri);
                    tryImportConfig(intentData);
                    doRestart();
                } catch (Exception e) {
                    e.printStackTrace();
                    VPNUtils.NethToast(this, R.drawable.skarti_wrong, "Import gagal: config tidak valid, file lama dipertahankan.");
                }
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleExternalConfigIntent(intent);
    }

    // Called when the app is opened by tapping a .skarti/.json file in
    // WhatsApp, Telegram, Files, etc. Shows a confirmation first and never
    // overwrites the current config without validation + backup.
    private void handleExternalConfigIntent(Intent intent) {
        if (intent == null || !Intent.ACTION_VIEW.equals(intent.getAction())) { return; }
        Uri uri = intent.getData();
        if (uri == null) { return; }
        String raw = importer(uri);
        if (raw == null || raw.trim().isEmpty()) {
            VPNUtils.NethToast(this, R.drawable.skarti_wrong, "Import gagal: file kosong atau tidak bisa dibaca.");
            return;
        }
        String fileName = uri.getLastPathSegment();
        try {
            android.database.Cursor c = getContentResolver().query(uri, new String[]{android.provider.OpenableColumns.DISPLAY_NAME}, null, null, null);
            if (c != null) {
                try { if (c.moveToFirst()) { fileName = c.getString(0); } } finally { c.close(); }
            }
        } catch (Exception ignored) {}
        final String content = raw;
        final String name = fileName != null ? fileName : "config";
        new AlertDialog.Builder(this)
            .setTitle("Import Config?")
            .setMessage("File " + name + " akan menggantikan config saat ini. Config lama dibackup ke Config.json.bak. Lanjutkan?")
            .setPositiveButton("Import", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface d, int which) {
                    try {
                        tryImportConfig(content);
                        VPNUtils.NethToast(PhreakersMainActivity.this, R.drawable.check, "Config berhasil diimport, aplikasi akan restart.");
                        doRestart();
                    } catch (Exception e) {
                        e.printStackTrace();
                        VPNUtils.NethToast(PhreakersMainActivity.this, R.drawable.skarti_wrong, "Import gagal: config tidak valid, file lama dipertahankan.");
                    }
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    // Validates + installs a config string. Accepts the encrypted .skarti
    // format (stored as-is) and plain JSON (encrypted before storing, since
    // ConfigUtil always decrypts Config.json on read). Throws on invalid.
    private void tryImportConfig(String raw) throws Exception {
        String toStore;
        String trimmed = raw.trim();
        try {
            String decrypted = AESCrypt.decrypt(ConfigUtil.PASSWORD, trimmed);
            org.json.JSONObject j = new org.json.JSONObject(decrypted);
            if (!j.has("Servers") || !j.has("Networks")) { throw new IOException("Invalid config: Servers/Networks missing"); }
            toStore = trimmed;
        } catch (Exception e) {
            // Not valid encrypted JSON: try plain JSON, then encrypt it for
            // storage (ConfigUtil decrypts Config.json on every read).
            org.json.JSONObject j = new org.json.JSONObject(trimmed);
            if (!j.has("Servers") || !j.has("Networks")) { throw new IOException("Invalid config: Servers/Networks missing"); }
            toStore = AESCrypt.encrypt(ConfigUtil.PASSWORD, trimmed);
        }
        File file = new File(getFilesDir(), "Config.json");
        File backup = new File(getFilesDir(), "Config.json.bak");
        if (file.exists()) {
            java.nio.file.Files.copy(file.toPath(), backup.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        OutputStream out = new FileOutputStream(file);
        out.write(toStore.getBytes());
        out.flush();
        out.close();
        mConfig.getPrefsPrivate().edit().putString("UpdateTime", VPNUtils.getTime01(PhreakersMainActivity.this)).apply();
    }

    private String importer(Uri uri)
    {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
	} 

	
@Override
public void onResume() {
    super.onResume();
    SkStatus.addStateListener(this);
    refreshCustomStats();
    updateLogVisibility();
   // mHandler.post(greetingRunnable); // [NEW] Start greeting updates
}

@Override
protected void onPause() {
    super.onPause();
    SkStatus.removeStateListener(this);
   // mHandler.removeCallbacks(greetingRunnable); // [NEW] Stop greeting updates
}
    
	@Override
	protected void onDestroy()
	{
		if (statsTimer != null) { statsTimer.cancel(); statsTimer = null; }
		if (mHandler != null) { mHandler.removeCallbacksAndMessages(null); }
		super.onDestroy();
		//mDrawer.onDestroy();
		LocalBroadcastManager.getInstance(this)
			.unregisterReceiver(mActivityReceiver);

		// Cleanup IP address helper
		if (ipAddressHelper != null) {
			ipAddressHelper.cleanup();
		}

	}       
	public static void updateMainViews(Context context) {
		Intent updateView = new Intent(UPDATE_VIEWS);
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(updateView);
	}
	
	public void showExitDialog() {
		AlertDialog dialog = new MaterialAlertDialogBuilder(this).
			create();
		dialog.setTitle(getString(R.string.attention));
		dialog.setMessage(getString(R.string.alert_exit));

		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.
				string.exit),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Utils.exitAll(PhreakersMainActivity.this);
				}
			}
		);

		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.
				string.minimize),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// minimiza app
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
				}
			}
		);

		dialog.show();
	}
	
}

// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
// Credit By : @SaputraTech, @SkartiVPN.
