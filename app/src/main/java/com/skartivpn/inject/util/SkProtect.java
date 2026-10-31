package com.skartivpn.inject.util;

import android.content.Context;

import com.skarti.premium.v2.R;

/**
 * @author Skank3r
 */
public class SkProtect {

    private static final String TAG = SkProtect.class.getSimpleName();

	private static final String APP_BASE = "com.skarti.premium.v2";
    
    private static final String APP_NAME = "SKARTI PRO DESIGN V2";

	private static SkProtect mInstance;

	private Context mContext;
	
	public static void init(Context context) {
		if (mInstance == null) {
			mInstance = new SkProtect(context);

		}
	}

	private SkProtect(Context context) {
		mContext = context;
	}


    public void simpleProtect() {
        if (!APP_BASE.equals(mContext.getPackageName().toLowerCase()) ||
            !mContext.getString(R.string.app_name).toLowerCase().equals(APP_NAME)) {
            throw new RuntimeException();
        }
	}

	public static void CharlieProtect() {
		if (mInstance == null) return;
			
		mInstance.simpleProtect();

	}
}
