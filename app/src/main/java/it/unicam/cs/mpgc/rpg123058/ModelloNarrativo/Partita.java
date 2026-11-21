package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StatoCombattimento;
import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaCombattimento;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaNavigazione;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaRaccolta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoGiocatore;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoPartita;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Function;

public class Partita {
    private final Giocatore giocatore;
    private final Map<String, Scena> scene;
    //gli eventi che definiscono la progressione di una determinata scena corrente
    private final Set<String> eventiCompletati = new HashSet<>();
    private String idScenaCorrente;
    private final Map<String, Oggetto> oggetti;
    //viene associato ogni id a una funzione che crea l'incontro
    private final Map<String, Function<Giocatore, Combattimento>> incontri;

    private Combattimento combattimentoAttivo;
    private String idIncontroAttivo;

    /**
     * Costruttore 1 (Nuova partita)
     * Costruttore per la creazione di una nuova partita, che utlizza il corpo del secondo costruttore passando
     * eventiCompletati vuoto
     */
    public Partita(Giocatore giocatore, List<Scena> scene, String idScenaIniziale, Map<String, Oggetto> oggetti, Map<String, Function<Giocatore, Combattimento>> incontri){

        //la chiamata this() esegue un altro costruttore della classe
        this(giocatore, scene, idScenaIniziale, oggetti, incontri, Set.of());
    }

