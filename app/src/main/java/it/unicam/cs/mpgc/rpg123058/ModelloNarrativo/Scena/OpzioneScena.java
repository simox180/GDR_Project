package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena;

import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;

import java.util.Set;

public record OpzioneScena(
        Scelta scelta,
        Set<String> eventiRichiesti,
        Set<String> eventiEsclusi
) {
    /**
     * Valida le condizioni della scelta e copia i due insiemi. Le condizioni non possono contenere identificativi blank né eventi presenti in entrambi gli insiemi.
     * @param scelta scelta associata, non nulla
     * @param eventiRichiesti eventi tutti necessari, non nulli
     * @param eventiEsclusi eventi che impediscono la scelta, non nulli
     * @throws NullPointerException se un argomento o un elemento degli insiemi è nullo
     * @throws IllegalArgumentException se un identificativo è blank o compare in entrambi gli insiemi
     */
    public OpzioneScena {
        if(scelta == null || eventiRichiesti == null || eventiEsclusi == null) throw new NullPointerException("pArametri nulli non ammessi");

        eventiRichiesti = Set.copyOf(eventiRichiesti);
        eventiEsclusi = Set.copyOf(eventiEsclusi);

        for (String evento : eventiRichiesti) {
            if (evento.isBlank()) {
                throw new IllegalArgumentException(
                        "Gli identificativi degli eventi richiesti non possono essere vuoti");
            }

            if (eventiEsclusi.contains(evento)) throw new IllegalArgumentException("Un evento non può essere sia richiesto sia escluso: " + evento);
        }

        for (String evento : eventiEsclusi) {
            if (evento.isBlank()) throw new IllegalArgumentException("Gli identificativi degli eventi esclusi non possono essere vuoti");
        }
    }

    /**
     * Verifica le condizioni senza modificare gli eventi.
     * @param eventiCompletati progressi da consultare, non nulli
     * @return {@code true} se tutti i richiesti sono presenti e nessun escluso è presente; {@code false} altrimenti
     * @throws NullPointerException se l’insieme è nullo
     */
    public boolean isDisponibile(Set<String> eventiCompletati) {
        if(eventiCompletati == null) throw new NullPointerException("Set di eventi nullo");
        if(!(eventiCompletati.containsAll(this.eventiRichiesti))) return false;

        for (String eventoEscluso : eventiEsclusi) {
            if (eventiCompletati.contains(eventoEscluso)) return false;
        }

        return true;
    }
}
