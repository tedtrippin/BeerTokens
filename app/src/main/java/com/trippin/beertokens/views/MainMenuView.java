package com.trippin.beertokens.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.trippin.beertokens.starfield.BubbleStarField;

import java.util.Timer;
import java.util.TimerTask;

public class MainMenuView extends View {

    private final static int ANIMATION_PERIOD_MS = 10;

    private BubbleStarField bubbleStarField;
    private Timer animationTimer;

    public MainMenuView(final Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

        super.onWindowFocusChanged(hasWindowFocus);

        if (hasWindowFocus) {
            bubbleStarField = new BubbleStarField(this.getContext());
            animationTimer = new Timer();
            animationTimer.scheduleAtFixedRate(new StepTask(), 0, ANIMATION_PERIOD_MS);
        } else {
            animationTimer.cancel();
            bubbleStarField = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (bubbleStarField != null) // Need to verify if its ever null at this point
            bubbleStarField.draw(canvas);
    }

    private class StepTask extends TimerTask {

        @Override
        public void run() {
            bubbleStarField.step();
            postInvalidate();
        }
    }
}