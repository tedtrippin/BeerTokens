package com.trippin.beertokens.starfield;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.trippin.beertokens.R;

/**
 *
 * @author robert.haycock
 *
 */
public class BubbleFactory {

    private final int screenWidth;
    private final int screenHeight;
    private Bitmap bubble1;
    private Bitmap bubble2;
    private Bitmap bubble3;

    public BubbleFactory(int screenWidth, int screenHeight, Resources resources) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        bubble1 = BitmapFactory.decodeResource(resources, R.drawable.bubble1);
        bubble2 = BitmapFactory.decodeResource(resources, R.drawable.bubble2);
        bubble3 = BitmapFactory.decodeResource(resources, R.drawable.bubble3);
    }

    public Bubble createBubble(boolean randomY) {

        int x = (int)(Math.random() * screenWidth);
        int y = randomY ? (int)(Math.random() * screenHeight) : screenHeight;

        double d = Math.random();
        if (d > 0.66) {
            return new Bubble(1, x, y, bubble1);
        } else if (d > 0.33) {
            return new Bubble(2, x, y, bubble2);
        } else {
            return new Bubble(3, x, y, bubble3);
        }
    }
}
