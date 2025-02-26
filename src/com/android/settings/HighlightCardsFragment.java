package com.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HighlightCardsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "HighlightCardsFragment";
    private static final String KEY_HIGHLIGHT_CARD_DISPLAY = "highlight_card_display";
    private static final String KEY_HIGHLIGHT_CARDS_ENABLED = "highlight_cards_enabled";
    private static final String KEY_HIGHLIGHT_CARDS_REFRESH_INTERVAL = "highlight_cards_refresh_interval";
    private static final String PREF_KEY_LAST_UPDATED_TIMESTAMP = "highlight_cards_last_updated";
    private static final String PREF_KEY_CURRENT_CARD_ID = "highlight_cards_current_id";

    private HighlightCardPreference mCardPreference;
    private SharedPreferences mPreferences;
    private List<HighlightCard> mCards = new ArrayList<>();
    private Random mRandom = new Random();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.highlight_cards);
        
        // Initialize preferences
        mPreferences = getContext().getSharedPreferences(
                getString(R.string.highlight_cards_prefs_key), Context.MODE_PRIVATE);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        
        // Get the card preference
        mCardPreference = findPreference(KEY_HIGHLIGHT_CARD_DISPLAY);
        
        // Load cards data
        loadCardsData();
        
        // Set up UI components
        setupPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHighlightCard();
    }

    @Override
    public void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (KEY_HIGHLIGHT_CARDS_ENABLED.equals(key) || 
            KEY_HIGHLIGHT_CARDS_REFRESH_INTERVAL.equals(key)) {
            updateHighlightCard();
        }
    }

    private void setupPreferences() {
        SwitchPreference enabledPref = findPreference(KEY_HIGHLIGHT_CARDS_ENABLED);
        ListPreference intervalPref = findPreference(KEY_HIGHLIGHT_CARDS_REFRESH_INTERVAL);
        
        // Initialize UI state based on preferences
        boolean isEnabled = mPreferences.getBoolean(KEY_HIGHLIGHT_CARDS_ENABLED, true);
        mCardPreference.setVisible(isEnabled);
    }

    private void loadCardsData() {
        try {
            XmlPullParser parser = getResources().getXml(R.xml.highlight_cards_data);
            
            int eventType = parser.getEventType();
            HighlightCard currentCard = null;
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "card".equals(parser.getName())) {
                    currentCard = new HighlightCard();
                    
                    // Parse card attributes
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attrName = parser.getAttributeName(i);
                        String attrValue = parser.getAttributeValue(i);
                        
                        switch (attrName) {
                            case "id":
                                currentCard.id = attrValue;
                                break;
                            case "title":
                                currentCard.titleResId = getResourceId(attrValue);
                                break;
                            case "summary":
                                currentCard.summaryResId = getResourceId(attrValue);
                                break;
                            case "icon":
                                currentCard.iconResId = getResourceId(attrValue);
                                break;
                            case "action_title":
                                currentCard.actionTitleResId = getResourceId(attrValue);
                                break;
                            case "action_intent":
                                currentCard.actionIntent = attrValue;
                                break;
                            case "background":
                                currentCard.backgroundResId = getResourceId(attrValue);
                                break;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && "card".equals(parser.getName())) {
                    mCards.add(currentCard);
                    currentCard = null;
                }
                
                eventType = parser.next();
            }
            
            Log.d(TAG, "Loaded " + mCards.size() + " highlight cards");
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "Error loading highlight cards data", e);
        }
    }

    private int getResourceId(String resourceString) {
        if (resourceString.startsWith("@")) {
            String name = resourceString.substring(resourceString.indexOf("/") + 1);
            String type = resourceString.substring(1, resourceString.indexOf("/"));
            return getResources().getIdentifier(name, type, getContext().getPackageName());
        }
        return 0;
    }

    private void updateHighlightCard() {
        if (!isAdded() || mCards.isEmpty()) {
            return;
        }
        
        boolean isEnabled = mPreferences.getBoolean(KEY_HIGHLIGHT_CARDS_ENABLED, true);
        mCardPreference.setVisible(isEnabled);
        
        if (!isEnabled) {
            return;
        }
        
        String refreshInterval = mPreferences.getString(
                KEY_HIGHLIGHT_CARDS_REFRESH_INTERVAL, "daily");
        long lastUpdated = mPreferences.getLong(PREF_KEY_LAST_UPDATED_TIMESTAMP, 0);
        String currentCardId = mPreferences.getString(PREF_KEY_CURRENT_CARD_ID, null);
        
        boolean shouldRefresh = shouldRefreshCard(refreshInterval, lastUpdated);
        
        if (shouldRefresh || currentCardId == null) {
            // Select a random card, different from the current one if possible
            HighlightCard selectedCard = selectRandomCard(currentCardId);
            
            if (selectedCard != null) {
                // Update the preference
                mCardPreference.setTitle(getString(selectedCard.titleResId));
                mCardPreference.setSummary(getString(selectedCard.summaryResId));
                mCardPreference.setIcon(getContext().getDrawable(selectedCard.iconResId));
                mCardPreference.setActionText(getString(selectedCard.actionTitleResId));
                mCardPreference.setActionIntent(selectedCard.actionIntent);
                mCardPreference.setCardBackground(selectedCard.backgroundResId);
                
                // Save current state
                mPreferences.edit()
                        .putLong(PREF_KEY_LAST_UPDATED_TIMESTAMP, System.currentTimeMillis())
                        .putString(PREF_KEY_CURRENT_CARD_ID, selectedCard.id)
                        .apply();
            }
        }
    }

    private boolean shouldRefreshCard(String interval, long lastUpdated) {
        if ("always".equals(interval)) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastUpdated;
        
        switch (interval) {
            case "daily":
                return timeDiff > TimeUnit.DAYS.toMillis(1);
            case "weekly":
                return timeDiff > TimeUnit.DAYS.toMillis(7);
            default:
                return false;
        }
    }

    private HighlightCard selectRandomCard(String currentCardId) {
        if (mCards.size() == 1) {
            return mCards.get(0);
        }
        
        // Create a list of cards excluding the current one
        List<HighlightCard> availableCards = new ArrayList<>(mCards);
        if (currentCardId != null) {
            availableCards.removeIf(card -> card.id.equals(currentCardId));
        }
        
        // If all cards were removed, fall back to the full list
        if (availableCards.isEmpty()) {
            availableCards = mCards;
        }
        
        // Select a random card
        int randomIndex = mRandom.nextInt(availableCards.size());
        return availableCards.get(randomIndex);
    }

    /**
     * Class representing a highlight card from XML
     */
    private static class HighlightCard {
        String id;
        int titleResId;
        int summaryResId;
        int iconResId;
        int actionTitleResId;
        String actionIntent;
        int backgroundResId;
    }
}
