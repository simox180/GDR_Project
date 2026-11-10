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

    /**
     * Crea una scena copiando le opzioni nell’ordine ricevuto. Una lista vuota è ammessa.
     * @param id identificativo non blank
     * @param titolo titolo non blank
     * @param descrizione descrizione non blank
     * @param opzioni opzioni non nulle con identificativi di scelta distinti
     * @throws NullPointerException se un argomento o un’opzione è nullo
     * @throws IllegalArgumentException se una stringa è blank o un identificativo di scelta è duplicato
     */
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

    /**
     * Filtra le opzioni nell’ordine originale, senza cambiare scena o progressi.
     * @param eventiCompletati eventi usati per il filtro, non nulli
     * @return lista non modificabile delle scelte disponibili, eventualmente vuota
     * @throws NullPointerException se l’insieme è nullo
     */
    public List<Scelta> getScelteDisponibili(Set<String> eventiCompletati) {
        if(eventiCompletati == null) throw new NullPointerException("Set nullo non ammesso");

        List<Scelta> scelteDisponibili = new ArrayList<>();

        // seleziona solo le scelte disponibili secondo gli eventi completati
        for(OpzioneScena opzione : opzioni){
            if(opzione.isDisponibile(eventiCompletati)) scelteDisponibili.add(opzione.scelta());
        }

        return List.copyOf(scelteDisponibili);
    }

    /**
     * Restituisce l’identificativo della scena.
     * @return l’identificativo
     */
    public String getId() {
        return id;
    }

    /**
     * Restituisce il titolo della scena.
     * @return il titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Restituisce la descrizione della scena.
     * @return la descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }
}
