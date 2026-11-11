package it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import it.unicam.cs.mpgc.rpg123058.Utility;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class GestoreTurniSequenziale implements GestoreTurni {

    private final List<Personaggio> partecipanti;
    private int indiceCorrente = 0;

    /**
     * Crea una sequenza copiando almeno due partecipanti vivi e distinti per riferimento.
     * @param partecipanti lista di personaggi vivi e non nulli
     * @throws NullPointerException se la lista contiene elementi nulli o è nulla
     * @throws IllegalArgumentException se contiene meno di due elementi, duplicati o morti
     */
    public GestoreTurniSequenziale(List<Personaggio> partecipanti){
        if (partecipanti == null) throw new NullPointerException("La lista non può essere null");
        //List.contanis(null) lancia eccezione
        for (Personaggio partecipante : partecipanti) {
            if (partecipante == null) throw new NullPointerException("Un partecipante è null");
        }

        if(partecipanti.size() < 2|| Utility.trovaDuplicati(partecipanti)) throw new IllegalArgumentException("Non è ammessa una lista di un solo elemento o riferimenti duplicati nella lista");
        for(Personaggio partecipante : partecipanti){
            if(!(partecipante.isVivo())) throw new IllegalArgumentException("Personaggi morti non ammessi");
        }

        this.partecipanti = List.copyOf(partecipanti);
    }

    @Override
    /**
     * Consulta il turno senza avanzarlo.
     * @return il partecipante corrente o {@code null} se tutti sono morti
     */
    public @Nullable Personaggio getPartecipanteCorrente() {
        if(this.indiceCorrente == -1) return null;
        return this.partecipanti.get(this.indiceCorrente);
    }

    @Override
    /**
     * Passa al successivo partecipante vivo, saltando i morti; se nessuno vive, rende nullo il corrente.
     */
    public void avanzaTurno() {
        if(this.indiceCorrente == -1) return;
        int i;
        if(this.indiceCorrente == this.partecipanti.size() - 1){
            i = 0;
        }else{
            i = this.indiceCorrente + 1;
        }

        while(!(this.partecipanti.get(i).isVivo())){
            if(this.indiceCorrente == i){
                this.indiceCorrente = -1;
                return;
            }
            if(i == this.partecipanti.size() - 1){
                i = 0;
            }else{
                i++;
            }
        }
        this.indiceCorrente = i;
    }
}
