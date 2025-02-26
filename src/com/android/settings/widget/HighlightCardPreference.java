package com.android.settings.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;

/**
 * A custom preference that displays a highlight card with an icon, title, summary, and action button.
 */
public class HighlightCardPreference extends Preference {

    private String mTitle;
    private String mSummary;
    private int mIconResId;
    private String mActionText;
    private String mActionIntent;
    private int mBackgroundResId;

    public HighlightCardPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.highlight_card_layout);
    }

    public HighlightCardPreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        
        final TextView titleView = (TextView) holder.findViewById(R.id.highlight_card_title);
        final TextView summaryView = (TextView) holder.findViewById(R.id.highlight_card_summary);
        final ImageView iconView = (ImageView) holder.findViewById(R.id.highlight_card_icon);
        final Button actionButton = (Button) holder.findViewById(R.id.highlight_card_action);
        final View cardBackground = holder.findViewById(R.id.highlight_card_background);
        
        // Set card content
        if (titleView != null && mTitle != null) {
            titleView.setText(mTitle);
        }
        
        if (summaryView != null && mSummary != null) {
            summaryView.setText(mSummary);
        }
        
        if (iconView != null && mIconResId != 0) {
            iconView.setImageResource(mIconResId);
        }
        
        if (actionButton != null && mActionText != null) {
            actionButton.setText(mActionText);
            actionButton.setOnClickListener(v -> {
                if (mActionIntent != null) {
                    try {
                        Intent intent = Intent.parseUri(mActionIntent, 0);
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        // Handle error
                    }
                }
            });
        }
        
        if (cardBackground != null && mBackgroundResId != 0) {
            cardBackground.setBackgroundResource(mBackgroundResId);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
        notifyChanged();
    }

    public void setSummary(String summary) {
        mSummary = summary;
        notifyChanged();
    }

    public void setIcon(int iconResId) {
        mIconResId = iconResId;
        notifyChanged();
    }

    public void setActionText(String actionText) {
        mActionText = actionText;
        notifyChanged();
    }

    public void setActionIntent(String actionIntent) {
        mActionIntent = actionIntent;
        notifyChanged();
    }

    public void setCardBackground(int backgroundResId) {
        mBackgroundResId = backgroundResId;
        notifyChanged();
    }
}
