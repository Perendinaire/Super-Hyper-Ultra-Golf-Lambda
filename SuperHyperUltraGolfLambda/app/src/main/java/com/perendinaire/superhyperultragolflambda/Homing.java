package com.perendinaire.superhyperultragolflambda;

/**
 * Homing objects constantly move towards the player, on top of trying to kill them
 */
class Homing extends Enemy {
    public Homing(V2 thePosition, int theRadius, V2 theDelta, int theColor, float theHomingSpeed) {
        super(thePosition, theRadius, theDelta, theColor, theHomingSpeed);
    }

    /**
     * Move the homing object towards the player, and determine if it is hitting them
     */
    @Override
    public boolean enemyMove(Actor thePlayer){
        if(thePlayer.pos.x > pos.x) {
            delta.x = enemyBaseSpeed;
        }
        else if(thePlayer.pos.x < pos.x) {
            delta.x = -enemyBaseSpeed;
        }
        if(thePlayer.pos.y > pos.y) {
            delta.y = enemyBaseSpeed;
        }
        else if(thePlayer.pos.y < pos.y) {
            delta.y = -enemyBaseSpeed;
        }
        return super.enemyMove(thePlayer);
    }
}
