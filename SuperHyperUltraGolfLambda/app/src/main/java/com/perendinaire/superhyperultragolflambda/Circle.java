package com.perendinaire.superhyperultragolflambda;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Circles are defined by their position and radius, and collide as circles
 */
class Circle extends Collider {
    protected V2 pos;      // The middle
    protected final float rad;   // The radius

    ////////////////// PUBLIC METHODS ////////////////////////////////////

    // Constructor;
    public Circle(V2 middle, float radius, int theColor) {
        pos = middle;
        rad = radius;
        defaultColor = theColor;
    }

    // Reflection of a ball against the circle - updates 'direction'
    @Override public V2 reflectBall(Actor ball) {
        V2 centre = ball.pos;
        V2 direction = ball.delta;
        float radius = ball.rad;
        if (!intersectBall(centre, radius)) {
            float deltax, deltay, lensq, bb, cc, det, lambda;
            deltax = pos.x - centre.x;
            deltay = pos.y - centre.y;
            lensq = (direction.x * direction.x + direction.y * direction.y);
            bb = (direction.x * deltax + direction.y * deltay) / lensq;
            cc = (deltax * deltax + deltay * deltay - (rad + radius) * (rad + radius)) / lensq;
            det = bb * bb - cc;
            if (det >= 0) {
                lambda = bb - (float) Math.sqrt(det);
                if (lambda >= 0 && lambda < 1) {
                    V2 hit = V2.add(centre, V2.multiply(direction, lambda));
                    V2 normal = V2.subtract(hit, pos).normalize();
                    //moving objects gain speed upon bouncing
                    //direction.x = newdir.x;
                    //direction.y = newdir.y;
                    //Return a new V2, rather than setting setting the object's V2 here
                    return V2.subtract(direction, V2.multiply(V2.multiply(normal, V2.dot(direction, normal)), 3.5f));
                }
            }
        }
        //if we did not collide, return the original direction
        return direction;
    }

    // Does a ball intersect with the circle
    @Override public boolean intersectBall(V2 centre, float radius) {
        return (V2.subtract(centre, pos).lengthSquared() < (rad + radius) * (rad + radius));
    }

    // Draw the circle
    @Override public void draw(Canvas c, Paint p) {
        c.drawCircle(pos.x, pos.y, rad, p);
    }
}
