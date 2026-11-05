package it.unicam.cs.mpgc.rpg123058;

import it.unicam.cs.mpgc.rpg123058.Dominio.Arma;
import it.unicam.cs.mpgc.rpg123058.Dominio.Inventario;
import it.unicam.cs.mpgc.rpg123058.Dominio.VoceInventario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {

    @Test
    void dueOggettiEquivalentiFormanoUnaVoceConQuantitaDue() {
        Inventario inventario = new Inventario();
        Arma prima = new Arma("Spada", 5);
        Arma seconda = new Arma("Spada", 5);

        inventario.inserisciOggetto(prima);
        inventario.inserisciOggetto(seconda);

        List<VoceInventario> voci = inventario.consultaInventario();
        assertEquals(1, voci.size());
        assertEquals(prima, voci.get(0).getOggetto());
        assertEquals(2, voci.get(0).getQuantita());
    }

    @Test
    void rimozioneDiminuisceQuantitaPoiEliminaLaVoce() {
        Inventario inventario = new Inventario();
        Arma arma = new Arma("Spada", 5);
        inventario.inserisciOggetto(arma);
        inventario.inserisciOggetto(new Arma("Spada", 5));

        assertTrue(inventario.rimuoviOggetto(arma));
        assertEquals(1, inventario.consultaInventario().get(0).getQuantita());
        assertTrue(inventario.rimuoviOggetto(arma));
        assertTrue(inventario.consultaInventario().isEmpty());
        assertFalse(inventario.rimuoviOggetto(arma));
    }

    @Test
    void modificheAllaConsultazioneNonCambianoLinventario() {
        Inventario inventario = new Inventario();
        Arma arma = new Arma("Spada", 5);
        inventario.inserisciOggetto(arma);
        inventario.inserisciOggetto(new Arma("Spada", 5));

        List<VoceInventario> copia = inventario.consultaInventario();
        copia.get(0).diminuisciQuantita();
        copia.clear();

        List<VoceInventario> originali = inventario.consultaInventario();
        assertEquals(1, originali.size());
        assertEquals(2, originali.get(0).getQuantita());
    }

    @Test
    void inserimentoDiNullGeneraNullPointerException() {
        Inventario inventario = new Inventario();

        assertThrows(NullPointerException.class, () -> inventario.inserisciOggetto(null));
        assertTrue(inventario.consultaInventario().isEmpty());
    }
}
