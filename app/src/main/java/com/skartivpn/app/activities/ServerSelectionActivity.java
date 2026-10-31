package com.skartivpn.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.skartivpn.app.adapter.ServerSelectionAdapter;
import com.skartivpn.app.model.ServerItem;
import com.skarti.premium.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern Server Selection Activity
 * Features:
 * - Search bar with real-time filtering + clear button
 * - Debouncing (300ms) for optimized search
 * - Quick filter chips (All, Asia, Europe, Americas)
 * - RecyclerView with smooth animations
 * - Visual feedback on selection (ripple, highlight, checkmark)
 * - Responsive design for all screen sizes
 */
public class ServerSelectionActivity extends BaseActivity implements ServerSelectionAdapter.OnServerSelectedListener {

    private MaterialToolbar toolbar;
    private TextInputEditText searchInput;
    private ImageView clearSearchButton;
    private ChipGroup filterChipGroup;
    private RecyclerView serverRecyclerView;
    private TextView emptyStateText;
    private ImageView emptyStateIcon;
    private View loadingIndicator;

    private ServerSelectionAdapter adapter;
    private List<ServerItem> allServers;
    private List<ServerItem> filteredServers;
    private String currentFilter = "ALL";
    private String selectedServerId = null;
    private int selectedPosition = -1;
    
    // Debouncing handler
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_selection);

        // Get previously selected server
        Intent intent = getIntent();
        if (intent != null) {
            selectedServerId = intent.getStringExtra("SELECTED_SERVER_ID");
        }

        initViews();
        setupToolbar();
        setupSearchBar();
        setupFilterChips();
        setupRecyclerView();
        loadServers();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_server_selection);
        searchInput = findViewById(R.id.search_input_server);
        clearSearchButton = findViewById(R.id.clear_search_button);
        filterChipGroup = findViewById(R.id.filter_chip_group);
        serverRecyclerView = findViewById(R.id.server_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);
        emptyStateIcon = findViewById(R.id.empty_state_icon);
        loadingIndicator = findViewById(R.id.loading_indicator);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Server");
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
                searchRunnable = () -> filterServers(s.toString());
                
                // Post delayed (300ms debounce)
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips() {
        String[] filters = {"All", "Asia", "Europe", "Americas", "Others"};
        
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
                filterServers(searchInput.getText().toString());
                
                // Animate chip selection
                animateChipSelection(clickedChip);
            });
            
            filterChipGroup.addView(chip);
        }
    }

    private void setupRecyclerView() {
        allServers = new ArrayList<>();
        filteredServers = new ArrayList<>();
        
        adapter = new ServerSelectionAdapter(this, filteredServers, this, selectedServerId);
        serverRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serverRecyclerView.setAdapter(adapter);
        
        // Add item animation
        serverRecyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
    }

    private void loadServers() {
        // Show loading indicator
        showLoading(true);
        
        // Simulate loading delay (remove in production or load from actual data source)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // TODO: Load servers from your data source (SharedPreferences, Database, or API)
            // For now, adding sample data
            allServers.clear();
            
            // Sample servers - replace with your actual data loading logic
            allServers.add(new ServerItem("1", "Singapore - SG1", "Singapore", "Asia", "sg", 25, 35, false, true));
            allServers.add(new ServerItem("2", "Singapore - SG2", "Singapore", "Asia", "sg", 45, 65, false, true));
            allServers.add(new ServerItem("3", "Japan - JP1", "Tokyo", "Asia", "jp", 60, 45, true, true));
            allServers.add(new ServerItem("4", "USA - US1", "New York", "Americas", "us", 120, 78, false, true));
            allServers.add(new ServerItem("5", "Germany - DE1", "Frankfurt", "Europe", "de", 80, 50, true, true));
            allServers.add(new ServerItem("6", "United Kingdom - UK1", "London", "Europe", "gb", 75, 60, false, true));
            allServers.add(new ServerItem("7", "Indonesia - ID1", "Jakarta", "Asia", "id", 15, 25, false, true));
            allServers.add(new ServerItem("8", "Thailand - TH1", "Bangkok", "Asia", "th", 35, 40, false, true));
            allServers.add(new ServerItem("9", "Australia - AU1", "Sydney", "Others", "au", 95, 55, true, true));
            allServers.add(new ServerItem("10", "Canada - CA1", "Toronto", "Americas", "ca", 110, 85, false, false));
            
            // Hide loading and show content
            showLoading(false);
            filterServers("");
        }, 800); // 800ms loading simulation
    }
    
    private void showLoading(boolean show) {
        if (show) {
            loadingIndicator.setVisibility(View.VISIBLE);
            serverRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.GONE);
            emptyStateIcon.setVisibility(View.GONE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            serverRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void filterServers(String query) {
        filteredServers.clear();
        
        for (ServerItem server : allServers) {
            boolean matchesQuery = query.isEmpty() || 
                                   server.getName().toLowerCase().contains(query.toLowerCase()) ||
                                   server.getLocation().toLowerCase().contains(query.toLowerCase());
            
            boolean matchesFilter = currentFilter.equals("ALL") || 
                                    server.getRegion().equalsIgnoreCase(currentFilter);
            
            if (matchesQuery && matchesFilter) {
                filteredServers.add(server);
            }
        }
        
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredServers.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateIcon.setVisibility(View.VISIBLE);
            serverRecyclerView.setVisibility(View.GONE);
            
            // Fade in animation
            emptyStateText.setAlpha(0f);
            emptyStateIcon.setAlpha(0f);
            emptyStateText.animate().alpha(1f).setDuration(300).start();
            emptyStateIcon.animate().alpha(1f).setDuration(300).start();
        } else {
            emptyStateText.setVisibility(View.GONE);
            emptyStateIcon.setVisibility(View.GONE);
            serverRecyclerView.setVisibility(View.VISIBLE);
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
    public void onServerSelected(ServerItem server, int position) {
        // Update selection
        selectedServerId = server.getId();
        selectedPosition = position;
        
        // Animate selection feedback
        animateServerSelection(position);
        
        // Return result to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_SERVER_ID", server.getId());
        resultIntent.putExtra("SELECTED_SERVER_NAME", server.getName());
        resultIntent.putExtra("SELECTED_SERVER_LOCATION", server.getLocation());
        resultIntent.putExtra("SELECTED_SERVER_PING", server.getPing());
        setResult(RESULT_OK, resultIntent);
        
        // Delay finish to show animation
        new android.os.Handler().postDelayed(() -> finish(), 300);
    }

    private void animateServerSelection(int position) {
        RecyclerView.ViewHolder viewHolder = serverRecyclerView.findViewHolderForAdapterPosition(position);
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
