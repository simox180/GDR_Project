package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Rappresenta un personaggio controllato dal giocatore, dotato di inventario
 * e di slot per arma e armatura equipaggiate.
 */
public class Giocatore extends Personaggio {

    private @Nullable Arma armaEquipaggiata = null;
    private @Nullable Armatura armaturaEquipaggiata;
    private final Inventario inventario;


    /**
     * Crea un giocatore con le caratteristiche di base specificate e un inventario vuoto.
     *
     * @param nome il nome del giocatore
     * @param saluteMassima la salute massima iniziale
     * @param attaccoBase il valore di attacco base
     * @param difesa il valore di difesa base
     */
    public Giocatore(String nome, int saluteMassima, int attaccoBase, int difesa){
        super(nome, saluteMassima, attaccoBase, difesa);

        this.inventario = new Inventario();
    }

    /**
     * Imposta l'arma equipaggiata dal giocatore.
     *
     * @param arma l'arma da equipaggiare, non nulla
     * @throws NullPointerException se l'arma è nulla
     */
    public void equipaggiaArma(Arma arma){
        if(arma == null) throw new NullPointerException("it.unicam.cs.mpgc.rpg123058.Arma passata nulla");

        if(this.armaEquipaggiata != null){
            //this.inventario.addOggetto
        }
        this.armaEquipaggiata = arma;
    }

    /**
     * Calcola l'attacco corrente sommando, se presente, il bonus dell'arma equipaggiata.
     *
     * @return l'attacco base, maggiorato del bonus dell'arma equipaggiata quando presente
     */
    public int getAttacco(){
        Arma arma = this.armaEquipaggiata;

        if (arma == null) {
            return super.getAttaccoBase();
        }

        return super.getAttaccoBase() + arma.getAttaccoBonus();
    }

    /**
     * Calcola la difesa corrente sommando, se presente, il bonus dell'armatura equipaggiata.
     *
     * @return la difesa base, maggiorata del bonus dell'armatura equipaggiata quando presente
     */
    public int getDifesa() {
        if (armaturaEquipaggiata == null)
            return super.getDifesaBase();

        return super.getDifesaBase() + armaturaEquipaggiata.getDifesaBonus();
    }

    /**
     * Restituisce l'arma attualmente equipaggiata.
     *
     * @return l'arma equipaggiata, oppure {@code null} se non ne è equipaggiata alcuna
     */
    public @Nullable Arma getArmaEquipaggiata() {
        return armaEquipaggiata;
    }
    /**
     * Restituisce l'armatura attualmente equipaggiata.
     *
     * @return l'armatura equipaggiata, oppure {@code null} se non ne è equipaggiata alcuna
     */
    public @Nullable Armatura getArmaturaEquipaggiata(){
        return armaturaEquipaggiata;
    }

    /**
     * Restituisce l'inventario del giocatore.
     *
     * @return l'inventario associato al giocatore
     */
    public Inventario getInventario(){
        return inventario;
    }
}
