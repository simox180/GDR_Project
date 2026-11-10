package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena;

import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scena {
    private final String id;
    private final String titolo;
    private final String descrizione;
    private final List<OpzioneScena> opzioni;

    public Scena(String id, String titolo, String descrizione, List<OpzioneScena> opzioni) {
        if(id == null || titolo == null || descrizione == null || opzioni == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(id.isBlank() || titolo.isBlank() || descrizione.isBlank()) throw new IllegalArgumentException("Stringe vuote o contenenti solo spazi non ammesse");

        this.opzioni = List.copyOf(opzioni);

        Set<String> idScelte = new HashSet<>();

        for (OpzioneScena opzione : opzioni) {
            String idScelta = opzione.scelta().id();

            if (!idScelte.add(idScelta)) throw new IllegalArgumentException("ID di scelta duplicato nella scena: " + idScelta);
        }

        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    public List<Scelta> getScelteDisponibili(Set<String> eventiCompletati) {
        if(eventiCompletati == null) throw new NullPointerException("Set nullo non ammesso");

        List<Scelta> scelteDisponibili = new ArrayList<>();

        // seleziona solo le scelte disponibili secondo gli eventi completati
        for(OpzioneScena opzione : opzioni){
            if(opzione.isDisponibile(eventiCompletati)) scelteDisponibili.add(opzione.scelta());
        }

        return List.copyOf(scelteDisponibili);
    }

    public String getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
