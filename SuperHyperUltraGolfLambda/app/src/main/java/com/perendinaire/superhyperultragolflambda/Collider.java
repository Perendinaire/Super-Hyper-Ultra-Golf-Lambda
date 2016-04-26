package com.perendinaire.superhyperultragolflambda;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Collider objects have the capacity to collide with each other
 */
abstract class Collider {
    public int defaultColor;
    public abstract V2 reflectBall(Actor ball);
    public abstract boolean intersectBall(V2 centre, float radius);
    public abstract void draw(Canvas c, Paint p);
}
