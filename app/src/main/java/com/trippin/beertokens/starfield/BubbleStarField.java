package com.trippin.beertokens.starfield;

import android.content.Context;
import android.graphics.Canvas;

public class BubbleStarField {

    private final Bubble[] bubbles = new Bubble[30];
    private BubbleFactory bubbleFactory;
    private int width;
    private int height;

    public BubbleStarField(Context context) {

        this.width = context.getResources().getDisplayMetrics().widthPixels;
        this.height = context.getResources().getDisplayMetrics().heightPixels;
        this.bubbleFactory = new BubbleFactory(width, height, context.getResources());

        for (int i = 0; i < bubbles.length; i++) {
            bubbles[i] = bubbleFactory.createBubble(true);
        }
    }

    public void step() {

        for (Bubble bubble : bubbles) {
            bubble.step();
        }
    }

    public void draw(Canvas canvas) {

        for (int x = 0; x < bubbles.length; x++) {
            Bubble beer = bubbles[x];
            if (isOutOfBounds(beer))
                bubbles[x] = bubbleFactory.createBubble(false);
            else
                beer.draw(canvas);
        }
    }

    private boolean isOutOfBounds(Bubble bubble) {

        if (bubble.getX() < 0 || bubble.getX() >= width)
            return true;

        if (bubble.getY() < 0 || bubble.getY() >= height)
            return true;

        return false;
    }
}
