package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gestisce le voci di inventario, accorpando le copie dello stesso oggetto
 * in un'unica voce con quantità incrementata.
 */
public class Inventario {

    private final List<VoceInventario> oggetti = new ArrayList<>();

    /**
     * Crea un inventario inizialmente vuoto.
     */
    public Inventario(){

    }

    /**
     * Inserisce un oggetto nell'inventario o incrementa la quantità della sua voce esistente.
     *
     * @param oggetto l'oggetto da inserire, non nullo
     * @throws NullPointerException se l'oggetto è nullo
     */
    public void inserisciOggetto(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere null");

        VoceInventario appVoceInventario = this.cercaOggetto(oggetto);
        if(appVoceInventario != null){
            appVoceInventario.aumentaQuantita();
            return;
        }

        oggetti.add(new VoceInventario(oggetto));
    }

    /**
     * Rimuove una copia dell'oggetto indicato dall'inventario.
     * Se è presente una sola copia, rimuove la relativa voce.
     *
     * @param oggetto l'oggetto da rimuovere, non nullo
     * @return {@code true} se l'oggetto era presente ed è stato rimosso, altrimenti {@code false}
     * @throws NullPointerException se l'oggetto è nullo
     */
    public boolean rimuoviOggetto(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere null");

        VoceInventario appVoceInventario = this.cercaOggetto(oggetto);
        if(appVoceInventario != null){
            boolean diminuito = appVoceInventario.diminuisciQuantita();
            if(!diminuito) oggetti.remove(appVoceInventario);
            return true;
        }

        return false;
    }

    /**
     * Cerca la voce relativa all'oggetto indicato.
     *
     * @param oggetto l'oggetto da cercare, non nullo
     * @return la voce corrispondente, oppure {@code null} se l'oggetto non è presente
     * @throws NullPointerException se l'oggetto è nullo
     */
    private @Nullable VoceInventario cercaOggetto(@NonNull Oggetto oggetto){
        Objects.requireNonNull(oggetto, "L'oggetto non può essere nullo");

        int i=0;
        while(i < oggetti.size()){
            VoceInventario appoggio = oggetti.get(i);
            if(appoggio.getOggetto().equals(oggetto)){
                return appoggio;
            }
            i++;
        }

        return null;
    }

    public ArrayList<VoceInventario> consultaInventario(){
        List<VoceInventario> copiaInventario = this.oggetti;

        return (ArrayList<VoceInventario>) copiaInventario;
    }
}
