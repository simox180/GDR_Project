package it.unicam.cs.mpgc.rpg123058.ModelloNarrativo;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Oggetto;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.Scelta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaCombattimento;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaNavigazione;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scelta.SceltaRaccolta;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;

import java.util.*;
import java.util.function.Function;

public class Partita {
    private final Giocatore giocatore;
    private final Map<String, Scena> scene;
    private final Set<String> eventiCompletati = new HashSet<>();
    private String idScenaCorrente;
    private final Map<String, Oggetto> oggetti;
    //viene associato ogni id a una funzione che crea l'incontro
    private final Map<String, Function<Giocatore, Combattimento>> incontri;

    private Combattimento combattimentoAttivo;
    private String idIncontroAttivo;

    public Partita(Giocatore giocatore, List<Scena> scene, String idScenaIniziale, Map<String, Oggetto> oggetti, Map<String, Function<Giocatore, Combattimento>> incontri){
        if(giocatore == null || scene == null || idScenaIniziale == null || oggetti == null || incontri == null) throw new NullPointerException("Parametri nulli non ammessi");
        if(idScenaIniziale.isBlank()) throw new IllegalArgumentException("Id scen iniziale buoto o contiene solo spazi");

        Map<String, Scena> mappaTemporanea = new HashMap<>();

        //per evitare di riceve chiavi incoerenti si crea la mappa che associa la scena al suo ID
        // all'interno del costruttore, in modo da recuperare direttamente una destinazione per esempio
        for(Scena scena: scene){
            if(scena == null) throw new NullPointerException("Scena nulla non ammessa");

            String idScena = scena.getId();

            if (mappaTemporanea.containsKey(idScena)) {
                throw new IllegalArgumentException("ID scena duplicato: " + idScena);
            }

            mappaTemporanea.put(idScena, scena);
        }

        this.scene = Map.copyOf(mappaTemporanea);
        this.giocatore = giocatore;
        this.idScenaCorrente = idScenaIniziale;
        this.oggetti = Map.copyOf(oggetti);
        this.incontri = Map.copyOf(incontri);
    }

    public void eseguiScelta(Scelta scelta){
        if(scelta == null) throw new NullPointerException("Scelta nulla non ammessa");
        if(!(this.giocatore.isVivo())) throw new IllegalStateException("Il giocatore deve essere vivo");

        if(!(this.getScelteDisponibili().contains(scelta))) throw new IllegalArgumentException("La scelta non appartiene alla scena corrente o non è disponibile");

        if (combattimentoAttivo != null) throw new IllegalStateException("Devi prima concludere il combattimento attivo");

        // Tramite questo switch case con Pattern Matching eseguo un metodo diverso a seconda del tipo di Scelta
        switch (scelta) {
            case SceltaNavigazione navigazione -> eseguiNavigazione(navigazione);

            case SceltaRaccolta raccolta -> eseguiRaccolta(raccolta);

            case SceltaCombattimento combattimento -> eseguiCombattimento(combattimento);

            default -> throw new UnsupportedOperationException("Tipo di scelta non gestito");
        }

    }

    private void eseguiNavigazione(SceltaNavigazione scelta) {
        String idDestinazione = scelta.idScenaDestinazione();

        if (!this.scene.containsKey(idDestinazione)) throw new IllegalArgumentException("Scena di destinazione inesistente: " + idDestinazione);

        this.idScenaCorrente = idDestinazione;
    }

    private void eseguiRaccolta(SceltaRaccolta scelta) {
        String evento = "raccolta:" + idScenaCorrente + ":" + scelta.id();

        if (eventiCompletati.contains(evento)) throw new IllegalStateException("Raccolta già effettuata");

        Oggetto oggetto = oggetti.get(scelta.idOggetto());

        if (oggetto == null) throw new IllegalArgumentException("Oggetto inesistente: " + scelta.idOggetto());

        giocatore.getInventario().inserisciOggetto(oggetto);
        eventiCompletati.add(evento);
    }

    private void eseguiCombattimento(SceltaCombattimento scelta) {
        String idIncontro = scelta.idCombattimento();

        if (eventiCompletati.contains("vittoria:" + idIncontro)) throw new IllegalStateException("Incontro già completato");

        //Funzione locale/temporanea che riceve un Giocatore e restituisce un Combattimento
        //recupera la funzione dalla mappa incontri e la esegue con apply
        Function<Giocatore, Combattimento> creaIncontro = incontri.get(idIncontro);

        if (creaIncontro == null) throw new IllegalArgumentException("Incontro inesistente: " + idIncontro);

        Combattimento nuovoCombattimento = creaIncontro.apply(giocatore);

        if (nuovoCombattimento == null) throw new IllegalStateException("La creazione dell'incontro ha restituito null");


        this.combattimentoAttivo = nuovoCombattimento;
        this.idIncontroAttivo = idIncontro;
    }

    public Scena getScenaCorrente() {
        return scene.get(idScenaCorrente);
    }

    public List<Scelta> getScelteDisponibili() {
        return getScenaCorrente().getScelteDisponibili(eventiCompletati);
    }
}
