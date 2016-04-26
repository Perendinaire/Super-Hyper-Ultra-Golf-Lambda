package com.perendinaire.superhyperultragolflambda;

/**
 * Actors have the capacity to move around in the world
 */
class Actor extends Circle{
    //position, radius, velocity/delta
    public V2 delta;
    //public final Kind actorKind;

    public Actor(V2 thePosition, float theRadius, V2 theDelta, int theColor) {
        super(thePosition,theRadius, theColor);
        delta = theDelta;
    }
    //moves the actor
    public void move(){
        pos.x += delta.x;
        pos.y += delta.y;
    }
}
