package com.trippin.beertokens.starfield;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bubble {

    private final double speed;
    private float x;
    private float y;
    private final Bitmap image;

    public Bubble(double speed, int x, int y, Bitmap image) {
        super();
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void step() {
        y -= speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
