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
                        // General intent for settings
                        Intent intent = new Intent();
                        String key = card.getSettingKey();

                        // Match setting key to the correct activity
                        switch (key) {
                            case "depth_wallpaper_subject_image_uri":
                            case "depth_wallpaper_opacity":
                            case "depth_wallpaper_offset_x":
                            case "theme_style":
                            case "monet_engine":
                            case "android.theme.customization.navbar":
                            case "settings_theme_style":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$DisplaySettingsActivity");
                                break;

                            case "statusbar_battery_bar":
                            case "statusbar_battery_bar_thickness":
                            case "statusbar_battery_bar_style":
                            case "charging_animation":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$PowerUsageSummaryActivity");
                                break;

                            case "shake_gestures_enabled":
                            case "shake_gestures_action":
                            case "shake_gestures_intensity":
                            case "three_finger_gesture_action":
                            case "three_finger_long_press_action":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$GestureSettingsActivity");
                                break;

                            case "sound_engine":
                            case "notification_sound_vib_screen_on":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$SoundSettingsActivity");
                                break;

                            case "security":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$SecurityDashboardActivity");
                                break;

                            case "status_bar_icons":
                            case "status_bar_clock":
                            case "double_tap_sleep_gesture":
                            case "status_bar_brightness_control":
                            case "qs_quick_pulldown":
                            case "rising_changelog":
                            case "notification_lights":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$StatusBarSettingsActivity");
                                break;

                            case "gaming_mode":
                            case "gestures":
                            case "navigation":
                                intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$GamingModeSettingsActivity");
                                break;

                            default:
                                // Default to System Settings if no match
                                intent.setAction(Settings.ACTION_SETTINGS);
                                break;
                        }

                        // Start the activity
                        context.startActivity(intent);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to start activity for setting: " + card.getSettingKey(), e);
                        // Fallback to system settings if the specific intent fails
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
