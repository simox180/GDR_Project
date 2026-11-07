package it.unicam.cs.mpgc.rpg123058.Combattimento;

import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import it.unicam.cs.mpgc.rpg123058.Utility;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GestoreTurniSequenziale implements GestoreTurni{

    private final List<Personaggio> partecipanti;
    private int indiceCorrente = 0;

    public GestoreTurniSequenziale(List<Personaggio> partecipanti){
        if(partecipanti == null || partecipanti.contains(null)) throw new NullPointerException("Ne la lista ne i partecipanti possono essere null");
        if(partecipanti.size() < 2|| Utility.trovaDuplicati(partecipanti)) throw new IllegalArgumentException("Non è ammessa una lista di un solo elemento o riferimenti duplicati nella lista");
        for(Personaggio partecipante : partecipanti){
            if(!(partecipante.isVivo())) throw new IllegalArgumentException("Personaggi morti non ammessi");
        }

        this.partecipanti = partecipanti;
    }

    @Override
    public @Nullable Personaggio getPartecipanteCorrente() {
        if(this.indiceCorrente == -1) return null;
        return this.partecipanti.get(this.indiceCorrente);
    }

    @Override
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
