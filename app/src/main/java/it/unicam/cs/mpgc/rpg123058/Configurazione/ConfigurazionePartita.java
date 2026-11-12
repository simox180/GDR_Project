package it.unicam.cs.mpgc.rpg123058.Configurazione;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDanno;
import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDannoBase;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurni;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.StrategiaNemico;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.StrategiaNemicoBase;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Oggetto;
import it.unicam.cs.mpgc.rpg123058.Dominio.Personaggio;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Partita;
import it.unicam.cs.mpgc.rpg123058.ModelloNarrativo.Scena.Scena;
import it.unicam.cs.mpgc.rpg123058.Persistenza.CaricatoreScene;
import it.unicam.cs.mpgc.rpg123058.Salvataggio.StatoPartita;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public Partita ripristinaPartita(StatoPartita stato){

        Objects.requireNonNull(stato, "Stato nullo non ammesso");

        Giocatore giocatore = creaGiocatore(stato.giocatore().nome());

        for(String id : stato.giocatore().inventario().keySet())
        return null;
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
