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

public class afterlifeMaintainerController extends BasePreferenceController
implements View.OnTouchListener {

    Context mContext;
    String mIgLink, mTeleLink, mFbLink, mGitLink;

    public afterlifeMaintainerController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }


    // system prop
    public static String getSystemProperty(String key) {
        String value = null;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    // device name
    private static void setInfo(String prop, TextView textview) {
        if (TextUtils.isEmpty(getSystemProperty(prop))) {
            textview.setText("Unknown");
        } else {
            textview.setText(getSystemProperty(prop));
        }

    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        LayoutPreference mPreference = screen.findPreference(getPreferenceKey());

        final TextView mMaintainerName = mPreference.findViewById(mContext.getResources().
                                                                      getIdentifier("id/maintainer_name", null, mContext.getPackageName()));
        final TextView mDeviceModel = mPreference.findViewById(mContext.getResources().
                                                                   getIdentifier("id/device_model", null, mContext.getPackageName()));
        mDeviceModel.setText("Device : " + getPhoneModel());
        setInfo("ro.afterlife.maintainer", mMaintainerName);
        final ImageView mBtnExpanded = mPreference.findViewById(mContext.getResources().
                                                                    getIdentifier("id/expand_button", null, mContext.getPackageName()));
        final LinearLayout mDetail = mPreference.findViewById(mContext.getResources().
                                                                  getIdentifier("id/hidden_view", null, mContext.getPackageName()));      
        final FrameLayout mBaseLayout = mPreference.findViewById(mContext.getResources().
                                                                     getIdentifier("id/base_layout", null, mContext.getPackageName()));
        mBaseLayout.setClickable(true);

        mBaseLayout.setOnClickListener(new View.OnClickListener()
            {
                private AnimatedVectorDrawable avd, avdClicked;
                private Boolean isClicked = false;
                @Override
                public void onClick(View v) {
                    avdClicked =  (AnimatedVectorDrawable)mContext.getDrawable(mContext.getResources().
                                                                              getIdentifier("drawable/ic_collapse", null, mContext.getPackageName()));
                    avd = (AnimatedVectorDrawable)mContext.getDrawable(mContext.getResources().
                                                                      getIdentifier("drawable/ic_expand", null, mContext.getPackageName()));
                    // isClicked = true;
                    //avd2.start();

                    AnimatedVectorDrawable drawable = isClicked ? avdClicked : avd;
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
                    isClicked = !isClicked;
                    mBtnExpanded.setImageDrawable(drawable);
                    drawable.start();
                    Transition transition = new AutoTransition();
                    transition.setDuration(600);
                    transition.addTarget(mBaseLayout);
                    TransitionManager.beginDelayedTransition(mBaseLayout, transition);
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
                                    mMaintainerName.setVisibility(View.VISIBLE);
                                    mDetail.setTranslationY(0);
                                }
                            });
                    } else {
                        mDetail.setVisibility(View.VISIBLE);
                        mMaintainerName.setVisibility(View.INVISIBLE);
                    }}
            });

        final TextView mMaintainerExpand = mPreference.findViewById(mContext.getResources().
                                                                        getIdentifier("id/maintainer_name_expanded", null, mContext.getPackageName()));
        setInfo("ro.afterlife.maintainer", mMaintainerExpand);


        LinearLayout mGithub = mPreference.findViewById(mContext.getResources().
                                                            getIdentifier("id/github", null, mContext.getPackageName()));
        LinearLayout mTelegram = mPreference.findViewById(mContext.getResources().
                                                              getIdentifier("id/telegram", null, mContext.getPackageName()));
        LinearLayout mFacebook = mPreference.findViewById(mContext.getResources().
                                                              getIdentifier("id/facebook", null, mContext.getPackageName()));
        LinearLayout mInstagram = mPreference.findViewById(mContext.getResources().
                                                               getIdentifier("id/instagram", null, mContext.getPackageName()));

        mGitLink = mContext.getString(mContext.getResources().getIdentifier("string/maintainer_git_username",null, mContext.getPackageName()));
        mFbLink = mContext.getString(mContext.getResources().getIdentifier("string/maintainer_fb_username",null, mContext.getPackageName()));
        mTeleLink = mContext.getString(mContext.getResources().getIdentifier("string/maintainer_tele_username",null, mContext.getPackageName()));
        mIgLink = mContext.getString(mContext.getResources().getIdentifier("string/maintainer_ig_username",null, mContext.getPackageName()));

        mGithub.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                   Uri.parse("https://github.com/"+ mGitLink));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }}
            });


        mTelegram.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                   Uri.parse("https://t.me/"+ mTeleLink));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }}
            });

        mFacebook.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                   Uri.parse("https://www.facebook.com/"+mFbLink));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }}
            });

        mInstagram.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                   Uri.parse("https://instagram.com/"+ mIgLink));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }}
            });


    }

    private String getPhoneModel() {
        return Build.MODEL;
    }

    public int getResources(String res) {
        return mContext.getResources().getIdentifier(res, null, mContext.getPackageName());
    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        return true;
    }

}
