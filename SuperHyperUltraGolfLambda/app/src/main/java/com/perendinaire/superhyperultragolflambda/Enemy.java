package com.perendinaire.superhyperultragolflambda;

/**
 * Enemies will kill the player if they touch them
 */
class Enemy extends Actor {
    final float enemyBaseSpeed;

    public Enemy(V2 thePosition, int theRadius, V2 theDelta, int theColor, float theEnemySpeed) {
        super(thePosition, theRadius, theDelta, theColor);
        enemyBaseSpeed = theEnemySpeed;
    }

    /**
     * Determine if the enemy is hitting the player
     */
    public boolean enemyMove(Actor thePlayer){
        return intersectBall(thePlayer.pos, thePlayer.rad);
    }
}
