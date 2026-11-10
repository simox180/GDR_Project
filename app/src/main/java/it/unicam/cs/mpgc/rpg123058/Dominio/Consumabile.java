package it.unicam.cs.mpgc.rpg123058.Dominio;

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

    /**
     * Verifica se l’effetto può essere applicato al giocatore.
     * @param giocatore destinatario da valutare
     * @return {@code true} se l’uso è consentito
     */
    public abstract boolean puoEssereUsato(Giocatore giocatore);

    /**
     * Applica l’effetto al giocatore.
     * @param giocatore destinatario dell’effetto
     */
    public abstract void applicaEffetto(Giocatore giocatore);
}
