/*
 * Copyright (C) 2023-2024 Afterlife Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import androidx.transition.AutoTransition;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class afterlifeMaintainerController extends BasePreferenceController implements View.OnTouchListener {
    private static final String TAG = "afterlifeMaintainerController";
    
    private final Context mContext;
    private String mGitLink;
    private String mTeleLink;
    private String mFbLink;
    private String mIgLink;
    
    // View references
    private TextView mMaintainerName;
    private TextView mDeviceModel;
    private TextView mMaintainerExpand;
    private ImageView mBtnExpanded;
    private LinearLayout mDetail;
    private FrameLayout mBaseLayout;
    private LinearLayout mGithub;
    private LinearLayout mTelegram;
    private LinearLayout mInstagram;

    public afterlifeMaintainerController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        try {
            super.displayPreference(screen);
            if (screen == null) {
                Log.e(TAG, "PreferenceScreen is null");
                return;
            }

            LayoutPreference preference = screen.findPreference(getPreferenceKey());
            if (preference == null) {
                Log.e(TAG, "LayoutPreference not found for key: " + getPreferenceKey());
                return;
            }

            initializeViews(preference);
            setupDeviceInfo();
            setupExpandableLayout();
            setupSocialLinks();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in displayPreference", e);
        }
    }

    private void initializeViews(LayoutPreference preference) {
        // Initialize all view references
        mMaintainerName = findViewById(preference, "maintainer_name");
        mDeviceModel = findViewById(preference, "device_model");
        mMaintainerExpand = findViewById(preference, "maintainer_name_expanded");
        mBtnExpanded = findViewById(preference, "expand_button");
        mDetail = findViewById(preference, "hidden_view");
        mBaseLayout = findViewById(preference, "base_layout");
        mGithub = findViewById(preference, "github");
        mTelegram = findViewById(preference, "telegram");
        mInstagram = findViewById(preference, "instagram");
    }

    private <T extends View> T findViewById(LayoutPreference preference, String id) {
        return preference.findViewById(
            mContext.getResources().getIdentifier(id, "id", mContext.getPackageName())
        );
    }

    private void setupDeviceInfo() {
        if (mDeviceModel != null) {
            mDeviceModel.setText(String.format("Device : %s", getPhoneModel()));
        }
        
        if (mMaintainerName != null) {
            setInfo("ro.afterlife.maintainer", mMaintainerName);
        }
        
        if (mMaintainerExpand != null) {
            setInfo("ro.afterlife.maintainer", mMaintainerExpand);
        }
    }

    private void setupExpandableLayout() {
        if (mBaseLayout == null || mBtnExpanded == null || mDetail == null) {
            Log.e(TAG, "Required views for expandable layout are null");
            return;
        }

        mBaseLayout.setClickable(true);
        mBaseLayout.setOnClickListener(new View.OnClickListener() {
            private AnimatedVectorDrawable avd;
            private AnimatedVectorDrawable avdClicked;
            private boolean isClicked = false;

            @Override
            public void onClick(View v) {
                handleExpandClick(isClicked);
                isClicked = !isClicked;
            }
        });
    }

    private void handleExpandClick(boolean isClicked) {
        try {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) mContext.getDrawable(
                mContext.getResources().getIdentifier(
                    isClicked ? "ic_collapse" : "ic_expand",
                    "drawable",
                    mContext.getPackageName()
                )
            );

            if (drawable != null) {
                setupAnimationCallback(drawable);
                mBtnExpanded.setImageDrawable(drawable);
                drawable.start();
                handleTransition(isClicked);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in handleExpandClick", e);
        }
    }

    private void setupAnimationCallback(AnimatedVectorDrawable drawable) {
        drawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
            }

            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
            }
        });
    }

    private void handleTransition(boolean isExpanded) {
        Transition transition = new AutoTransition();
        transition.setDuration(600);
        transition.addTarget(mBaseLayout);
        TransitionManager.beginDelayedTransition(mBaseLayout, transition);

        if (isExpanded) {
            collapseView();
        } else {
            expandView();
        }
    }

    private void collapseView() {
        if (mDetail.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mDetail, new Slide(Gravity.TOP).setDuration(200));
            mDetail.animate()
                .translationYBy(-mDetail.getHeight())
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mDetail.setVisibility(View.GONE);
                        if (mMaintainerName != null) {
                            mMaintainerName.setVisibility(View.VISIBLE);
                        }
                        mDetail.setTranslationY(0);
                    }
                });
        }
    }

    private void expandView() {
        mDetail.setVisibility(View.VISIBLE);
        if (mMaintainerName != null) {
            mMaintainerName.setVisibility(View.INVISIBLE);
        }
    }

    private void setupSocialLinks() {
        initializeSocialLinks();
        setupSocialClickListeners();
    }

    private void initializeSocialLinks() {
        mGitLink = getStringResource("maintainer_git_username");
        mFbLink = getStringResource("maintainer_fb_username");
        mTeleLink = getStringResource("maintainer_tele_username");
        mIgLink = getStringResource("maintainer_ig_username");
    }

    private String getStringResource(String resourceName) {
        try {
            int resourceId = mContext.getResources().getIdentifier(
                resourceName,
                "string",
                mContext.getPackageName()
            );
            return mContext.getString(resourceId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting string resource: " + resourceName, e);
            return "";
        }
    }

    private void setupSocialClickListeners() {
        setupSocialClickListener(mGithub, "https://github.com/" + mGitLink);
        setupSocialClickListener(mTelegram, "https://t.me/" + mTeleLink);
        setupSocialClickListener(mInstagram, "https://instagram.com/" + mIgLink);
    }

    private void setupSocialClickListener(View view, final String url) {
        if (view != null) {
            view.setOnClickListener(v -> openUrl(url));
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Error opening URL: " + url, e);
        }
    }

    private static void setInfo(String prop, TextView textView) {
        if (textView == null) return;
        
        String propertyValue = getSystemProperty(prop);
        textView.setText(TextUtils.isEmpty(propertyValue) ? "Unknown" : propertyValue);
    }

    private static String getSystemProperty(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties")
                .getMethod("get", String.class)
                .invoke(null, key);
        } catch (Exception e) {
            Log.e(TAG, "Error getting system property: " + key, e);
            return null;
        }
    }

    private String getPhoneModel() {
        return Build.MODEL;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
