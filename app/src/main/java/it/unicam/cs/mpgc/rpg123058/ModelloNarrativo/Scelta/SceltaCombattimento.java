package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta;

public record SceltaCombattimento(
        String id,
        String testo,
        String idCombattimento
) implements Scelta {

    /**
     * Valida una scelta che avvia un incontro.
     * @param id identificativo non vuoto
     * @param testo testo non vuoto
     * @param idCombattimento identificativo non vuoto dell’incontro
     * @throws NullPointerException se un campo è nullo
     * @throws IllegalArgumentException se un campo è blank
     */
    public SceltaCombattimento {
        if(id == null || testo == null || idCombattimento == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(id.isBlank() || testo.isBlank() || idCombattimento.isBlank()) throw new IllegalArgumentException("Campi vuoti o contengono solo spazi");
    }
}
