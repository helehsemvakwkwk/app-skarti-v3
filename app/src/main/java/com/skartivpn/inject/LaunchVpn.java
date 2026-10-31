package com.skartivpn.inject;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.skartivpn.inject.logger.SkStatus;
import com.skartivpn.inject.logger.ConnectionStatus;
import com.skartivpn.inject.tunnel.TunnelManagerHelper;
import android.os.Build;
import com.skartivpn.inject.config.Settings;
import android.net.VpnService;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.widget.Toast;
import com.skartivpn.inject.tunnel.TunnelUtils;
import android.widget.EditText;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.annotation.SuppressLint;
import android.widget.CheckBox;
import androidx.core.widget.CompoundButtonCompat;
import android.widget.CompoundButton;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.RemoteException;
import com.skartivpn.inject.config.PasswordCache;
import android.content.SharedPreferences;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.skarti.premium.v2.R;
import android.widget.LinearLayout;
public class LaunchVpn extends AppCompatActivity
implements DialogInterface.OnCancelListener
{
    public static final String EXTRA_HIDELOG = "com.skarti.showNoLogWindow";
    public static final String CLEARLOG = "clearlogconnect";

    private static final int START_VPN_PROFILE = 70;

    private Settings mConfig;
    private String mTransientAuthPW;
    private boolean mhideLog = false;
    private boolean isMostrarSenha = false;

@Override
public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    LinearLayout ll = new LinearLayout(this);
    ll.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
    setContentView(ll);
    mConfig = new Settings(this);
    
    // Ambil username dan password dari SharedPreferences
    String savedUsername = mConfig.getPrivString(Settings.USUARIO_KEY);
    String savedPassword = mConfig.getPrivString(Settings.SENHA_KEY);
    
    // Jika username dan password sudah ada, langsung lanjutkan ke proses VPN
    if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
        // Lanjutkan ke proses VPN
        launchVPN();

    }
}


 private void askForPW(final int type) {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(R.string.title_auth);
    dialog.setMessage(R.string.auth_message);
    dialog.setOnCancelListener(this);

    @SuppressLint("InflateParams")
    final View userpwlayout = getLayoutInflater()
            .inflate(R.layout.userpass, null, false);

    final EditText usernameEditText = userpwlayout.findViewById(R.id.username);
    final EditText passwordEditText = userpwlayout.findViewById(R.id.password);
    final CheckBox savePasswordCheckbox = userpwlayout.findViewById(R.id.save_password);
    final ImageButton showPasswordButton = userpwlayout.findViewById(R.id.show_password);

    // Mengisi nilai awal dari SharedPreferences
    usernameEditText.setText(mConfig.getPrivString(Settings.USUARIO_KEY));
    passwordEditText.setText(mConfig.getPrivString(Settings.SENHA_KEY));
    savePasswordCheckbox.setChecked(true);

    // Tombol untuk menampilkan/menyembunyikan password
    showPasswordButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isMostrarSenha = !isMostrarSenha;
            if (isMostrarSenha) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordButton.setImageDrawable(ContextCompat.getDrawable(LaunchVpn.this, R.drawable.ic_visibility_off_black_24dp));
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordButton.setImageDrawable(ContextCompat.getDrawable(LaunchVpn.this, R.drawable.ic_visibility_black_24dp));
            }
        }
    });

    dialog.setView(userpwlayout);

    dialog.setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();

                    // Menyimpan username
                    String mUsername = usernameEditText.getText().toString();
                    edit.putString(Settings.USUARIO_KEY, mUsername);

                    // Menyimpan atau menghapus password
                    String pw = passwordEditText.getText().toString();
                    if (savePasswordCheckbox.isChecked()) {
                        edit.putString(Settings.SENHA_KEY, pw);
                    } else {
                        edit.remove(Settings.SENHA_KEY); // Hapus password jika tidak ingin disimpan
                    }

                    edit.apply();

                    // Lanjutkan proses VPN
                    onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
                }
            });

    dialog.setNegativeButton(android.R.string.cancel,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                            ConnectionStatus.LEVEL_NOTCONNECTED);
                    finish();
                }
            });

    dialog.create().show();
}



    @Override
    public void onCancel(DialogInterface p1)
    {
        SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                   ConnectionStatus.LEVEL_NOTCONNECTED);
        finish();
    }

    private void showLogWindow() {
        Intent updateView = new Intent("com.skartivpn.app:openLogs");
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(updateView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferences prefs = mConfig.getPrefsPrivate();

                if (!TunnelUtils.isNetworkOnline(this)) {
                    SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                               ConnectionStatus.LEVEL_NOTCONNECTED);

                    Toast.makeText(this, R.string.error_internet_off,
                                   Toast.LENGTH_SHORT).show();

                    finish();
                }
                else if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY &&
                         (mConfig.getPrivString(Settings.PROXY_IP_KEY).isEmpty() || mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty())) {
                    SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                               ConnectionStatus.LEVEL_NOTCONNECTED);

                    Toast.makeText(this, R.string.error_proxy_invalid,
                                   Toast.LENGTH_SHORT).show();

                    finish();
                }
                else if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) && mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).isEmpty()) {
                    SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                               ConnectionStatus.LEVEL_NOTCONNECTED);

                    Toast.makeText(this, R.string.error_empty_payload,
                                   Toast.LENGTH_SHORT).show();

                    finish();
                }
                else if (mConfig.getPrivString(Settings.SERVIDOR_KEY).isEmpty() || mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY).isEmpty()) {
                    SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                               ConnectionStatus.LEVEL_NOTCONNECTED);

                    Toast.makeText(this, R.string.error_empty_settings,
                                   Toast.LENGTH_SHORT).show();

               /**     Intent startLW = new Intent();
                    startLW.setComponent(new ComponentName(this, getPackageName() + ".activities.ConfigGeralActivity"));
                    startLW.setAction("openSSHScreen");
                    startLW.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startLW);*/

                    finish();
                }
                else if (mConfig.getPrivString(Settings.USUARIO_KEY).isEmpty() || (mConfig.getPrivString(Settings.SENHA_KEY).isEmpty() &&
                         (mTransientAuthPW == null || mTransientAuthPW.isEmpty()))) {
                    SkStatus.updateStateString("USER_VPN_PASSWORD", "", R.string.state_user_vpn_password,
                                               ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);

                    askForPW(R.string.password);
                }
                else {
                    if (!mhideLog) {
                        showLogWindow();
                    }

                    TunnelManagerHelper.startSocksHttp(this);

                    finish();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                SkStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                                           ConnectionStatus.LEVEL_NOTCONNECTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    SkStatus.logError(R.string.nought_alwayson_warning);

                finish();
            }
        }
    }

    private void launchVPN() {
        Intent intent = VpnService.prepare(this);

        if (intent != null) {
            SkStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                                       ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                SkStatus.logError(R.string.no_vpn_support_image);
                showLogWindow();
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }

}
