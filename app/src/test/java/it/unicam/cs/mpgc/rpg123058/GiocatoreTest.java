package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Dominio.Arma;
import it.unicam.cs.mpgc.rpg123058.Dominio.Armatura;
import it.unicam.cs.mpgc.rpg123058.Dominio.ConsumabileCurativo;
import it.unicam.cs.mpgc.rpg123058.Dominio.Giocatore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GiocatoreTest {

    @Test
    void equipaggiareESostituireArmaAggiornaAttaccoEInventario() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        Arma prima = new Arma("Spada", 5);
        Arma seconda = new Arma("Ascia", 8);
        giocatore.getInventario().inserisciOggetto(prima);
        giocatore.getInventario().inserisciOggetto(new Arma("Spada", 5));
        giocatore.getInventario().inserisciOggetto(seconda);

        giocatore.equipaggiaArma(prima);
        assertSame(prima, giocatore.getArmaEquipaggiata());
        assertEquals(15, giocatore.getAttacco());
        assertEquals(1, giocatore.getInventario().consultaInventario().stream()
                .filter(voce -> voce.getOggetto().equals(prima)).findFirst().orElseThrow().getQuantita());

        giocatore.equipaggiaArma(seconda);

        assertSame(seconda, giocatore.getArmaEquipaggiata());
        assertEquals(18, giocatore.getAttacco());
        assertEquals(2, giocatore.getInventario().consultaInventario().get(0).getQuantita());
        assertEquals(1, giocatore.getInventario().consultaInventario().size());
    }

    @Test
    void armaAssenteNonModificaSlotOInventario() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        Arma equipaggiata = new Arma("Spada", 5);
        Arma altra = new Arma("Ascia", 8);
        giocatore.getInventario().inserisciOggetto(equipaggiata);
        giocatore.getInventario().inserisciOggetto(new Arma("Pugnale", 2));
        giocatore.equipaggiaArma(equipaggiata);

        assertThrows(IllegalArgumentException.class, () -> giocatore.equipaggiaArma(altra));

        assertSame(equipaggiata, giocatore.getArmaEquipaggiata());
        assertEquals(15, giocatore.getAttacco());
        assertEquals(1, giocatore.getInventario().consultaInventario().size());
        assertEquals("Pugnale", giocatore.getInventario().consultaInventario().get(0).getOggetto().getNome());
        assertEquals(1, giocatore.getInventario().consultaInventario().get(0).getQuantita());
    }

    @Test
    void disequipaggiareArmaturaLaRestituisceERipristinaDifesa() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        Armatura armatura = new Armatura("Corazza", 4);
        giocatore.getInventario().inserisciOggetto(armatura);
        giocatore.equipaggiaArmatura(armatura);
        assertEquals(7, giocatore.getDifesa());

        assertTrue(giocatore.disequipaggiaArmatura());
        assertNull(giocatore.getArmaturaEquipaggiata());
        assertEquals(3, giocatore.getDifesa());
        assertEquals(1, giocatore.getInventario().consultaInventario().size());
        assertEquals(armatura, giocatore.getInventario().consultaInventario().get(0).getOggetto());
        assertFalse(giocatore.disequipaggiaArmatura());
    }

    @Test
    void consumabileCurativoDaFeritoCuraEConsumaUnaCopia() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        ConsumabileCurativo pozione = new ConsumabileCurativo("Pozione", 20);
        giocatore.getInventario().inserisciOggetto(pozione);
        giocatore.getInventario().inserisciOggetto(new ConsumabileCurativo("Pozione", 20));
        giocatore.subisciDanno(30);

        assertTrue(giocatore.usaConsumabile(pozione));

        assertEquals(90, giocatore.getSalute());
        assertEquals(1, giocatore.getInventario().consultaInventario().get(0).getQuantita());
    }

    @Test
    void consumabileASalutePienaRestaNellInventario() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        ConsumabileCurativo pozione = new ConsumabileCurativo("Pozione", 20);
        giocatore.getInventario().inserisciOggetto(pozione);

        assertFalse(giocatore.usaConsumabile(pozione));

        assertEquals(100, giocatore.getSalute());
        assertEquals(1, giocatore.getInventario().consultaInventario().get(0).getQuantita());
    }

    @Test
    void consumabileDaSconfittoNonSiUsa() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        ConsumabileCurativo pozione = new ConsumabileCurativo("Pozione", 20);
        giocatore.getInventario().inserisciOggetto(pozione);
        giocatore.subisciDanno(100);

        assertFalse(giocatore.usaConsumabile(pozione));

        assertEquals(0, giocatore.getSalute());
        assertEquals(1, giocatore.getInventario().consultaInventario().get(0).getQuantita());
    }

    @Test
    void consumabileAssenteNonCambiaLaSalute() {
        Giocatore giocatore = new Giocatore("Ada", 100, 10, 3);
        ConsumabileCurativo pozione = new ConsumabileCurativo("Pozione", 20);
        giocatore.subisciDanno(30);

        assertFalse(giocatore.usaConsumabile(pozione));

        assertEquals(70, giocatore.getSalute());
        assertTrue(giocatore.getInventario().consultaInventario().isEmpty());
    }
}
