package com.perendinaire.superhyperultragolflambda;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Rectangles are defined by their corners and perpendiculars, and collide as rectangles
 */
class Rectangle extends Collider {

    private final V2 a, b, c, d;          // The corners
    private final V2 pab, pbd, pdc, pca;  // Perpendiculars to the sides

    ///////////////////// PRIVATE HELPER METHODS ////////////////////////

    // Calculate reflection on one edge, returns
    private V2 reflectLine(V2 centre, V2 direction, float radius, V2 p, V2 q, V2 ppq) {
        if (!inside(centre, radius, p, q, ppq)) {
            float divisor, gamma, lambda;
            V2 pp, qq;
            pp = V2.add(p, V2.multiply(ppq, radius));
            qq = V2.add(q, V2.multiply(ppq, radius));
            divisor = direction.y * (pp.x - qq.x) - direction.x * (pp.y - qq.y);
            if (divisor != 0) {
                gamma = (direction.y * (pp.x - centre.x) - direction.x * (pp.y - centre.y)) / divisor;
                if (gamma >= 0 && gamma <= 1) {
                    lambda = ((pp.y - centre.y) * (pp.x - qq.x) - (pp.x- centre.x) * (pp.y - qq.y)) / divisor;
                    if (lambda > 0 && lambda <= 1) {
                        //centre.x += lambda * direction.x;
                        //centre.y += lambda * direction.y;
                        //Return a new V2, rather than setting setting the object's V2 here
                        return V2.subtract(direction, V2.multiply(V2.multiply(ppq, V2.dot(direction, ppq)), 2));
                    }
                }
            }
        }
        // If we did not collide, return the original direction
        return direction;
    }

    // Calculate reflection on a curved corner
    private V2 reflectCurve(V2 centre, V2 direction, float radius, V2 p, V2 q, V2 r, V2 ppq, V2 prp) {
        if (V2.subtract(centre, p).length() >= radius) {
            // The ball is outside the circle(radius) about p
            float deltax, deltay, lensq, bb, cc, det, lambda;
            deltax = p.x - centre.x;
            deltay = p.y - centre.y;
            lensq = (direction.x * direction.x + direction.y * direction.y);
            bb = (direction.x * deltax + direction.y * deltay) / lensq;
            cc = (deltax * deltax + deltay * deltay - radius * radius) / lensq;
            det = bb * bb - cc;
            if (det >= 0) {
                lambda = bb - (float) Math.sqrt(det);
                if (lambda >= 0 && lambda <= 1) {
                    V2 hit = V2.add(centre, V2.multiply(direction, lambda));
                    if (!inside(hit, 0, p, q, ppq) && !inside(hit, 0, r, p, prp)) {
                        V2 normal = V2.subtract(hit, p).normalize();
                        //direction.x = newdir.x;
                        //direction.y = newdir.y;
                        return V2.subtract(direction, V2.multiply(V2.multiply(normal, V2.dot(direction, normal)), 1.5f));
                    }
                }
            }
        }
        //if we did not collide, return the original direction
        return direction;
    }

    // Is a ball of radius r 'inside' with respect to one edge
    private boolean inside(V2 x, float r, V2 p, V2 q, V2 ppq) {
        return V2.dot(ppq, V2.subtract(x, V2.add(p, V2.multiply(ppq, r)))) < 0;
    }

    ////////////////// PUBLIC METHODS ////////////////////////////////////

    // Constructor
    public Rectangle(V2 corner, float width, float height, int theColor) {
        a = corner;
        b = V2.add(a, new V2(width, 0));
        c = V2.add(a, new V2(0, height));
        d = V2.add(a, new V2(width, height));
        pab = V2.subtract(a, c).normalize();
        pbd = V2.subtract(b, a).normalize();
        pdc = pab.negate();
        pca = pbd.negate();
        defaultColor = theColor;
    }

    // Reflection of a ball against the rectangle - gives a new direction
    @Override public V2 reflectBall(final Actor ball) {
        //try to reflect against each "collision edge" and collision corner
        V2 newDir;
        //if the new direction has changed, we intersected that object, return
        if ((newDir = reflectLine(ball.pos, ball.delta, ball.rad, a, b, pab)) != ball.delta) return newDir;
        if ((newDir = reflectLine(ball.pos, ball.delta, ball.rad, b, d, pbd)) != ball.delta) return newDir;
        if ((newDir = reflectLine(ball.pos, ball.delta, ball.rad, d, c, pdc)) != ball.delta) return newDir;
        if ((newDir = reflectLine(ball.pos, ball.delta, ball.rad, c, a, pca)) != ball.delta) return newDir;
        if ((newDir = reflectCurve(ball.pos, ball.delta, ball.rad, b, d, a, pbd, pab)) != ball.delta) return newDir;
        if ((newDir = reflectCurve(ball.pos, ball.delta, ball.rad, d, c, b, pdc, pbd)) != ball.delta) return newDir;
        if ((newDir = reflectCurve(ball.pos, ball.delta, ball.rad, c, a, d, pdc, pca)) != ball.delta) return newDir;
        if ((newDir = reflectCurve(ball.pos, ball.delta, ball.rad, a, b, c, pab, pca)) != ball.delta) return newDir;
        //if we did not reflect at all, return old direction
        return ball.delta;
    }

    // Does a ball intersect with the rectangle
    @Override public boolean intersectBall(V2 centre, float radius) {
        if (inside(centre, radius, a, b, pab)
                && inside(centre, radius, b, d, pbd)
                && inside(centre, radius, d, c, pdc)
                && inside(centre, radius, c, a, pca)) {
            if (!inside(centre, 0, a, b, pab)) {
                if (!inside(centre, 0, c, a, pca)) {
                    if (V2.subtract(centre, a).length() >= radius)
                        return false;
                } else if (!inside(centre, 0, b, d, pbd)) {
                    if (V2.subtract(centre, b).length() >= radius)
                        return false;
                }
            } else if (!inside(centre, 0, d, c, pdc)) {
                if (!inside(centre, 0, c, a, pca)) {
                    if (V2.subtract(centre, c).length() >= radius)
                        return false;
                } else if (!inside(centre, 0, b, d, pbd)) {
                    if (V2.subtract(centre, d).length() >= radius)
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    // Draw the rectangle
    @Override public void draw(Canvas c, Paint p) {
        c.drawRect(a.x, a.y, d.x, d.y, p);
    }
}
