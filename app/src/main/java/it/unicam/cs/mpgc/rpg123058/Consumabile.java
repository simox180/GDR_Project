package it.unicam.cs.mpgc.rpg123058;

/**
 * Rappresenta un oggetto che fornisce un effetto quando viene utilizzato.
 */
public abstract class Consumabile extends Oggetto {

    /**
     * Crea un consumabile con il nome indicato.
     *
     * @param nome il nome del consumabile
     */
    public Consumabile(String nome){
        super(nome);
    }

    public abstract boolean puoEssereUsato(Giocatore giocatore);

    public abstract void applicaEffetto(Giocatore giocatore);
}
