package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo;

import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;

import java.util.Set;

public record OpzioneScena(
        Scelta scelta,
        Set<String> eventiRichiesti,
        Set<String> eventiEsclusi
) {
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

    public boolean isDisponibile(Set<String> eventiCompletati) {
        if(eventiCompletati == null) throw new NullPointerException("Set di eventi nullo");
        if(!(eventiCompletati.containsAll(this.eventiRichiesti))) return false;

        for (String eventoEscluso : eventiEsclusi) {
            if (eventiCompletati.contains(eventoEscluso)) return false;
        }

        return true;
    }
}
