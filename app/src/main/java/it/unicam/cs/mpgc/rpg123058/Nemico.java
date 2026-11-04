package it.unicam.cs.mpgc.rpg123058;

/**
 * Rappresenta un personaggio avversario associato a un oggetto di bottino.
 */
public class Nemico extends Personaggio {

    private Oggetto bottino;

    /**
     * Crea un nemico con le caratteristiche e il bottino specificati.
     *
     * @param nome il nome del nemico
     * @param saluteMassima la salute massima iniziale
     * @param attaccoBase il valore di attacco base
     * @param difesa il valore di difesa base
     * @param bottino l'oggetto associato al nemico come bottino
     */
    public Nemico(String nome, int saluteMassima, int attaccoBase, int difesa, Oggetto bottino){
        super(nome, saluteMassima, attaccoBase, difesa);
        this.bottino = bottino;
    }

    /**
     * Restituisce il bottino associato al nemico.
     *
     * @return l'oggetto bottino
     */
    public Oggetto getBottino() {
        return bottino;
    }
}
