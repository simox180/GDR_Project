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
    private final CalcolatoreDanno calcolatoreDanno;

   /**
    * Costruttore che acquisisce il giocatore, la lista dei nemici
    * e un gestoreTurni creato dalla funzione passata come parametro
    * per poter usare un gestore diverso polimorficamente.
    * Questo costruttore si occupa di creare il gestoreTurni in modo da avere
    * un solo punto di inizio usando una copia della lista dei partecipanti
    * così da non essere modificabile.
    *
    * @param giocatore giocatore protagonista del combattimento
    * @param nemici lista dei nemici scelti
    * @param creaGestore funzione che permette il polimorsfismo/scelta dei gestoriTurni
    * @param calcolatoreDanno calcolatore per il danno effettivo degli attacchi
    */
    public Combattimento(Giocatore giocatore, List<Nemico> nemici, Function<List<Personaggio>, GestoreTurni> creaGestore, CalcolatoreDanno calcolatoreDanno){
        if(giocatore == null || nemici == null || creaGestore == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(!(giocatore.isVivo())) throw new IllegalArgumentException("Giocatore non vivo");
        if(nemici.isEmpty()) throw new IllegalArgumentException("Nessun nemico nella lista");
        for(Nemico nemico : nemici){
            if(nemico == null) throw new NullPointerException("Uno dei nemici è nullo");
            if(!(nemico.isVivo())) throw new IllegalArgumentException("Uno dei nemici è già sconfitto");
        }
        if(Utility.trovaDuplicati(nemici)) throw new IllegalArgumentException("Nemici duplicati");

        this.nemici = List.copyOf(nemici); // assegno una copia della lista dei nemici in modo che il riferimento non possa essere modificato runtime compromettendo il combattimento
        List<Personaggio> partecipanti = new ArrayList<>();
        partecipanti.add(giocatore);
        partecipanti.addAll(this.nemici);

        GestoreTurni gestoreTurniApp = creaGestore.apply(partecipanti);

        this.gestoreTurni = Objects.requireNonNull(gestoreTurniApp, "Gestore nullo");
        this.giocatore = giocatore;
        this.calcolatoreDanno = calcolatoreDanno;
    }

    public Personaggio getPersonaggioDiTurno(){
        if(this.getStatoCombattimento() != StatoCombattimento.IN_CORSO) throw new IllegalStateException("Il combattimento è termitato");

        Personaggio partecipanteCorrente = this.gestoreTurni.getPartecipanteCorrente();

        if(partecipanteCorrente == null) throw new IllegalStateException("Nessun partecipante corrente");

        return partecipanteCorrente;
    }

    public StatoCombattimento getStatoCombattimento(){
        if(!(this.giocatore.isVivo())) return StatoCombattimento.SCONFITTA;

        for(Nemico nemico : nemici){
            if(nemico.isVivo()){
                return StatoCombattimento.IN_CORSO;
            }
        }

        return StatoCombattimento.VITTORIA;
    }


    public void attaccaBersaglio(Personaggio bersaglio){
        if(bersaglio == null) throw new NullPointerException("Bersaglio nullo");
        if(!(bersaglio.isVivo())) throw new IllegalArgumentException("Non è possibile bersagliare un personaggio morto");




    }
}
