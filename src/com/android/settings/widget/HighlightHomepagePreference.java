/*
 * Copyright (C) 2023-2024 The risingOS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.widget;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HighlightHomepagePreference extends HomepagePreference implements
        HomepagePreferenceLayoutHelper.HomepagePreferenceLayout {

    private static final String TAG = "HighlightHomePref";
    private final HomepagePreferenceLayoutHelper mHelper;
    private Context mContext;
    private static final Random RANDOM = new Random();
    
    // List of available setting keys to choose from
    private static final String[] AVAILABLE_KEYS = {
        "depth_wallpaper_subject_image_uri", "depth_wallpaper_opacity", "depth_wallpaper_offset_x", 
        "statusbar_battery_bar", "statusbar_battery_bar_thickness", "statusbar_battery_bar_style",
        "shake_gestures_enabled", "shake_gestures_action", "shake_gestures_intensity",
        "three_finger_gesture_action", "three_finger_long_press_action", "hardware_keys_disable",
        "theme_style", "notification_sound_vib_screen_on", "alert_slider_notifications",
        "notification_lights", "sensor_block", "status_bar_icons", "status_bar_clock",
        "double_tap_sleep_gesture", "status_bar_brightness_control", "qs_quick_pulldown",
        "rising_changelog", "custom_aod_image_enabled", "lockscreen_custom_image",
        "monet_engine", "android.theme.customization.navbar", "settings_theme_style",
        "charging_animation", "screen_off_animation", "adaptive_playback_timeout",
        "gaming_mode", "gestures", "navigation", "security", "sound_engine", "pulse_settings"
    };
    
    // List of available drawable resources to use
    private static final int[] AVAILABLE_DRAWABLES = {
        R.drawable.ic_custom_settings_wallpaper_white,
        R.drawable.ic_custom_wireless,
        R.drawable.ic_custom_devices,
        android.R.drawable.ic_menu_preferences,
        android.R.drawable.ic_menu_manage,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_share,
        android.R.drawable.ic_menu_help,
        android.R.drawable.ic_menu_info_details
    };

    public HighlightHomepagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mHelper = new HomepagePreferenceLayoutHelper(this);
        mContext = context;
        init();
    }

    public HighlightHomepagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new HomepagePreferenceLayoutHelper(this);
        mContext = context;
        init();
    }

    public HighlightHomepagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new HomepagePreferenceLayoutHelper(this);
        mContext = context;
        init();
    }

    public HighlightHomepagePreference(Context context) {
        super(context);
        mHelper = new HomepagePreferenceLayoutHelper(this);
        mContext = context;
        init();
    }

    private void init() {
        setLayoutResource(R.layout.top_level_preference_highlight_card);
    }
    
    // Generate a random list of highlight cards based on available settings
    private List<HighlightCard> generateRandomHighlightCards(int count) {
        List<String> keys = new ArrayList<>(Arrays.asList(AVAILABLE_KEYS));
        Collections.shuffle(keys);
        
        List<HighlightCard> cards = new ArrayList<>();
        for (int i = 0; i < Math.min(count, keys.size()); i++) {
            String key = keys.get(i);
            String formattedTitle = formatKeyToTitle(key);
            String summary = "Customize your " + formattedTitle.toLowerCase() + " settings";
            int iconResId = AVAILABLE_DRAWABLES[RANDOM.nextInt(AVAILABLE_DRAWABLES.length)];
            
            cards.add(new HighlightCard(formattedTitle, summary, iconResId, key));
        }
        
        return cards;
    }
    
    // Format a settings key into a readable title
    private String formatKeyToTitle(String key) {
        // Remove any prefixes like "android.theme.customization."
        String processedKey = key;
        if (key.contains(".")) {
            processedKey = key.substring(key.lastIndexOf(".") + 1);
        }
        
        // Replace underscores with spaces and capitalize each word
        String[] words = processedKey.split("_");
        StringBuilder title = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                title.append(Character.toUpperCase(word.charAt(0)))
                     .append(word.substring(1))
                     .append(" ");
            }
        }
        
        return title.toString().trim();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        ViewPager2 viewPager = holder.itemView.findViewById(R.id.card_holders);
        
        // Generate 3-5 random highlight cards
        int cardCount = RANDOM.nextInt(3) + 3; // Between 3 and 5 cards
        List<HighlightCard> highlightCards = generateRandomHighlightCards(cardCount);
        
        HighlightCardAdapter adapter = new HighlightCardAdapter(highlightCards);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(cardCount);
        viewPager.setPageTransformer(new MarginPageTransformer((int) mContext.getResources().getDimension(R.dimen.page_margin)));
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.beginFakeDrag();
                viewPager.fakeDragBy(-10f);
                viewPager.endFakeDrag();
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.beginFakeDrag();
                        viewPager.fakeDragBy(10f);
                        viewPager.endFakeDrag();
                    }
                });
            }
        });
    }

    @Override
    public HomepagePreferenceLayoutHelper getHelper() {
        return mHelper;
    }

    private static class HighlightCard {
        private final String title;
        private final String summary;
        private final int iconResId;
        private final String settingKey;

        public HighlightCard(String title, String summary, int iconResId, String settingKey) {
            this.title = title;
            this.summary = summary;
            this.iconResId = iconResId;
            this.settingKey = settingKey;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public int getIconResId() {
            return iconResId;
        }
        
        public String getSettingKey() {
            return settingKey;
        }
    }

    private static class HighlightCardAdapter extends RecyclerView.Adapter<HighlightCardAdapter.HighlightCardViewHolder> {

        private final List<HighlightCard> mHighlightCards;
        private static final int[] HIGHLIGHT_CARD_STYLES = {
                R.layout.highlight_card_default,
                R.layout.highlight_card,
                R.layout.highlight_card_material,
                R.layout.highlight_card_oos,
                R.layout.highlight_card_colorful
        };

        public HighlightCardAdapter(List<HighlightCard> highlightCards) {
            mHighlightCards = highlightCards;
        }

        @NonNull
        @Override
        public HighlightCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int theme = Settings.System.getInt(context.getContentResolver(), "settings_theme_style", 0);
            View view = LayoutInflater.from(context).inflate(HIGHLIGHT_CARD_STYLES[theme], parent, false);
            return new HighlightCardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HighlightCardViewHolder holder, int position) {
            HighlightCard card = mHighlightCards.get(position);
            holder.title.setText(card.getTitle());
            holder.summary.setText(card.getSummary());
            holder.icon.setImageResource(card.getIconResId());
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.itemView.setLayoutParams(layoutParams);
            
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    try {
                        // Create a general intent for settings and put the setting key as an extra
                        Intent intent = new Intent();
                        intent.setClassName("com.android.settings", 
                            "com.android.settings.Settings$SystemDashboardActivity");
                        intent.putExtra("setting_key", card.getSettingKey());
                        
                        // Determine specific intent based on the setting key
                        String key = card.getSettingKey();
                        if (key.contains("wallpaper") || key.contains("theme") || key.contains("style")) {
                            intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$DisplaySettingsActivity");
                        } else if (key.contains("battery") || key.contains("charging")) {
                            intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$PowerUsageSummaryActivity");
                        } else if (key.contains("gesture") || key.contains("navigation")) {
                            intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$GestureSettingsActivity");
                        } else if (key.contains("sound") || key.contains("vibrate")) {
                            intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$SoundSettingsActivity");
                        } else if (key.contains("security") || key.contains("lockscreen")) {
                            intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$SecurityDashboardActivity");
                        } else if (key.contains("statusbar") || key.contains("notification")) {
                            intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$StatusBarSettingsActivity");
                        }
                        
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to start activity for setting: " + card.getSettingKey(), e);
                        // Fallback to system settings
                        Intent fallbackIntent = new Intent(Settings.ACTION_SETTINGS);
                        context.startActivity(fallbackIntent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mHighlightCards.size();
        }

        static class HighlightCardViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView summary;
            ImageView icon;

            HighlightCardViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.title);
                summary = itemView.findViewById(android.R.id.summary);
                icon = itemView.findViewById(android.R.id.icon);
            }
        }
    }
}
