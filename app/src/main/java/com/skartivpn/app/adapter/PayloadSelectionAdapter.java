package com.skartivpn.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skartivpn.app.model.PayloadItem;
import com.skarti.premium.v2.R;

import java.util.List;

/**
 * Modern Payload Selection Adapter with animations and visual feedback
 */
public class PayloadSelectionAdapter extends RecyclerView.Adapter<PayloadSelectionAdapter.PayloadViewHolder> {

    private Context context;
    private List<PayloadItem> payloads;
    private OnPayloadSelectedListener listener;
    private String selectedPayloadId;
    private int lastPosition = -1;

    public interface OnPayloadSelectedListener {
        void onPayloadSelected(PayloadItem payload, int position);
    }

    public PayloadSelectionAdapter(Context context, List<PayloadItem> payloads, OnPayloadSelectedListener listener, String selectedPayloadId) {
        this.context = context;
        this.payloads = payloads;
        this.listener = listener;
        this.selectedPayloadId = selectedPayloadId;
    }

    @NonNull
    @Override
    public PayloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payload_selection, parent, false);
        return new PayloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayloadViewHolder holder, int position) {
        PayloadItem payload = payloads.get(position);
        
        // Set payload data
        holder.payloadName.setText(payload.getName());
        holder.payloadDescription.setText(payload.getDescription());
        holder.payloadType.setText(payload.getType());
        
        // Set type icon and color
        holder.typeIcon.setImageResource(payload.getTypeIcon());
        int typeColor = Color.parseColor(payload.getTypeColor());
        holder.typeIcon.setColorFilter(typeColor);
        holder.typeIndicatorBar.setBackgroundColor(typeColor);
        holder.payloadType.setTextColor(typeColor);
        
        // Set active/inactive status
        if (payload.isActive()) {
            holder.statusBadge.setVisibility(View.VISIBLE);
            holder.statusBadge.setText("Active");
            holder.statusBadge.setBackgroundResource(R.drawable.badge_active);
            holder.itemView.setAlpha(1f);
        } else {
            holder.statusBadge.setVisibility(View.VISIBLE);
            holder.statusBadge.setText("Inactive");
            holder.statusBadge.setBackgroundResource(R.drawable.badge_inactive);
            holder.itemView.setAlpha(0.6f);
        }
        
        // Show favorite icon if favorited
        if (payload.isFavorite()) {
            holder.favoriteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.favoriteIcon.setVisibility(View.GONE);
        }
        
        // Handle selection state
        boolean isSelected = payload.getId().equals(selectedPayloadId);
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.sktgruy));
            holder.cardView.setCardElevation(8f);
            holder.checkMark.setVisibility(View.VISIBLE);
            holder.selectionBorder.setVisibility(View.VISIBLE);
            holder.selectionGlow.setVisibility(View.VISIBLE);
            
            // Glow effect for selected item
            holder.cardView.setAlpha(1f);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.cardView.setCardElevation(2f);
            holder.checkMark.setVisibility(View.GONE);
            holder.selectionBorder.setVisibility(View.GONE);
            holder.selectionGlow.setVisibility(View.GONE);
            holder.cardView.setAlpha(0.95f);
        }
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (payload.isActive()) {
                // Update selection
                String previousSelected = selectedPayloadId;
                selectedPayloadId = payload.getId();
                
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
                    listener.onPayloadSelected(payload, holder.getAdapterPosition());
                }
            } else {
                // Show inactive message
                android.widget.Toast.makeText(context, "This payload is currently inactive", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        // Long click to show payload content
        holder.itemView.setOnLongClickListener(v -> {
            if (!payload.getPayloadContent().isEmpty()) {
                showPayloadContentDialog(payload);
            }
            return true;
        });
        
        // Animate items as they appear
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return payloads.size();
    }

    /**
     * Find position of payload by ID
     */
    private int findPositionById(String id) {
        for (int i = 0; i < payloads.size(); i++) {
            if (payloads.get(i).getId().equals(id)) {
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
     * Show payload content in dialog
     */
    private void showPayloadContentDialog(PayloadItem payload) {
        new android.app.AlertDialog.Builder(context)
            .setTitle(payload.getName())
            .setMessage(payload.getPayloadContent())
            .setPositiveButton("OK", null)
            .show();
    }

    /**
     * ViewHolder class
     */
    static class PayloadViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View selectionBorder;
        View selectionGlow;
        View typeIndicatorBar;
        ImageView typeIcon;
        ImageView checkMark;
        ImageView favoriteIcon;
        TextView payloadName;
        TextView payloadDescription;
        TextView payloadType;
        TextView statusBadge;

        public PayloadViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.payload_card_view);
            selectionBorder = itemView.findViewById(R.id.selection_border_payload);
            selectionGlow = itemView.findViewById(R.id.selection_glow);
            typeIndicatorBar = itemView.findViewById(R.id.type_indicator_bar);
            typeIcon = itemView.findViewById(R.id.type_icon);
            checkMark = itemView.findViewById(R.id.check_mark_payload);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            payloadName = itemView.findViewById(R.id.payload_name);
            payloadDescription = itemView.findViewById(R.id.payload_description);
            payloadType = itemView.findViewById(R.id.payload_type);
            statusBadge = itemView.findViewById(R.id.status_badge);
        }
    }

    /**
     * Update selected payload
     */
    public void updateSelection(String payloadId) {
        this.selectedPayloadId = payloadId;
        notifyDataSetChanged();
    }
}
