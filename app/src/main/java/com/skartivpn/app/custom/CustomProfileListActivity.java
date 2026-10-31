package com.skartivpn.app.custom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skartivpn.app.activities.BaseActivity;
import com.skarti.premium.v2.R;

import java.util.List;

public class CustomProfileListActivity extends BaseActivity implements CustomProfileAdapter.OnProfileActionListener {

    private RecyclerView recyclerView;
    private View emptyState;
    private CustomProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_profile_list);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar_custom_list);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.custom_recycler_view);
        emptyState = findViewById(R.id.empty_state_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_custom);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomProfileListActivity.this, CustomProfileEditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<CustomProfile> profiles = CustomProfileStore.load(this);
        CustomProfile active = CustomProfileStore.getActive(this);
        String activeId = active == null ? "" : active.id;
        if (adapter == null) {
            adapter = new CustomProfileAdapter(this, profiles, this, activeId);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.update(profiles, activeId);
        }
        emptyState.setVisibility(profiles.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(profiles.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onProfileClicked(CustomProfile profile) {
        CustomProfileStore.setActive(this, profile.id);
        Toast.makeText(this, "Profil aktif: " + profile.name, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onEditClicked(CustomProfile profile) {
        Intent intent = new Intent(this, CustomProfileEditorActivity.class);
        intent.putExtra("PROFILE_ID", profile.id);
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(final CustomProfile profile) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Profil")
                .setMessage("Hapus profil " + profile.name + "?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomProfileStore.delete(CustomProfileListActivity.this, profile.id);
                        refresh();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
