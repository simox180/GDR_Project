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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigurazionePartita {

    public Partita creaNuovaPartita(String nomeGiocatore) {

        Giocatore giocatore = new Giocatore(nomeGiocatore, 100, 8, 2);

        CaricatoreScene caricatore = new CaricatoreScene();
        //caricamento di tutte le scene della storia da 'resources'
        // (ordine non necessario visto che sono collegate tra loro tramite gli ID e la scena iniziale
        // va inserita nel costruttore di Partita)
        List<Scena> scene = caricatore.caricaScene(List.of(
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

        //catalogo degli Oggetti che passiamo al CatalogoIncontri per associarli al bottino dei nemici e per le scene di raccolta
        CatalogoOggetti catalogoOggetti = new CatalogoOggetti();
        Map<String, Oggetto> oggetti = catalogoOggetti.creaCatalogo();

        //funzione che crea un gestoreTurni per ogni combattimento
        Function<List<Personaggio>, GestoreTurni> creaGestore = partecipanti -> new GestoreTurniSequenziale(partecipanti);

        CalcolatoreDanno calcolatoreDanno = new CalcolatoreDannoBase();
        StrategiaNemico strategiaNemico = new StrategiaNemicoBase();

        //catalogo degli Incontri
        CatalogoIncontri catalogoIncontri = new CatalogoIncontri();

        Map<String, Function<Giocatore, Combattimento>> incontri = catalogoIncontri.creaCatalogo(oggetti, creaGestore, calcolatoreDanno, strategiaNemico);

        // restituzione della partita nella scena iniziale
        return new Partita(giocatore, scene, "sotto_le_macerie", oggetti, incontri);
    }

    /*  Per avviare una nuova partita:

        ConfigurazionePartita configurazione = new ConfigurazionePartita();
        Partita partita = configurazione.creaNuovaPartita("Thorin");
    */

}
