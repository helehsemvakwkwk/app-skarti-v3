package com.skartivpn.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skartivpn.app.model.ServerItem;
import com.skarti.premium.v2.R;

import java.util.List;

/**
 * Modern Server Selection Adapter with animations and visual feedback
 */
public class ServerSelectionAdapter extends RecyclerView.Adapter<ServerSelectionAdapter.ServerViewHolder> {

    private Context context;
    private List<ServerItem> servers;
    private OnServerSelectedListener listener;
    private String selectedServerId;
    private int lastPosition = -1;

    public interface OnServerSelectedListener {
        void onServerSelected(ServerItem server, int position);
    }

    public ServerSelectionAdapter(Context context, List<ServerItem> servers, OnServerSelectedListener listener, String selectedServerId) {
        this.context = context;
        this.servers = servers;
        this.listener = listener;
        this.selectedServerId = selectedServerId;
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_server_selection, parent, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        ServerItem server = servers.get(position);
        
        // Set server data
        holder.serverName.setText(server.getName());
        holder.serverLocation.setText(server.getLocation());
        holder.serverPing.setText(server.getPing() + " ms");
        
        // Set ping color based on quality
        int pingColor = Color.parseColor(server.getPingColor());
        holder.serverPing.setTextColor(pingColor);
        holder.pingIndicator.setColorFilter(pingColor);
        
        // Set load indicator
        int loadPercentage = server.getLoad();
        holder.serverLoadBar.setProgress(loadPercentage);
        holder.serverLoadText.setText(loadPercentage + "%");
        
        // Set load color based on percentage
        int loadColor = Color.parseColor(server.getLoadColor());
        holder.serverLoadBar.setProgressTintList(android.content.res.ColorStateList.valueOf(loadColor));
        holder.serverLoadText.setTextColor(loadColor);
        
        // Set online/offline status
        if (server.isOnline()) {
            holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.green));
            holder.serverStatus.setText("Online");
            holder.serverStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.red));
            holder.serverStatus.setText("Offline");
            holder.serverStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.itemView.setAlpha(0.5f);
        }
        
        // Show premium badge if premium server
        if (server.isPremium()) {
            holder.premiumBadge.setVisibility(View.VISIBLE);
        } else {
            holder.premiumBadge.setVisibility(View.GONE);
        }
        
        // Handle selection state
        boolean isSelected = server.getId().equals(selectedServerId);
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.sktgruy));
            holder.cardView.setCardElevation(8f);
            holder.checkMark.setVisibility(View.VISIBLE);
            holder.selectionBorder.setVisibility(View.VISIBLE);
            
            // Glow effect for selected item
            holder.cardView.setAlpha(1f);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.cardView.setCardElevation(2f);
            holder.checkMark.setVisibility(View.GONE);
            holder.selectionBorder.setVisibility(View.GONE);
            holder.cardView.setAlpha(0.95f);
        }
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (server.isOnline()) {
                // Update selection
                String previousSelected = selectedServerId;
                selectedServerId = server.getId();
                
                // Notify previous and current selection changed
                notifyItemChanged(holder.getAdapterPosition());
                if (previousSelected != null) {
                    int previousPosition = findPositionById(previousSelected);
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition);
                    }
                }
                
                // Ripple effect animation
                animateRipple(holder.itemView);
                
                // Callback
                if (listener != null) {
                    listener.onServerSelected(server, holder.getAdapterPosition());
                }
            } else {
                // Show offline message
                android.widget.Toast.makeText(context, "This server is currently offline", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        // Animate items as they appear
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    /**
     * Find position of server by ID
     */
    private int findPositionById(String id) {
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Animate items as they appear in RecyclerView
     */
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    /**
     * Ripple effect animation on item click
     */
    private void animateRipple(View view) {
        Animation ripple = AnimationUtils.loadAnimation(context, R.anim.ripple_effect);
        view.startAnimation(ripple);
    }

    /**
     * ViewHolder class
     */
    static class ServerViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View selectionBorder;
        ImageView serverIcon;
        ImageView statusIndicator;
        ImageView pingIndicator;
        ImageView checkMark;
        ImageView premiumBadge;
        TextView serverName;
        TextView serverLocation;
        TextView serverPing;
        TextView serverStatus;
        ProgressBar serverLoadBar;
        TextView serverLoadText;

        public ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.server_card_view);
            selectionBorder = itemView.findViewById(R.id.selection_border);
            serverIcon = itemView.findViewById(R.id.server_icon);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            pingIndicator = itemView.findViewById(R.id.ping_indicator);
            checkMark = itemView.findViewById(R.id.check_mark);
            premiumBadge = itemView.findViewById(R.id.premium_badge);
            serverName = itemView.findViewById(R.id.server_name);
            serverLocation = itemView.findViewById(R.id.server_location);
            serverPing = itemView.findViewById(R.id.server_ping);
            serverStatus = itemView.findViewById(R.id.server_status);
            serverLoadBar = itemView.findViewById(R.id.server_load_bar);
            serverLoadText = itemView.findViewById(R.id.server_load_text);
        }
    }

    /**
     * Update selected server
     */
    public void updateSelection(String serverId) {
        this.selectedServerId = serverId;
        notifyDataSetChanged();
    }
}
