package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta;

public record SceltaNavigazione(
        String id,
        String testo,
        String idScenaDestinazione
) implements Scelta {

    public SceltaNavigazione {
        if(id == null || testo == null || idScenaDestinazione == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(id.isBlank() || testo.isBlank() || idScenaDestinazione.isBlank()) throw new IllegalArgumentException("Campi vuoti o contengono solo spazi");
    }
}
