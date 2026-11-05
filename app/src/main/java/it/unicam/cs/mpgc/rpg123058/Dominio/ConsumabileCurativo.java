package it.unicam.cs.mpgc.rpg123058.Dominio;

import java.util.Objects;

public class ConsumabileCurativo extends Consumabile {

    private final int puntiCura;

    public ConsumabileCurativo(String nome, int puntiCura){
        super(nome);

        if(puntiCura <= 0) throw new IllegalArgumentException("I punti cura devono essere positivi");

        this.puntiCura = puntiCura;
    }

    @Override
    public boolean puoEssereUsato(Giocatore giocatore) {
        Objects.requireNonNull(giocatore, "Giocatore passato nullo");

        return !giocatore.isSaluteAlMassimo();
    }

    @Override
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
