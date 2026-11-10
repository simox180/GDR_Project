package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta;

public record SceltaNavigazione(
        String id,
        String testo,
        String idScenaDestinazione
) implements Scelta {

    /**
     * Valida una scelta che conduce a una scena.
     * @param id identificativo non vuoto
     * @param testo testo non vuoto
     * @param idScenaDestinazione identificativo non vuoto della destinazione
     * @throws NullPointerException se un campo è nullo
     * @throws IllegalArgumentException se un campo è blank
     */
    public SceltaNavigazione {
        if(id == null || testo == null || idScenaDestinazione == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(id.isBlank() || testo.isBlank() || idScenaDestinazione.isBlank()) throw new IllegalArgumentException("Campi vuoti o contengono solo spazi");
    }
}
