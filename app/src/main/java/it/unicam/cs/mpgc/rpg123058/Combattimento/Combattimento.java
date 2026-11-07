package it.unicam.cs.mpgc.rpg123058.Combattimento;

import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;
import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import it.unicam.cs.mpgc.rpg123058.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Combattimento {
    private final Giocatore giocatore;
    private final List<Nemico> nemici;
    private final GestoreTurni gestoreTurni;

    public Combattimento(Giocatore giocatore, List<Nemico> nemici, Function<List<Personaggio>, GestoreTurni> creaGestore){
        if(giocatore == null || nemici == null || creaGestore == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(!(giocatore.isVivo())) throw new IllegalArgumentException("Giocatore non vivo");
        if(nemici.isEmpty()) throw new IllegalArgumentException("Nessun nemico nella lista");
        for(Nemico nemico : nemici){
            if(nemico == null) throw new NullPointerException("Uno dei nemici è nullo");
            if(!(nemico.isVivo())) throw new IllegalArgumentException("Uno dei nemici è già sconfitto");
        }
        if(Utility.trovaDuplicati(nemici)) throw new IllegalArgumentException("Nemici duplicati");

        this.nemici = List.copyOf(nemici);
        List<Personaggio> partecipanti = new ArrayList<>();
        partecipanti.add(giocatore);
        partecipanti.addAll(this.nemici);

        GestoreTurni gestoreTurniApp = creaGestore.apply(partecipanti);

        this.gestoreTurni = Objects.requireNonNull(gestoreTurniApp, "Gestore nullo");
        this.giocatore = giocatore;
    }
}