    /**
     * Costruttore 2 (Continuo partita salvata)
     * Inizializza una partita con un giocatore vivo e una scena iniziale esistente. Copia i cataloghi e la collezione di scene; gli oggetti contenuti restano condivisi.
     * Questo costruttore accetta anche gli eventiCompletati se chiamato direttamente per riprendere una partita salvata,
     * invece se chiamato attraverso il primo costruttore avrà il campo eventiCompletati vuoto per iniziare una nuova partita
     *
     * @param giocatore protagonista vivo
     * @param scene scene non nulle con ID distinti
     * @param idScenaIniziale ID non blank di una scena presente
     * @param oggetti catalogo degli oggetti con chiavi non blank e valori non nulli
     * @param incontri factory degli incontri con chiavi non blank e valori non nulli
     * @throws NullPointerException se un parametro, una scena o una voce di catalogo è nulla
     * @throws IllegalArgumentException se il giocatore è morto, un ID è invalido o una scena è duplicata
     */
    public Partita(Giocatore giocatore, List<Scena> scene, String idScenaIniziale, Map<String, Oggetto> oggetti, Map<String, Function<Giocatore, Combattimento>> incontri, Set<String> eventiCompletati){
        if(giocatore == null || scene == null || idScenaIniziale == null || oggetti == null || incontri == null || eventiCompletati == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(idScenaIniziale.isBlank()) throw new IllegalArgumentException("Id scen iniziale buoto o contiene solo spazi");
        if (!giocatore.isVivo()) throw new IllegalArgumentException("Il giocatore deve essere vivo");

        Map<String, Scena> mappaTemporanea = new HashMap<>();

        //per evitare di riceve chiavi incoerenti si crea la mappa che associa la scena al suo ID
        // all'interno del costruttore, in modo da recuperare direttamente una destinazione per esempio
        for(Scena scena: scene){
            if(scena == null) throw new NullPointerException("Scena nulla non ammessa");

            String idScena = scena.getId();

            if (mappaTemporanea.containsKey(idScena)) {
                throw new IllegalArgumentException("ID scena duplicato");
            }

            mappaTemporanea.put(idScena, scena);
        }

        //controllo della consistenza degli eventiCompletati passati per riprendere una partita salvata
        //in caso di una nuova partita questo Set è vuoto (non nullo)
        for (String evento : eventiCompletati) {
            if(evento == null) throw new NullPointerException("ID evento nullo non ammesso");

            if (evento.isBlank()) throw new IllegalArgumentException("ID evento vuoto o di soli spazi");
        }

        if (!mappaTemporanea.containsKey(idScenaIniziale)) throw new IllegalArgumentException("Scena iniziale inesistente");

        this.oggetti = Map.copyOf(oggetti);
        this.incontri = Map.copyOf(incontri);

        for (String idOggetto : this.oggetti.keySet()) {
            if (idOggetto.isBlank()) throw new IllegalArgumentException("ID oggetto vuoto");
        }

        for (String idIncontro : this.incontri.keySet()) {
            if (idIncontro.isBlank()) throw new IllegalArgumentException("ID incontro vuoto");
        }

        this.scene = Map.copyOf(mappaTemporanea);
        this.giocatore = giocatore;
        this.idScenaCorrente = idScenaIniziale;
        this.eventiCompletati.addAll(eventiCompletati);
    }

    /**
     * Esegue una scelta disponibile della scena corrente: naviga, raccoglie o avvia un incontro.
     * @param scelta scelta effettivamente presente e disponibile
     * @throws NullPointerException se la scelta è nulla
     * @throws IllegalStateException se il giocatore è morto o uno scontro è attivo
     * @throws IllegalArgumentException se la scelta non è disponibile o un riferimento è inesistente
     */
    public void eseguiScelta(Scelta scelta){
        if(scelta == null) throw new NullPointerException("Scelta nulla non ammessa");
        if(!(this.giocatore.isVivo())) throw new IllegalStateException("Il giocatore deve essere vivo");

        if (combattimentoAttivo != null) throw new IllegalStateException("Devi prima concludere il combattimento attivo");

        if(!(this.getScelteDisponibili().contains(scelta))) throw new IllegalArgumentException("La scelta non appartiene alla scena corrente o non è disponibile");

        // Tramite questo switch case con Pattern Matching eseguo un metodo diverso a seconda del tipo di Scelta
        switch (scelta) {
            case SceltaNavigazione navigazione -> eseguiNavigazione(navigazione);

            case SceltaRaccolta raccolta -> eseguiRaccolta(raccolta);

            case SceltaCombattimento combattimento -> eseguiCombattimento(combattimento);

            default -> throw new UnsupportedOperationException("Tipo di scelta non gestito");
        }

    }

    /**
     * Sposta la scena corrente alla destinazione esistente della scelta già validata.
     * @param scelta scelta di navigazione disponibile
     * @throws IllegalArgumentException se la destinazione non esiste
     */
    private void eseguiNavigazione(SceltaNavigazione scelta) {
        String idDestinazione = scelta.idScenaDestinazione();

        if (!this.scene.containsKey(idDestinazione)) throw new IllegalArgumentException("Scena di destinazione inesistente");

        this.idScenaCorrente = idDestinazione;
    }

    /**
     * Inserisce l’oggetto nell’inventario e registra idScelta. La scelta è già stata validata.
     * @param scelta scelta di raccolta disponibile
     * @throws IllegalStateException se la raccolta è già avvenuta
     * @throws IllegalArgumentException se l’oggetto non è nel catalogo
     */
    private void eseguiRaccolta(SceltaRaccolta scelta) {
        String evento = scelta.id();

        if (eventiCompletati.contains(evento)) throw new IllegalStateException("Raccolta già effettuata");

        Oggetto oggetto = oggetti.get(scelta.idOggetto());

        if (oggetto == null) throw new IllegalArgumentException("Oggetto inesistente");

        giocatore.getInventario().inserisciOggetto(oggetto);
        eventiCompletati.add(evento);
    }

    /**
     * Crea e attiva l’incontro indicato dalla scelta già validata, senza registrare una vittoria.
     * @param scelta scelta di incontro disponibile
     * @throws IllegalStateException se l’incontro è già vinto o la factory restituisce {@code null}
     * @throws IllegalArgumentException se l’incontro non è nel catalogo
     */
    private void eseguiCombattimento(SceltaCombattimento scelta) {
        String idIncontro = scelta.idCombattimento();

        if (eventiCompletati.contains(idIncontro)) throw new IllegalStateException("Incontro già completato");

        //Funzione locale/temporanea che riceve un Giocatore e restituisce un Combattimento
        //recupera la funzione dalla mappa incontri e la esegue con apply
        Function<Giocatore, Combattimento> creaIncontro = incontri.get(idIncontro);

        if (creaIncontro == null) throw new IllegalArgumentException("Incontro inesistente");

        Combattimento nuovoCombattimento = creaIncontro.apply(giocatore);

        if (nuovoCombattimento == null) throw new IllegalStateException("La creazione dell'incontro ha restituito null");


        this.combattimentoAttivo = nuovoCombattimento;
        this.idIncontroAttivo = idIncontro;
    }

    /**
     * Restituisce la scena corrente senza cambiarla.
     * @return la scena corrente
     */
    public Scena getScenaCorrente() {
        return scene.get(idScenaCorrente);
    }

    /**
     * Consulta le scelte della scena corrente; durante uno scontro o dopo la sconfitta restituisce una lista vuota.
     * @return lista non modificabile delle scelte disponibili
     */
    public List<Scelta> getScelteDisponibili() {
        //per evitare di mostrare scelte durante uno scontro o dopo la morte
        if (!giocatore.isVivo() || combattimentoAttivo != null) return List.of();

        return getScenaCorrente().getScelteDisponibili(eventiCompletati);
    }

    /**
     * Chiude lo scontro terminato; in caso di vittoria assegna il bottino e registra idIncontro.
     * @throws IllegalStateException se non esiste uno scontro o è ancora in corso
     */
    private void concludiCombattimento(){
        if(this.combattimentoAttivo == null) throw new IllegalStateException("Non è attivo un combattimento");

        StatoCombattimento stato =
                this.combattimentoAttivo.getStatoCombattimento();

        if (stato == StatoCombattimento.IN_CORSO) {
            throw new IllegalStateException("Il combattimento è ancora in corso");
        }

        //il bottino viene assegnato solo se il combattimento è stato vinto
        if(stato == StatoCombattimento.VITTORIA){
            List<Oggetto> listaBottino = this.combattimentoAttivo.getBottino();
            for(Oggetto oggetto : listaBottino){
                this.giocatore.getInventario().inserisciOggetto(oggetto);
            }
            this.eventiCompletati.add(idIncontroAttivo);
        }

        this.combattimentoAttivo = null;
        this.idIncontroAttivo = null;
    }

    /**
     * Delega l’attacco del personaggio di turno e chiude uno scontro terminato.
     * @param bersaglio avversario vivo appartenente allo scontro
     * @throws IllegalStateException se non esiste uno scontro attivo
     * @throws IllegalArgumentException se il bersaglio non è valido
     */
    public void attaccaBersaglio(Personaggio bersaglio) {
        verificaCombattimentoAttivo();
        combattimentoAttivo.attaccaBersaglio(bersaglio);
        aggiornaEsitoCombattimento();
    }

    /**
     * Fa difendere il personaggio di turno e aggiorna l’esito dello scontro.
     * @throws IllegalStateException se non esiste uno scontro attivo
     */
    public void alzaDifesa() {
        verificaCombattimentoAttivo();
        combattimentoAttivo.alzaDifesa();
        aggiornaEsitoCombattimento();
    }

    /**
     * Delega l’azione del nemico di turno e aggiorna l’esito dello scontro.
     * @throws IllegalStateException se non esiste uno scontro attivo o non è il turno di un nemico
     */
    public void eseguiTurnoNemico() {
        verificaCombattimentoAttivo();
        combattimentoAttivo.eseguiTurnoNemico();
        aggiornaEsitoCombattimento();
    }

    /**
     * Tenta l’uso di un consumabile senza avanzare il turno e aggiorna l’esito.
     * @param consumabile oggetto da usare, non nullo
     * @return {@code true} se è stato usato, {@code false} se l’uso non è consentito
     * @throws IllegalStateException se non esiste uno scontro attivo
     */
    public boolean usaConsumabile(Consumabile consumabile) {
        verificaCombattimentoAttivo();

        boolean utilizzato = combattimentoAttivo.usaConsumabile(consumabile);

        aggiornaEsitoCombattimento();
        return utilizzato;
    }

    /**
     * Consulta i nemici ancora vivi.
     * @return lista non modificabile, eventualmente vuota
     * @throws IllegalStateException se non esiste uno scontro attivo
     */
    public List<Nemico> getNemiciVivi() {
        verificaCombattimentoAttivo();
        return combattimentoAttivo.getNemiciVivi();
    }

    /**
     * Verifica la precondizione comune delle azioni di scontro.
     * @throws IllegalStateException se non esiste uno scontro attivo
     */
    private void verificaCombattimentoAttivo() {
        if (combattimentoAttivo == null) {
            throw new IllegalStateException("Non è attivo un combattimento");
        }
    }

    /**
     * Conclude lo scontro attivo se il suo stato non è più in corso. Richiede uno scontro attivo.
     */
    private void aggiornaEsitoCombattimento() {
        if (combattimentoAttivo.getStatoCombattimento()
                != StatoCombattimento.IN_CORSO) {
            concludiCombattimento();
        }
    }

    /**
     * Cerca l'id di un oggetto all'interno del catalogo degli oggetti della partita.
     * Gli oggetti salvabili devono per forza provenire dal catalogo della partita.
     *
     * @param oggetto oggetto che si sta cercando
     * @return l'Id dell'oggetto se trovato oppure viene segnalata l'assenza dello stesso
     */
    private String trovaIdOggetto(Oggetto oggetto){
        Objects.requireNonNull(oggetto, "Oggetto nullo non ammessoo");

        String idOggettoTrovato = null;

        //qui non è possibile usare il .forEach perchè nelle lambda espressioni non si possono modificare variabili esterne
        for (Map.Entry<String, Oggetto> voce : oggetti.entrySet()) {
            //visto che gli oggetti istanziati dal catalogo solo sempre gli stessi è possibile usare ==
            if(voce.getValue() == oggetto) {
                if(idOggettoTrovato == null){
                    idOggettoTrovato = voce.getKey();
                }else{
                    //per essere sicuri che non ci siano errori nel caricamento degli oggetti
                    throw new IllegalStateException("Un oggetto non può essere associato a più di un Id");
                }
            }
        }

        if(idOggettoTrovato == null) throw new IllegalStateException("Oggetto non presente all'interno del catalogo");

        return idOggettoTrovato;
    }


    /**
     * Legge lo stato attuale della partita e costruisce i due record StatoPartita e StatoGiocatore che racchiudono solo
     * le informazioni utili per il salvataggio
     *
     * @return lo StatoPartita che contiene al suo interno lo StatoGiocatore
     */
    public StatoPartita esportaStato() {
        if (isInCombattimento()) throw new IllegalStateException("Non è possibile salvare durante un combattimento");

        if (!giocatore.isVivo()) throw new IllegalStateException("Non è possibile salvare dopo la sconfitta");

        Map<String, Integer> inventarioSalvato = new HashMap<>();

        //cerca l'Id di ogni oggetto nell'inventario del Giocatore in quel momento dal catalogo
        // e lo inserisce in una mappa per creare lo StatoGiocatore
        for (VoceInventario voce : giocatore.getInventario().consultaInventario()) {
            String idOggetto = trovaIdOggetto(voce.getOggetto());
            inventarioSalvato.put(idOggetto, voce.getQuantita());
        }

        Arma arma = giocatore.getArmaEquipaggiata();
        Armatura armatura = giocatore.getArmaturaEquipaggiata();

        //sintassi brutta ma compatta, se l'equipaggiamento non è null cerca e assegna il suo id
        String idArma = arma == null ? null : trovaIdOggetto(arma);
        String idArmatura = armatura == null ? null : trovaIdOggetto(armatura);

        StatoGiocatore statoGiocatore = new StatoGiocatore(
                giocatore.getNome(),
                giocatore.getSalute(),
                idArma,
                idArmatura,
                inventarioSalvato
        );

        return new StatoPartita(
                1,
                idScenaCorrente,
                eventiCompletati,
                statoGiocatore
        );
    }

    /**
     * Indica se è presente uno scontro attivo.
     * @return {@code true} durante uno scontro, {@code false} altrimenti
     */
    public boolean isInCombattimento() {
        return combattimentoAttivo != null;
    }

    /**
     * Indica se il giocatore è morto.
     * @return {@code true} se il giocatore non è vivo
     */
    public boolean isSconfitta() {
        return !giocatore.isVivo();
    }

    /**
     * Consulta il personaggio di turno dello scontro attivo.
     * @return il partecipante corrente
     * @throws IllegalStateException se non esiste uno scontro attivo
     */
    public Personaggio getPersonaggioDiTurno() {
        verificaCombattimentoAttivo();
        return combattimentoAttivo.getPersonaggioDiTurno();
    }

    /**
     * Restituisce una copia non modificabile degli eventi completati.
     * @return gli identificativi degli eventi completati
     */
    public Set<String> getEventiCompletati() {
        return Set.copyOf(eventiCompletati);
    }

    public List<VoceInventario> consultaInventario() {
        return giocatore.getInventario().consultaInventario();
    }

    public @Nullable Arma getArmaEquipaggiata() {
        return giocatore.getArmaEquipaggiata();
    }

    public @Nullable Armatura getArmaturaEquipaggiata() {
        return giocatore.getArmaturaEquipaggiata();
    }

    public void equipaggiaArma(Arma arma) {
        verificaCambioEquipaggiamento();
        giocatore.equipaggiaArma(arma);
    }

    public void equipaggiaArmatura(Armatura armatura) {
        verificaCambioEquipaggiamento();
        giocatore.equipaggiaArmatura(armatura);
    }

    public boolean disequipaggiaArma() {
        verificaCambioEquipaggiamento();
        return giocatore.disequipaggiaArma();
    }

    public boolean disequipaggiaArmatura() {
        verificaCambioEquipaggiamento();
        return giocatore.disequipaggiaArmatura();
    }

    private void verificaCambioEquipaggiamento() {
        if (isSconfitta()) {
            throw new IllegalStateException("Non puoi cambiare equipaggiamento dopo la sconfitta");
        }

        if (isInCombattimento()) {
            throw new IllegalStateException("Non puoi cambiare equipaggiamento durante un combattimento");
        }
    }

    public String getNomeGiocatore() {
        return giocatore.getNome();
    }

    public int getSaluteGiocatore() {
        return giocatore.getSalute();
    }

    public int getSaluteMassimaGiocatore() {
        return giocatore.getSaluteMassima();
    }

    public boolean isTurnoGiocatore() {
        return combattimentoAttivo != null && !isSconfitta() && combattimentoAttivo.getPersonaggioDiTurno() == giocatore;
    }

    public int getAttaccoGiocatore() {
        return giocatore.getAttacco();
    }

    public int getDifesaGiocatore() {
        return giocatore.getDifesa();
    }

}
