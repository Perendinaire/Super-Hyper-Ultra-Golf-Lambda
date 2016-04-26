package com.perendinaire.superhyperultragolflambda;

/**
 * V2 Is a 2D point, and can perform operations on points
 */
class V2 {
    public float x, y;
    public V2(float px, float py) { x = px;  y = py; }
    public static V2 add(V2 a, V2 b) { return new V2(a.x + b.x, a.y + b.y); }
    public static V2 subtract(V2 a, V2 b) { return new V2(a.x - b.x, a.y - b.y); }
    public static V2 multiply(V2 a, float s) { return new V2(a.x * s, a.y * s); }
    public static V2 divide(V2 a, float s) { return new V2(a.x / s, a.y / s); }
    public static float dot(V2 a, V2 b) { return a.x * b.x + a.y * b.y; }
    public float length() { return (float)Math.sqrt(x * x + y * y); }
    public float lengthSquared() { return x * x + y * y; }
    public V2 normalize() { return V2.divide(this, this.length()); }
    public V2 negate() { return new V2(-x, -y); }
}
