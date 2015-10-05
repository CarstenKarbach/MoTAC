package de.karbach.tac.core;

import java.io.Serializable;

/**
 * Stores a card and a possible color, which played it.
 * The color can be left blank as it is not always known (e.g. for trickser and tac).
 */
public class Move implements Serializable{

    /**
     * The played card
     */
    protected Card card;

    /**
     * The ID of the ball, which was moved or made the move
     */
    protected int ballID;

    /**
     * The ID of this move
     */
    protected int id;

    /**
     * Used for ID generation
     */
    protected static int idcounter=0;

    /**
     * Create empty move
     */
    public Move(){
        this(null, -1);
    }

    /**
     * Create a move entry.
     * @param c the played card or null if unknown
     * @param ballID the players ball or -1 if unknown
     */
    public Move(Card c, int ballID){
        setCard(c);
        setBallID(ballID);
        idcounter++;
        setId(idcounter);
    }

    /**
     * Call this function on game restart.
     */
    public static void resetIDCounter(){
        idcounter=0;
    }

    /**
     * Set the ID for this move in the game
     * @param id the new ID for this move
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return the ID of the move in the game
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param c the played card
     */
    public void setCard(Card c){
        this.card=c;
    }

    /**
     *
     * @return the played card or null, if none was played
     */
    public Card getCard() {
        return card;
    }

    /**
     *
     * @return the ID of the player ball or -1 if unknown
     */
    public int getBallID() {
        return ballID;
    }

    public void setBallID(int ballID) {
        this.ballID = ballID;
    }
}
