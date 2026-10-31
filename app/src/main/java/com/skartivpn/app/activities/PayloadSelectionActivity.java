package com.skartivpn.app.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.skartivpn.app.adapter.PayloadSelectionAdapter;
import com.skartivpn.app.model.PayloadItem;
import com.skarti.premium.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern Payload Selection Activity
 * Features:
 * - Search bar with real-time filtering + clear button
 * - Debouncing (300ms) for optimized search
 * - Quick filter chips (All, Direct, SNI, Slowdns, etc)
 * - RecyclerView with smooth animations
 * - Visual feedback on selection
 * - Responsive design
 */
public class PayloadSelectionActivity extends BaseActivity implements PayloadSelectionAdapter.OnPayloadSelectedListener {

    private MaterialToolbar toolbar;
    private TextInputEditText searchInput;
    private ImageView clearSearchButton;
    private ChipGroup filterChipGroup;
    private RecyclerView payloadRecyclerView;
    private TextView emptyStateText;
    private ImageView emptyStateIcon;

    private PayloadSelectionAdapter adapter;
    private List<PayloadItem> allPayloads;
    private List<PayloadItem> filteredPayloads;
    private String currentFilter = "ALL";
    private String selectedPayloadId = null;
    private int selectedPosition = -1;
    
    // Debouncing handler
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload_selection);

        // Get previously selected payload
        Intent intent = getIntent();
        if (intent != null) {
            selectedPayloadId = intent.getStringExtra("SELECTED_PAYLOAD_ID");
        }

        initViews();
        setupToolbar();
        setupSearchBar();
        setupFilterChips();
        setupRecyclerView();
        loadPayloads();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_payload_selection);
        searchInput = findViewById(R.id.search_input_payload);
        clearSearchButton = findViewById(R.id.clear_search_button_payload);
        filterChipGroup = findViewById(R.id.filter_chip_group_payload);
        payloadRecyclerView = findViewById(R.id.payload_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text_payload);
        emptyStateIcon = findViewById(R.id.empty_state_icon_payload);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Payload");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSearchBar() {
        // Clear button click listener
        clearSearchButton.setOnClickListener(v -> {
            searchInput.setText("");
            searchInput.clearFocus();
            // Hide keyboard
            android.view.inputmethod.InputMethodManager imm = 
                (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
            }
        });
        
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                if (s.length() > 0) {
                    clearSearchButton.setVisibility(View.VISIBLE);
                } else {
                    clearSearchButton.setVisibility(View.GONE);
                }
                
                // Debouncing: Remove previous callback
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                // Create new callback
                searchRunnable = () -> filterPayloads(s.toString());
                
                // Post delayed (300ms debounce)
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips() {
        String[] filters = {"All", "Direct", "SNI", "Slowdns", "SSL/TLS", "Websocket"};
        
        for (int i = 0; i < filters.length; i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_filter_chip, filterChipGroup, false);
            chip.setText(filters[i]);
            chip.setTag(filters[i].toUpperCase());
            
            if (i == 0) {
                chip.setChecked(true);
            }
            
            chip.setOnClickListener(v -> {
                Chip clickedChip = (Chip) v;
                currentFilter = clickedChip.getTag().toString();
                filterPayloads(searchInput.getText().toString());
                
                // Animate chip selection
                animateChipSelection(clickedChip);
            });
            
            filterChipGroup.addView(chip);
        }
    }

    private void setupRecyclerView() {
        allPayloads = new ArrayList<>();
        filteredPayloads = new ArrayList<>();
        
        adapter = new PayloadSelectionAdapter(this, filteredPayloads, this, selectedPayloadId);
        payloadRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        payloadRecyclerView.setAdapter(adapter);
        
        // Add item animation
        payloadRecyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
    }

    private void loadPayloads() {
        // TODO: Load payloads from your data source (SharedPreferences, Database, or API)
        // For now, adding sample data
        allPayloads.clear();
        
        // Sample payloads - replace with your actual data loading logic
        allPayloads.add(new PayloadItem("1", "Direct Connection", "Direct connection without proxy", "DIRECT", true));
        allPayloads.add(new PayloadItem("2", "SNI Bug", "GET / HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf]Upgrade: websocket[crlf][crlf]", "SNI", true));
        allPayloads.add(new PayloadItem("3", "Slowdns Mode", "Slowdns tunneling configuration", "SLOWDNS", true));
        allPayloads.add(new PayloadItem("4", "SSL/TLS Direct", "SSL/TLS direct connection", "SSL/TLS", true));
        allPayloads.add(new PayloadItem("5", "Websocket CDN", "GET wss://[host]/ HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf]Upgrade: websocket[crlf][crlf]", "WEBSOCKET", true));
        allPayloads.add(new PayloadItem("6", "Custom SNI 1", "GET / HTTP/1.1[crlf]Host: [custom_host][crlf]Connection: keep-alive[crlf][crlf]", "SNI", true));
        allPayloads.add(new PayloadItem("7", "Custom SNI 2", "CONNECT [host]:443 HTTP/1.0[crlf][crlf]GET / HTTP/1.0[crlf]Host: [host][crlf][crlf]", "SNI", false));
        allPayloads.add(new PayloadItem("8", "Game Mode", "Optimized payload for gaming", "DIRECT", true));
        allPayloads.add(new PayloadItem("9", "Streaming Mode", "Optimized payload for video streaming", "WEBSOCKET", true));
        allPayloads.add(new PayloadItem("10", "Low Latency", "Low latency configuration", "DIRECT", true));
        
        filterPayloads("");
    }

    private void filterPayloads(String query) {
        filteredPayloads.clear();
        
        for (PayloadItem payload : allPayloads) {
            boolean matchesQuery = query.isEmpty() || 
                                   payload.getName().toLowerCase().contains(query.toLowerCase()) ||
                                   payload.getDescription().toLowerCase().contains(query.toLowerCase());
            
            boolean matchesFilter = currentFilter.equals("ALL") || 
                                    payload.getType().equalsIgnoreCase(currentFilter);
            
            if (matchesQuery && matchesFilter) {
                filteredPayloads.add(payload);
            }
        }
        
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredPayloads.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateIcon.setVisibility(View.VISIBLE);
            payloadRecyclerView.setVisibility(View.GONE);
            
            // Fade in animation
            emptyStateText.setAlpha(0f);
            emptyStateIcon.setAlpha(0f);
            emptyStateText.animate().alpha(1f).setDuration(300).start();
            emptyStateIcon.animate().alpha(1f).setDuration(300).start();
        } else {
            emptyStateText.setVisibility(View.GONE);
            emptyStateIcon.setVisibility(View.GONE);
            payloadRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void animateChipSelection(Chip chip) {
        // Scale animation for chip selection feedback
        chip.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                chip.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start();
            })
            .start();
    }

    @Override
    public void onPayloadSelected(PayloadItem payload, int position) {
        // Update selection
        selectedPayloadId = payload.getId();
        selectedPosition = position;
        
        // Animate selection feedback
        animatePayloadSelection(position);
        
        // Return result to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_PAYLOAD_ID", payload.getId());
        resultIntent.putExtra("SELECTED_PAYLOAD_NAME", payload.getName());
        resultIntent.putExtra("SELECTED_PAYLOAD_CONTENT", payload.getPayloadContent());
        resultIntent.putExtra("SELECTED_PAYLOAD_TYPE", payload.getType());
        setResult(RESULT_OK, resultIntent);
        
        // Delay finish to show animation
        new android.os.Handler().postDelayed(() -> finish(), 300);
    }

    private void animatePayloadSelection(int position) {
        RecyclerView.ViewHolder viewHolder = payloadRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            View itemView = viewHolder.itemView;
            
            // Pulse animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(itemView, "scaleX", 1f, 1.05f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(itemView, "scaleY", 1f, 1.05f, 1f);
            scaleX.setDuration(300);
            scaleY.setDuration(300);
            scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleX.start();
            scaleY.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
