package com.android.settings.rainbow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

public class RainbowPreferenceCategory extends PreferenceCategory {

    public RainbowPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // Find the title text view
        TextView titleView = (TextView) holder.findViewById(android.R.id.title);

        // Replace with RainbowTextView safely
        replaceWithRainbowTextView(titleView);
    }

    /**
     * Replaces the title TextView with a RainbowTextView to apply rainbow effect.
     */
    private static void replaceWithRainbowTextView(TextView titleView) {
        // Check if titleView or its parent is null to avoid crashes
        if (titleView == null || titleView.getParent() == null) {
            return; // Exit safely to prevent crash
        }

        // Check if it's already a RainbowTextView to avoid redundant replacement
        if (!(titleView instanceof RainbowTextView)) {
            Context context = titleView.getContext();
            RainbowTextView rainbowTextView = new RainbowTextView(context);

            // Copy properties from the original TextView
            rainbowTextView.setId(titleView.getId());
            rainbowTextView.setText(titleView.getText());
            rainbowTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            rainbowTextView.setEllipsize(titleView.getEllipsize());
            rainbowTextView.setGravity(titleView.getGravity());
            rainbowTextView.setSingleLine(titleView.isSingleLine());

            // Copy layout parameters
            ViewGroup.LayoutParams lp = titleView.getLayoutParams();
            rainbowTextView.setLayoutParams(lp);

            // Get the parent view safely
            ViewGroup parent = (ViewGroup) titleView.getParent();
            if (parent != null) { // Extra check for safety
                int index = parent.indexOfChild(titleView);
                parent.removeView(titleView);
                parent.addView(rainbowTextView, index);
            }
        }
    }
}
