package it.unicam.cs.mpgc.rpg123058.Combattimento;

import it.unicam.cs.mpgc.rpg123058.Dominio.Consumabile;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;
import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import it.unicam.cs.mpgc.rpg123058.Utility;

import java.util.*;
import java.util.function.Function;


public class Combattimento {
    private final Giocatore giocatore;
    private final List<Nemico> nemici;
    private final GestoreTurni gestoreTurni;
    private final CalcolatoreDanno calcolatoreDanno;
    private final Set<Personaggio> personaggiInDifesa = new HashSet<>();
    private final StrategiaNemico strategiaNemico;

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
    public Combattimento(Giocatore giocatore, List<Nemico> nemici, Function<List<Personaggio>, GestoreTurni> creaGestore, CalcolatoreDanno calcolatoreDanno, StrategiaNemico strategiaNemico){
        if(giocatore == null || nemici == null || creaGestore == null || calcolatoreDanno == null || strategiaNemico == null) throw new NullPointerException("Parametri nulli non ammessi");
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
        this.strategiaNemico = strategiaNemico;
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

    /**
     * Azione di attacco eseguita dal Personaggio che sta agendo nel turno attuale
     *
     * @param bersaglio chi subisce il danno prodotto da chi esegue l'azione di attacco
     */
    public void attaccaBersaglio(Personaggio bersaglio){
        if(bersaglio == null) throw new NullPointerException("Bersaglio nullo");
        if(!(bersaglio.isVivo())) throw new IllegalArgumentException("Non è possibile bersagliare un personaggio morto");

        Personaggio attaccante = this.getPersonaggioDiTurno();

        if(!(this.validaAvversario(attaccante, bersaglio))) throw new IllegalArgumentException("I personaggi devono appartenere al combattimento e il bersaglio deve essere un avversario");

        int danno = calcolatoreDanno.calcolaDanno(attaccante,bersaglio);
        if (this.personaggiInDifesa.contains(bersaglio)) {
            danno = danno / 2;
        }

        bersaglio.subisciDanno(danno);

        this.terminaTurno();
    }

    /**
     * Validazione interna dell'avversario che si assicura che l'attaccante
     * e il bersaglio siano avversari e appartengano al combattimento
     *
     * @param attaccante chi produce l'attacco
     * @param bersaglio chi subisce i danni
     * @return {@code true} se l'attaccante e il bersaglio sono avversari
     * e appartengono al combattimento, {@code false} se sono alleati
     * o non appartengono al combattimento
     */
    private boolean validaAvversario(Personaggio attaccante, Personaggio bersaglio){
        if (attaccante == this.giocatore) {
            return this.nemici.contains(bersaglio);
        }

        if (this.nemici.contains(attaccante)) {
            return bersaglio == this.giocatore;
        }

        return false;
    }

    /**
     * Azione di utilizzo del Consumabile da parte del Personaggio
     * che sta agendo del turno attuale che deve essere il Giocatore
     *
     * @param consumabile oggetto usabile con effetto
     * @return {@code true} se l'utilizzo va a buon fine, {@code false} se
     * l'utilizzo non va a buon fine
     */
    public boolean usaConsumabile(Consumabile consumabile){
        if(consumabile == null) throw new NullPointerException("Consumabile nullo");

        Personaggio personaggio = this.getPersonaggioDiTurno();
        if(personaggio != this.giocatore) throw new IllegalStateException("Solo il giocatore può usare consumabili");

        return this.giocatore.usaConsumabile(consumabile);
    }

    public void alzaDifesa(){
        Personaggio personaggio = this.getPersonaggioDiTurno();
        this.personaggiInDifesa.add(personaggio);

        this.terminaTurno();
    }

    public void eseguiTurnoNemico(){
        Personaggio nemico = this.getPersonaggioDiTurno();
        if(!(this.nemici.contains(nemico))) throw new IllegalStateException("Non è il turno di un nemico");

        AzioneNemico azioneNemico = strategiaNemico.scegliAzione((Nemico) nemico);

        switch (azioneNemico){
            case ATTACCO -> {
                this.attaccaBersaglio(this.giocatore);
            }
            case DIFESA -> {
                this.alzaDifesa();
            }
        }
    }

    private void terminaTurno(){
        if(this.getStatoCombattimento() != StatoCombattimento.IN_CORSO) return;

        gestoreTurni.avanzaTurno();

        Personaggio personaggio = this.getPersonaggioDiTurno();

        personaggiInDifesa.remove(personaggio);
    }
}
