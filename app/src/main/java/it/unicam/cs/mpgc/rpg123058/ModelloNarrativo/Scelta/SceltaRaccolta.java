package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta;

public record SceltaRaccolta(
        String id,
        String testo,
        String idOggetto
) implements Scelta {

    public SceltaRaccolta{
        if(id == null || testo == null || idOggetto == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(id.isBlank() || testo.isBlank() || idOggetto.isBlank()) throw new IllegalArgumentException("Campi vuoti o contengono solo spazi");
    }
}
