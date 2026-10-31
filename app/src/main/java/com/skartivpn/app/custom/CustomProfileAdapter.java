package com.skartivpn.app.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skarti.premium.v2.R;

import java.util.List;

public class CustomProfileAdapter extends RecyclerView.Adapter<CustomProfileAdapter.ProfileViewHolder> {

    public interface OnProfileActionListener {
        void onProfileClicked(CustomProfile profile);
        void onEditClicked(CustomProfile profile);
        void onDeleteClicked(CustomProfile profile);
    }

    private Context context;
    private List<CustomProfile> profiles;
    private OnProfileActionListener listener;
    private String activeId;

    public CustomProfileAdapter(Context context, List<CustomProfile> profiles,
                                OnProfileActionListener listener, String activeId) {
        this.context = context;
        this.profiles = profiles;
        this.listener = listener;
        this.activeId = activeId;
    }

    public void update(List<CustomProfile> profiles, String activeId) {
        this.profiles = profiles;
        this.activeId = activeId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        final CustomProfile profile = profiles.get(position);
        holder.name.setText(profile.name);
        holder.summary.setText(profile.summary());
        holder.mode.setText(profile.modeLabel());
        boolean isActive = activeId != null && activeId.equals(profile.id);
        holder.activeBar.setVisibility(isActive ? View.VISIBLE : View.GONE);
        if (profile.needsSni() && profile.sni != null && !profile.sni.isEmpty()) {
            holder.sni.setText("SNI: " + profile.sni);
            holder.sni.setVisibility(View.VISIBLE);
        } else {
            holder.sni.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onProfileClicked(profile);
                }
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClicked(profile);
                }
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClicked(profile);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles == null ? 0 : profiles.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView summary;
        TextView mode;
        TextView sni;
        View activeBar;
        TextView editButton;
        TextView deleteButton;

        ProfileViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.profile_icon);
            name = (TextView) itemView.findViewById(R.id.profile_name);
            summary = (TextView) itemView.findViewById(R.id.profile_summary);
            mode = (TextView) itemView.findViewById(R.id.profile_mode);
            sni = (TextView) itemView.findViewById(R.id.profile_sni);
            activeBar = itemView.findViewById(R.id.profile_active_bar);
            editButton = (TextView) itemView.findViewById(R.id.btn_edit_profile);
            deleteButton = (TextView) itemView.findViewById(R.id.btn_delete_profile);
        }
    }
}
