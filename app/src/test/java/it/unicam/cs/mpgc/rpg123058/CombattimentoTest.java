package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Combattimento.CalcolatoreDanno.CalcolatoreDannoBase;
import it.unicam.cs.mpgc.rpg123058.Combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.GestoreTurni.GestoreTurniSequenziale;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StatoCombattimento;
import it.unicam.cs.mpgc.rpg123058.Combattimento.StrategiaNemico.AzioneNemico;
import it.unicam.cs.mpgc.rpg123058.Dominio.ConsumabileCurativo;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import it.unicam.cs.mpgc.rpg123058.Dominio.Nemico;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombattimentoTest {

    private Combattimento creaCombattimento(Giocatore giocatore, Nemico... nemici) {
        return new Combattimento(giocatore, List.of(nemici), GestoreTurniSequenziale::new,
                new CalcolatoreDannoBase(), nemico -> AzioneNemico.ATTACCO);
    }

    @Test
    void attaccoDelGiocatoreRiduceLaSaluteDelNemico() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 15, 2);
        Nemico nemico = new Nemico("Orco", 30, 8, 4, null);
        Combattimento combattimento = creaCombattimento(giocatore, nemico);

        combattimento.attaccaBersaglio(nemico);

        assertEquals(19, nemico.getSalute());
        assertEquals(100, giocatore.getSalute());
    }

    @Test
    void attaccoPassaIlTurnoAlNemico() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 15, 2);
        Nemico nemico = new Nemico("Orco", 30, 8, 4, null);
        Combattimento combattimento = creaCombattimento(giocatore, nemico);

        assertSame(giocatore, combattimento.getPersonaggioDiTurno());
        combattimento.attaccaBersaglio(nemico);

        assertSame(nemico, combattimento.getPersonaggioDiTurno());
    }

    @Test
    void esecuzioneDelTurnoNemicoAttaccaEPassaIlTurno() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 15, 2);
        Nemico nemico = new Nemico("Orco", 30, 8, 4, null);
        Combattimento combattimento = creaCombattimento(giocatore, nemico);
        combattimento.attaccaBersaglio(nemico);

        combattimento.eseguiTurnoNemico();

        assertEquals(94, giocatore.getSalute());
        assertSame(giocatore, combattimento.getPersonaggioDiTurno());
    }

    @Test
    void difesaDimezzaIlDannoRicevutoFinoAlTurnoSuccessivo() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 10, 2);
        Nemico nemico = new Nemico("Orco", 50, 12, 0, null);
        Combattimento combattimento = creaCombattimento(giocatore, nemico);

        combattimento.alzaDifesa();
        combattimento.eseguiTurnoNemico();
        assertEquals(95, giocatore.getSalute());

        combattimento.attaccaBersaglio(nemico);
        combattimento.eseguiTurnoNemico();
        assertEquals(85, giocatore.getSalute());
    }

    @Test
    void consumabileCuraIlGiocatoreSenzaTerminareIlTurno() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 10, 2);
        Nemico nemico = new Nemico("Orco", 30, 8, 0, null);
        ConsumabileCurativo pozione = new ConsumabileCurativo("Pozione", 20);
        giocatore.getInventario().inserisciOggetto(pozione);
        giocatore.subisciDanno(30);
        Combattimento combattimento = creaCombattimento(giocatore, nemico);

        assertTrue(combattimento.usaConsumabile(pozione));

        assertEquals(90, giocatore.getSalute());
        assertTrue(giocatore.getInventario().consultaInventario().isEmpty());
        assertSame(giocatore, combattimento.getPersonaggioDiTurno());
        combattimento.attaccaBersaglio(nemico);
        assertSame(nemico, combattimento.getPersonaggioDiTurno());
    }

    @Test
    void vittoriaQuandoTuttiINemiciSonoSconfitti() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 20, 2);
        Nemico primo = new Nemico("Orco", 10, 8, 0, null);
        Nemico secondo = new Nemico("Goblin", 10, 8, 0, null);
        Combattimento combattimento = creaCombattimento(giocatore, primo, secondo);

        combattimento.attaccaBersaglio(primo);
        assertEquals(StatoCombattimento.IN_CORSO, combattimento.getStatoCombattimento());
        assertSame(secondo, combattimento.getPersonaggioDiTurno());
        combattimento.eseguiTurnoNemico();
        combattimento.attaccaBersaglio(secondo);

        assertEquals(0, primo.getSalute());
        assertEquals(0, secondo.getSalute());
        assertEquals(StatoCombattimento.VITTORIA, combattimento.getStatoCombattimento());
        assertThrows(IllegalStateException.class, combattimento::getPersonaggioDiTurno);
    }

    @Test
    void sconfittaQuandoIlNemicoAzzeraLaSaluteDelGiocatore() {
        Giocatore giocatore = new Giocatore("Eroe", 10, 5, 0);
        Nemico nemico = new Nemico("Orco", 30, 12, 0, null);
        Combattimento combattimento = creaCombattimento(giocatore, nemico);
        combattimento.alzaDifesa();

        combattimento.eseguiTurnoNemico();
        assertEquals(StatoCombattimento.IN_CORSO, combattimento.getStatoCombattimento());
        combattimento.alzaDifesa();
        combattimento.eseguiTurnoNemico();

        assertEquals(0, giocatore.getSalute());
        assertEquals(StatoCombattimento.SCONFITTA, combattimento.getStatoCombattimento());
        assertThrows(IllegalStateException.class, combattimento::getPersonaggioDiTurno);
    }

    @Test
    void nemiciSconfittiSonoSaltatiNellaSequenzaDeiTurni() {
        Giocatore giocatore = new Giocatore("Eroe", 100, 20, 2);
        Nemico primo = new Nemico("Orco", 10, 8, 0, null);
        Nemico secondo = new Nemico("Goblin", 50, 8, 0, null);
        Combattimento combattimento = creaCombattimento(giocatore, primo, secondo);

        combattimento.attaccaBersaglio(primo);
        assertFalse(primo.isVivo());
        assertSame(secondo, combattimento.getPersonaggioDiTurno());

        combattimento.eseguiTurnoNemico();
        assertSame(giocatore, combattimento.getPersonaggioDiTurno());
        combattimento.alzaDifesa();
        assertSame(secondo, combattimento.getPersonaggioDiTurno());
    }
}
