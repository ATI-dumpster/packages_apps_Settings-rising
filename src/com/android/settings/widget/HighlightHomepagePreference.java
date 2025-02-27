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
import java.util.Map;
import java.util.HashMap;

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
        "three_finger_gesture_action", "three_finger_long_press_action",
        "theme_style", "notification_sound_vib_screen_on", "alert_slider_notifications",
        "notification_lights", "status_bar_icons", "status_bar_clock",
        "double_tap_sleep_gesture", "status_bar_brightness_control", "qs_quick_pulldown",
        "rising_changelog", "custom_aod_image_enabled", "lockscreen_custom_image",
        "monet_engine", "android.theme.customization.navbar",
        "screen_off_animation", "adaptive_playback_timeout",
        "gestures", "navigation", "security", "sound_engine", "pulse_settings"
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

    // Mapping setting keys to their corresponding activities
    private static final Map<String, String> settingToActivityMap = new HashMap<String, String>() {{
        put("depth_wallpaper_subject_image_uri", "com.rising.settings.fragments.WallpaperDepth");
        put("depth_wallpaper_opacity", "com.rising.settings.fragments.WallpaperDepth");
        put("depth_wallpaper_offset_x", "com.rising.settings.fragments.WallpaperDepth");
        put("statusbar_battery_bar", "com.rising.settings.fragments.statusbar.BatteryBar");
        put("statusbar_battery_bar_thickness", "com.rising.settings.fragments.statusbar.BatteryBar");
        put("statusbar_battery_bar_style", "com.rising.settings.fragments.statusbar.BatteryBar");
        put("shake_gestures_enabled", "com.rising.settings.fragments.Gestures");
        put("shake_gestures_action", "com.rising.settings.fragments.Gestures");
        put("shake_gestures_intensity", "com.rising.settings.fragments.Gestures");
        put("three_finger_gesture_action", "com.rising.settings.fragments.Gestures");
        put("three_finger_long_press_action", "com.rising.settings.fragments.Gestures");
        put("theme_style", "com.rising.settings.fragments.Themes");
        put("notification_sound_vib_screen_on", "com.rising.settings.fragments.sound.SoundEngine");
        put("alert_slider_notifications", "com.rising.settings.fragments.sound.SoundEngine");
        put("notification_lights", "com.rising.settings.fragments.Notifications");
        put("status_bar_icons", "com.rising.settings.fragments.statusbar.Clock");
        put("status_bar_clock", "com.rising.settings.fragments.statusbar.Clock");
        put("double_tap_sleep_gesture", "com.rising.settings.fragments.Gestures");
        put("status_bar_brightness_control", "com.rising.settings.fragments.Themes");
        put("qs_quick_pulldown", "com.rising.settings.fragments.Themes");
        put("rising_changelog", "com.rising.settings.fragments.about.ChangelogActivity");
        put("custom_aod_image_enabled", "com.rising.settings.fragments.lockscreen.doze.AODSettings");
        put("lockscreen_custom_image", "com.rising.settings.fragments.lockscreen.doze.AODSettings");
        put("monet_engine", "com.rising.settings.fragments.MonetSettings");
        put("android.theme.customization.navbar", "com.rising.settings.fragments.ui.NavbarStyles");
        put("screen_off_animation", "com.rising.settings.fragments.Themes");
        put("adaptive_playback_timeout", "com.rising.settings.fragments.Themes");
        put("gestures", "com.rising.settings.fragments.Gestures");
        put("navigation", "com.rising.settings.fragments.Navigation");
        put("security", "com.rising.settings.fragments.about.ChangelogActivity");
        put("sound_engine", "com.rising.settings.fragments.ui.NavbarStyles");
        put("pulse_settings", "com.rising.settings.fragments.ui.NavbarStyles");
    }};

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
        String processedKey = key;
        if (key.contains(".")) {
            processedKey = key.substring(key.lastIndexOf(".") + 1);
        }

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

        public HighlightCardAdapter(List<HighlightCard> highlightCards) {
            mHighlightCards = highlightCards;
        }

        @NonNull
        @Override
        public HighlightCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.highlight_card_default, parent, false);
            return new HighlightCardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HighlightCardViewHolder holder, int position) {
            HighlightCard card = mHighlightCards.get(position);
            holder.title.setText(card.getTitle());
            holder.summary.setText(card.getSummary());
            holder.icon.setImageResource(card.getIconResId());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    try {
                        // Get activity class for this setting key
                        String activity = settingToActivityMap.get(card.getSettingKey());
                        if (activity != null) {
                            Intent intent = new Intent();
                            intent.setClassName(context.getPackageName(), activity);
                            context.startActivity(intent);
                        } else {
                            // If no mapping found, fall back to the main settings
                            Intent fallbackIntent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(fallbackIntent);
                        }
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
