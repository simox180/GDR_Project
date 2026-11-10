package it.unicam.cs.mpgc.rpg123058.Dominio;

import java.util.Objects;

public class ConsumabileCurativo extends Consumabile {

    private final int puntiCura;

    /**
     * Crea un consumabile con una cura positiva.
     * @param nome nome non nullo né blank
     * @param puntiCura punti di cura positivi
     * @throws IllegalArgumentException se i punti cura non sono positivi
     */
    public ConsumabileCurativo(String nome, int puntiCura){
        super(nome);

        if(puntiCura <= 0) throw new IllegalArgumentException("I punti cura devono essere positivi");

        this.puntiCura = puntiCura;
    }

    @Override
    /**
     * Controlla se al giocatore manca salute.
     * @param giocatore giocatore non nullo
     * @return {@code true} se la salute non è al massimo, {@code false} altrimenti
     * @throws NullPointerException se il giocatore è nullo
     */
    public boolean puoEssereUsato(Giocatore giocatore) {
        Objects.requireNonNull(giocatore, "Giocatore passato nullo");

        return !giocatore.isSaluteAlMassimo();
    }

    @Override
    /**
     * Cura il giocatore fino alla salute massima.
     * @param giocatore destinatario non nullo
     * @throws NullPointerException se il giocatore è nullo
     */
    public void applicaEffetto(Giocatore giocatore) {
        Objects.requireNonNull(giocatore, "Giocatore passato nullo");

        giocatore.cura(this.puntiCura);
    }

    /**
     * Restituisce i punti cura associati al consumabile.
     *
     * @return i punti cura forniti dall'oggetto
     */
    public int getPuntiCura() {
        return puntiCura;
    }
}
