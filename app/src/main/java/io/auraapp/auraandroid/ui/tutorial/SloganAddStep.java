package io.auraapp.auraandroid.ui.tutorial;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.auraapp.auraandroid.R;
import io.auraapp.auraandroid.ui.ScreenPager;

public class SloganAddStep extends ProfileStatusHidingStep {

    public SloganAddStep(ViewGroup mRootView, Context mContext, ScreenPager mPager) {
        super(mRootView, mContext, mPager);
    }

    public ViewGroup enter() {
        doEnter();

        ViewGroup screen = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tutorial_slogan_add, mRootView, false);

        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) screen.findViewById(R.id.tutorial_overlay).getLayoutParams();

        layoutParams.height = getRelativeTop(R.id.profile_add_slogan)
                - ((ViewGroup.MarginLayoutParams) mRootView.findViewById(R.id.profile_add_slogan).getLayoutParams()).bottomMargin;

        return screen;
    }

    public void leave() {
        doLeave();
    }

    @Override
    public Class<? extends TutorialStep> getPrevious() {
        return TextStep.class;
    }

    @Override
    public Class<? extends TutorialStep> getNextStep() {
        return SloganEditStep.class;
    }


}