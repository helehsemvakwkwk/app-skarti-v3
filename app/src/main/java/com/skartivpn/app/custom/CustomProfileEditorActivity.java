package com.skartivpn.app.custom;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.skartivpn.app.activities.BaseActivity;
import com.skartivpn.inject.config.Settings;
import com.skarti.premium.v2.R;

import java.util.List;

public class CustomProfileEditorActivity extends BaseActivity {

    private static final int[] MODES = new int[]{
            Settings.bTUNNEL_TYPE_SSH_DIRECT,
            Settings.bTUNNEL_TYPE_SSH_PROXY,
            Settings.bTUNNEL_TYPE_SSL_TLS,
            Settings.bTUNNEL_TYPE_PAY_SSL,
            Settings.bTUNNEL_TYPE_SLOWDNS
    };

    private CustomProfile editing;
    private int selectedMode = Settings.bTUNNEL_TYPE_SSH_DIRECT;

    private TextInputEditText inputName;
    private TextInputEditText inputHost;
    private TextInputEditText inputPort;
    private TextInputEditText inputSni;
    private TextInputEditText inputPayload;
    private TextInputEditText inputProxyHost;
    private TextInputEditText inputProxyPort;
    private TextInputEditText inputSlowDnsKey;
    private TextInputEditText inputDnsKey;
    private View sniSection;
    private View payloadSection;
    private View proxySection;
    private View slowdnsSection;
    private ChipGroup modeChipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_profile_editor);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar_custom_editor);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        inputName = (TextInputEditText) findViewById(R.id.input_profile_name);
        inputHost = (TextInputEditText) findViewById(R.id.input_custom_host);
        inputPort = (TextInputEditText) findViewById(R.id.input_custom_port);
        inputSni = (TextInputEditText) findViewById(R.id.input_custom_sni);
        inputPayload = (TextInputEditText) findViewById(R.id.input_custom_payload);
        inputProxyHost = (TextInputEditText) findViewById(R.id.input_proxy_host);
        inputProxyPort = (TextInputEditText) findViewById(R.id.input_proxy_port);
        inputSlowDnsKey = (TextInputEditText) findViewById(R.id.input_slowdns_key);
        inputDnsKey = (TextInputEditText) findViewById(R.id.input_dns_key);
        sniSection = findViewById(R.id.sni_section);
        payloadSection = findViewById(R.id.payload_section);
        proxySection = findViewById(R.id.proxy_section);
        slowdnsSection = findViewById(R.id.slowdns_section);
        modeChipGroup = (ChipGroup) findViewById(R.id.mode_chip_group);

        String profileId = getIntent() == null ? null : getIntent().getStringExtra("PROFILE_ID");
        if (profileId != null && !profileId.isEmpty()) {
            List<CustomProfile> profiles = CustomProfileStore.load(this);
            for (CustomProfile profile : profiles) {
                if (profile.id.equals(profileId)) {
                    editing = profile;
                    break;
                }
            }
        }
        if (editing == null) {
            editing = new CustomProfile();
        }
        selectedMode = editing.mode;

        setupModeChips();
        fillForm();
        updateSections();

        setupInsertButton(R.id.btn_insert_host, "[host]");
        setupInsertButton(R.id.btn_insert_port, "[port]");
        setupInsertButton(R.id.btn_insert_crlf, "[crlf]");
        setupInsertButton(R.id.btn_insert_protocol, "[protocol]");

        Button saveButton = (Button) findViewById(R.id.btn_save_custom);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void setupModeChips() {
        modeChipGroup.removeAllViews();
        for (int i = 0; i < MODES.length; i++) {
            final int mode = MODES[i];
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_filter_chip, modeChipGroup, false);
            CustomProfile tmp = new CustomProfile();
            tmp.mode = mode;
            chip.setText(tmp.modeLabel());
            chip.setId(View.generateViewId());
            chip.setTag(Integer.valueOf(mode));
            chip.setChecked(mode == selectedMode);
            modeChipGroup.addView(chip);
        }
        modeChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = (Chip) group.findViewById(checkedId);
                if (chip != null && chip.getTag() instanceof Integer) {
                    selectedMode = ((Integer) chip.getTag()).intValue();
                    updateSections();
                }
            }
        });
    }

    private void fillForm() {
        setText(inputName, editing.name);
        setText(inputHost, editing.host);
        setText(inputPort, editing.port);
        setText(inputSni, editing.sni);
        setText(inputPayload, editing.payload);
        setText(inputProxyHost, editing.proxyHost);
        setText(inputProxyPort, editing.proxyPort);
        setText(inputSlowDnsKey, editing.slowDnsKey);
        setText(inputDnsKey, editing.dnsKey);
        if (editing.id == null || editing.id.isEmpty()) {
            setText(inputPort, "22");
        }
    }

    private void setText(EditText editText, String value) {
        editText.setText(value == null ? "" : value);
    }

    private String textOf(EditText editText) {
        Editable editable = editText.getText();
        return editable == null ? "" : editable.toString().trim();
    }

    private void updateSections() {
        CustomProfile tmp = new CustomProfile();
        tmp.mode = selectedMode;
        sniSection.setVisibility(tmp.needsSni() ? View.VISIBLE : View.GONE);
        payloadSection.setVisibility(tmp.needsPayload() ? View.VISIBLE : View.GONE);
        proxySection.setVisibility(tmp.needsProxy() ? View.VISIBLE : View.GONE);
        slowdnsSection.setVisibility(tmp.needsSlowDns() ? View.VISIBLE : View.GONE);
        if (tmp.needsSlowDns()) {
            inputPort.setText("2222");
            inputPort.setEnabled(false);
        } else {
            inputPort.setEnabled(true);
        }
    }

    private void setupInsertButton(int buttonId, final String snippet) {
        Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int start = inputPayload.getSelectionStart();
                if (start < 0) {
                    start = textOf(inputPayload).length();
                }
                Editable editable = inputPayload.getText();
                if (editable != null) {
                    editable.insert(start, snippet);
                }
            }
        });
    }

    private boolean validPort(String port) {
        try {
            int value = Integer.parseInt(port);
            return value >= 1 && value <= 65535;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveProfile() {
        String name = textOf(inputName);
        String host = textOf(inputHost);
        String port = textOf(inputPort);
        String sni = textOf(inputSni);
        String payload = textOf(inputPayload);
        String proxyHost = textOf(inputProxyHost);
        String proxyPort = textOf(inputProxyPort);
        String slowDnsKey = textOf(inputSlowDnsKey);
        String dnsKey = textOf(inputDnsKey);

        if (name.isEmpty()) {
            Toast.makeText(this, "Nama profil wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (host.isEmpty()) {
            Toast.makeText(this, "Host wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }
        CustomProfile tmp = new CustomProfile();
        tmp.mode = selectedMode;
        if (!tmp.needsSlowDns() && !validPort(port)) {
            Toast.makeText(this, "Port harus angka 1-65535.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tmp.needsSni() && sni.isEmpty()) {
            Toast.makeText(this, "SNI wajib diisi untuk mode SSL.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tmp.needsPayload() && payload.isEmpty()) {
            Toast.makeText(this, "Payload wajib diisi untuk mode ini.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tmp.needsProxy()) {
            if (proxyHost.isEmpty()) {
                Toast.makeText(this, "Proxy host wajib diisi untuk mode SSH Proxy.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!validPort(proxyPort)) {
                Toast.makeText(this, "Proxy port harus angka 1-65535.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        editing.name = name;
        editing.host = host;
        editing.mode = selectedMode;
        if (tmp.needsSlowDns()) {
            editing.port = "2222";
            editing.slowDnsHost = host;
            editing.slowDnsKey = slowDnsKey;
            editing.dnsKey = dnsKey;
            editing.sni = "";
            editing.payload = "";
            editing.proxyHost = "";
            editing.proxyPort = "";
        } else {
            editing.port = port;
            editing.sni = tmp.needsSni() ? sni : "";
            editing.payload = tmp.needsPayload() ? payload : "";
            editing.proxyHost = tmp.needsProxy() ? proxyHost : "";
            editing.proxyPort = tmp.needsProxy() ? proxyPort : "";
            editing.slowDnsKey = "";
            editing.slowDnsHost = "";
            editing.dnsKey = "";
        }

        CustomProfileStore.upsert(this, editing);
        CustomProfileStore.setActive(this, editing.id);
        Toast.makeText(this, "Profil tersimpan dan diaktifkan: " + name, Toast.LENGTH_SHORT).show();
        finish();
    }
}
