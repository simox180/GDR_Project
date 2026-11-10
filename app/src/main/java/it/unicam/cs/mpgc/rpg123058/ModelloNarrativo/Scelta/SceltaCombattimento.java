package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta;

public record SceltaCombattimento(
        String id,
        String testo,
        String idCombattimento
) implements Scelta {

    public SceltaCombattimento {
        if(id == null || testo == null || idCombattimento == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(id.isBlank() || testo.isBlank() || idCombattimento.isBlank()) throw new IllegalArgumentException("Campi vuoti o contengono solo spazi");
    }
}
