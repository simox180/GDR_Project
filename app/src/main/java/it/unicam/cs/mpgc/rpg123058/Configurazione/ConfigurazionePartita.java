package it.unicam.cs.mpgc.rpg123058.Configurazione;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDanno;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDannoBase;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurni;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.StrategiaNemico;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.StrategiaNemicoBase;
import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;
import it.unicam.cs.mpgc.rpg123058.Persistenza.CaricatoreScene;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoPartita;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoGiocatore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ConfigurazionePartita {

    public Partita creaNuovaPartita(String nomeGiocatore) {

        Giocatore giocatore = creaGiocatore(nomeGiocatore);
        List<Scena> scene = caricaScene();
        Map<String, Oggetto> oggetti = new CatalogoOggetti().creaCatalogo();

        // restituzione della partita nella scena iniziale
        return new Partita(giocatore, scene, "sotto_le_macerie", oggetti, creaIncontri(oggetti));
    }

    /*  Per avviare una nuova partita:
        ConfigurazionePartita configurazione = new ConfigurazionePartita();
        Partita partita = configurazione.creaNuovaPartita("Thorin");
    */

    /**
     * Ripristina una partita al punto in cui è stata salvata. Riceve lo stato salvato e controlla la validità di tutti
     * i campi del record StatoPartita, che usa per creare la partita chiamando direttamente il costruttore a 6 parametri
     * inserendo anche gli eventiCompletati (le scelte che sono già state fatte al momento del salvataggio),
     * insieme anche allo stato del giocatore (salute, equipaggiamenti e tutti gli oggetti nell'inventario)
     *
     * @return la partita al punto del salvataggio
     */
    public Partita ripristinaPartita(StatoPartita stato){

        Objects.requireNonNull(stato, "Stato nullo non ammesso");

        //per adesso il salvataggio è solo uno con la versione 1
        if (stato.versione() != 1) throw new IllegalArgumentException("Versione del salvataggio non supportata");

        String idScenaCorrente = stato.idScenaCorrente();
        if(idScenaCorrente == null) throw new NullPointerException("Id della scena corrente non può essere nullo");
        if(idScenaCorrente.isBlank()) throw new IllegalArgumentException("Id della scena vuoto o con solo spazi");

        //carico le scene prima per vedere se la scena corrente dello stato esiste
        List<Scena> scene = caricaScene();

        boolean idScenaTrovato = false;
        for(Scena scena : scene){
            if(scena.getId().equals(idScenaCorrente)){
                idScenaTrovato = true;
                break;
            }
        }
        if(!idScenaTrovato) throw new IllegalArgumentException("La scena corrente dello stato non esiste");

        Set<String> eventiCompletati = stato.eventiCompletati();

        if(eventiCompletati == null) throw new NullPointerException("Set degli eventi nullo non ammesso");

        for(String eventoCompletato : eventiCompletati){
            Objects.requireNonNull(eventoCompletato, "Eventi nulli non ammessi");
            if(eventoCompletato.isBlank()) throw new IllegalArgumentException("Eventi vuoti o di soli spazi non ammessi");
        }

        Map<String, Oggetto> oggetti = new CatalogoOggetti().creaCatalogo();

        Giocatore giocatore = ripristinaGiocatore(stato.giocatore(),oggetti);

        //i controlli sulla scena e sugli eventi si ripetono anche nel costruttore di partita, però li ho voluti mettere anche
        //qui perchè prendendo come parametro un oggetto derivato dal JSON voglio che sia controllato prima possibile
        // so che va contro il principio DRYS (Dont Repeat Yourself Stupid)
        return new Partita(giocatore, scene, idScenaCorrente, oggetti, creaIncontri(oggetti), eventiCompletati);
    }

    /**
     * Ripristina lo stato del giocatore al momento del salvataggio controllando e ricostruendo vita, equipaggiamenti e inventario.
     *
     * @param statoGiocatore lo stato del giocatore al momento del salvataggio
     * @param oggetti gli oggetti e i loro id contenuti nell'inventario del giocatore al momento del salvataggio
     * @return l'istanza del giocatore ricreata con le caratteristiche dello stato del giocatore passato
     */
    private Giocatore ripristinaGiocatore(StatoGiocatore statoGiocatore, Map<String, Oggetto> oggetti) {
        Objects.requireNonNull(statoGiocatore, "Stato del giocatore nullo");
        Objects.requireNonNull(oggetti, "Catalogo degli oggetti nullo");

        Giocatore giocatore = creaGiocatore(statoGiocatore.nome());

        if (statoGiocatore.salute() > giocatore.getSaluteMassima()) throw new IllegalArgumentException("La salute salvata supera la salute massima");

        for (String id : statoGiocatore.inventario().keySet()) {
            if (!oggetti.containsKey(id)) throw new IllegalArgumentException("Oggetto salvato inesistente");
        }

        Arma arma = null;
        //è possibile che al momento del salvataggio il giocatore non avesse nessun equipaggiamento negli slot
        if (statoGiocatore.armaEquipaggiata() != null) {
            Oggetto oggetto = oggetti.get(statoGiocatore.armaEquipaggiata());

            if (!(oggetto instanceof Arma)) throw new IllegalArgumentException("ID arma inesistente o associato a un oggetto diverso da un'arma");

            arma = (Arma) oggetto;
        }

        Armatura armatura = null;
        if (statoGiocatore.armaturaEquipaggiata() != null) {
            Oggetto oggetto = oggetti.get(statoGiocatore.armaturaEquipaggiata());

            if (!(oggetto instanceof Armatura)) throw new IllegalArgumentException("ID armatura inesistente o associato a un oggetto diverso da un'armatura");

            armatura = (Armatura) oggetto;
        }

        //porta la vita del giocatore al punto di salvataggio tramite .subisciDanno evitando un setVita dannoso
        giocatore.subisciDanno(giocatore.getSaluteMassima() - statoGiocatore.salute());

        //inserendo gli equipaggiamenti nell'inventario per poi equipaggiarli con .equipaggia evita un set dannoso come per la vita
        if (arma != null) {
            giocatore.getInventario().inserisciOggetto(arma);
            giocatore.equipaggiaArma(arma);
        }

        if (armatura != null) {
            giocatore.getInventario().inserisciOggetto(armatura);
            giocatore.equipaggiaArmatura(armatura);
        }

        //dopo i controlli per l'inventario dello stato, usiamo un entryset per aggiungere gli oggetti nell'inventario del giocatore
        //che stiamo ripristinando
        for (Map.Entry<String, Integer> voce : statoGiocatore.inventario().entrySet()) {
            Oggetto oggetto = oggetti.get(voce.getKey());
            //il campo integer definisce quanti duplicati dello stesso oggetto ci sono, quindi cè bisogno di inserirli tutti
            for (int i = 0; i < voce.getValue(); i++) {
                giocatore.getInventario().inserisciOggetto(oggetto);
            }
        }

        return giocatore;
    }

    /**
     * Crea un nuovo giocatore con le caratteristiche base, nella creazione di una nuova partita verrà passato così,
     * in ripristinaPartita sarà portato alla salute attuale del salvataggio
     *
     * @param nome del giocatore che si vuole creare
     * @return il giocatore creato
     */
    private Giocatore creaGiocatore(String nome){
        return new Giocatore(nome, 100, 8, 2);
    }

    /**
     * Carica tutte le scene della storia
     *
     * @return la lista delle scene
     */
    private List<Scena> caricaScene() {
        CaricatoreScene caricatore = new CaricatoreScene();

        //caricamento di tutte le scene della storia da 'resources'
        // (ordine non necessario visto che sono collegate tra loro tramite gli ID e la scena iniziale
        // va inserita nel costruttore di Partita)
        return caricatore.caricaScene(List.of(
                "sotto_le_macerie.json",
                "strada_interrotta.json",
                "segni_sulle_pareti.json",
                "galleria_echi.json",
                "sala_registri.json",
                "compagno_ferito.json",
                "ultima_riserva.json",
                "qualcosa_nelle_profondita.json",
                "ponte_khazad_dum.json",
                "primo_sole.json"
        ));
    }

    /**
     * Crea il catalogo degli incontri.
     *
     * @param oggetti il catalogo degli oggetti da assegnare ai loot dei nemici
     * @return una mappa con l'Id dell'incontro e la funzione per creare il combattimento
     */
    private Map<String, Function<Giocatore, Combattimento>> creaIncontri(Map<String, Oggetto> oggetti) {

        //funzione che crea un gestoreTurni per ogni combattimento al momento della creazione dello stesso
        Function<List<Personaggio>, GestoreTurni> creaGestore = partecipanti -> new GestoreTurniSequenziale(partecipanti);

        CalcolatoreDanno calcolatoreDanno = new CalcolatoreDannoBase();
        StrategiaNemico strategiaNemico = new StrategiaNemicoBase();

        return new CatalogoIncontri().creaCatalogo(oggetti, creaGestore, calcolatoreDanno, strategiaNemico);
    }


}
