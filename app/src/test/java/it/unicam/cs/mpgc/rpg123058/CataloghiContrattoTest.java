package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StatoCombattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.AzioneNemico;
import it.unicam.cs.mpgc.rpg123058.Configurazione.CatalogoIncontri;
import it.unicam.cs.mpgc.rpg123058.Configurazione.CatalogoOggetti;
import it.unicam.cs.mpgc.rpg123058.Dominio.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CataloghiContrattoTest {
    private final Map<String, Oggetto> oggetti = new CatalogoOggetti().creaCatalogo();

    @Test
    void oggettiHannoIdTipiEValoriDichiarati() {
        assertEquals(Set.of("acqua", "pane", "ascia_nanica_danneggiata", "spada_nanica",
                "martello_guerra_nanico", "scudo_nanico", "corazza_maglia_nanica"), oggetti.keySet());
        assertEquals(15, assertInstanceOf(ConsumabileCurativo.class, oggetti.get("acqua")).getPuntiCura());
        assertEquals(30, assertInstanceOf(ConsumabileCurativo.class, oggetti.get("pane")).getPuntiCura());
        assertEquals(3, assertInstanceOf(Arma.class, oggetti.get("ascia_nanica_danneggiata")).getAttaccoBonus());
        assertEquals(5, assertInstanceOf(Arma.class, oggetti.get("spada_nanica")).getAttaccoBonus());
        assertEquals(8, assertInstanceOf(Arma.class, oggetti.get("martello_guerra_nanico")).getAttaccoBonus());
        assertEquals(2, assertInstanceOf(Armatura.class, oggetti.get("scudo_nanico")).getDifesaBonus());
        assertEquals(4, assertInstanceOf(Armatura.class, oggetti.get("corazza_maglia_nanica")).getDifesaBonus());
        assertThrows(UnsupportedOperationException.class, () -> oggetti.put("altro", new Arma("Altro", 1)));
    }

    private Map<String, Function<Giocatore, Combattimento>> incontri() {
        return new CatalogoIncontri().creaCatalogo(oggetti, GestoreTurniSequenziale::new,
                (attaccante, bersaglio) -> 100, nemico -> AzioneNemico.DIFESA);
    }

    @Test
    void incontriCreanoPartecipantiNuoviEBottinoPrevisto() {
        Map<String, Function<Giocatore, Combattimento>> incontri = incontri();
        assertEquals(Set.of("orchi_galleria", "orchi_inseguitori", "capitano_orchi"), incontri.keySet());
        assertThrows(UnsupportedOperationException.class, () -> incontri.clear());
        var giocatore = new Giocatore("Ada", 100, 8, 2);
        assertIncontro(incontri.get("orchi_galleria").apply(giocatore), giocatore,
                List.of("Orco esploratore", "Orco guerriero"), List.of(20, 25), List.of(6, 7), List.of(1, 2),
                List.of(oggetti.get("spada_nanica")));
        assertIncontro(incontri.get("orchi_inseguitori").apply(giocatore), giocatore,
                List.of("Orco inseguitore", "Orco veterano"), List.of(28, 35), List.of(8, 9), List.of(2, 3),
                List.of(oggetti.get("martello_guerra_nanico")));
        assertIncontro(incontri.get("capitano_orchi").apply(giocatore), giocatore,
                List.of("Capitano degli Orchi"), List.of(60), List.of(12), List.of(4), List.of());
    }

    private void assertIncontro(Combattimento combattimento, Giocatore giocatore, List<String> nomi,
                               List<Integer> salute, List<Integer> attacco, List<Integer> difesa, List<Oggetto> bottino) {
        assertSame(giocatore, combattimento.getPersonaggioDiTurno());
        List<Nemico> nemici = combattimento.getNemiciVivi();
        assertEquals(nomi, nemici.stream().map(Nemico::getNome).toList());
        assertEquals(salute, nemici.stream().map(Nemico::getSalute).toList());
        assertEquals(attacco, nemici.stream().map(Nemico::getAttacco).toList());
        assertEquals(difesa, nemici.stream().map(Nemico::getDifesa).toList());
        for (Nemico nemico : nemici) nemico.subisciDanno(nemico.getSalute());
        assertEquals(StatoCombattimento.VITTORIA, combattimento.getStatoCombattimento());
        assertEquals(bottino, combattimento.getBottino());
    }

    @Test
    void applicazioniRipetuteNonCondividonoNemiciEUsanoLeDipendenze() {
        AtomicInteger gestori = new AtomicInteger();
        AtomicInteger calcoli = new AtomicInteger();
        AtomicInteger strategie = new AtomicInteger();
        var incontri = new CatalogoIncontri().creaCatalogo(oggetti,
                partecipanti -> { gestori.incrementAndGet(); return new GestoreTurniSequenziale(partecipanti); },
                (a, b) -> { calcoli.incrementAndGet(); return 1; },
                n -> { strategie.incrementAndGet(); return AzioneNemico.DIFESA; });
        var giocatore = new Giocatore("Ada", 100, 8, 2);
        Combattimento primo = incontri.get("orchi_galleria").apply(giocatore);
        Combattimento secondo = incontri.get("orchi_galleria").apply(giocatore);
        assertNotSame(primo, secondo);
        assertEquals(2, gestori.get());
        Nemico primoNemico = primo.getNemiciVivi().get(0);
        Nemico secondoNemico = secondo.getNemiciVivi().get(0);
        assertNotSame(primoNemico, secondoNemico);
        primo.attaccaBersaglio(primoNemico);
        assertEquals(19, primoNemico.getSalute());
        assertEquals(20, secondoNemico.getSalute());
        assertEquals(1, calcoli.get());
        primo.eseguiTurnoNemico();
        assertEquals(1, strategie.get());
        assertEquals(100, giocatore.getSalute());
    }

    @Test
    void rifiutaDipendenzeNulleEBottiniObbligatoriAssenti() {
        CatalogoIncontri catalogo = new CatalogoIncontri();
        Function<List<Personaggio>, it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurni> gestore = GestoreTurniSequenziale::new;
        var danno = (it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDanno) (a, b) -> 1;
        var strategia = (it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.StrategiaNemico) n -> AzioneNemico.DIFESA;
        assertThrows(NullPointerException.class, () -> catalogo.creaCatalogo(null, gestore, danno, strategia));
        assertThrows(NullPointerException.class, () -> catalogo.creaCatalogo(oggetti, null, danno, strategia));
        assertThrows(NullPointerException.class, () -> catalogo.creaCatalogo(oggetti, gestore, null, strategia));
        assertThrows(NullPointerException.class, () -> catalogo.creaCatalogo(oggetti, gestore, danno, null));
        assertThrows(NullPointerException.class, () -> catalogo.creaCatalogo(Map.of("spada_nanica", oggetti.get("spada_nanica")), gestore, danno, strategia));
        assertThrows(NullPointerException.class, () -> catalogo.creaCatalogo(Map.of("martello_guerra_nanico", oggetti.get("martello_guerra_nanico")), gestore, danno, strategia));
    }
}
