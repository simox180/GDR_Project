package it.unicam.cs.mpgc.rpg123058.Dominio;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

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
        if(arma == null) throw new NullPointerException("Arma passata nulla");

        this.escludiOggetto(arma);

        if(this.armaEquipaggiata != null){
            this.inventario.inserisciOggetto(this.armaEquipaggiata);
        }


        this.armaEquipaggiata = arma;
    }

    public boolean disequipaggiaArma(){
        if(this.armaEquipaggiata == null) return false;

        this.inventario.inserisciOggetto(this.armaEquipaggiata);
        this.armaEquipaggiata = null;

        return true;
    }

    /**
     * Imposta l'armatura equipaggiata dal giocatore.
     *
     * @param armatura l'arma da equipaggiare, non nulla
     * @throws NullPointerException se l'armatura è nulla
     */
    public void equipaggiaArmatura(Armatura armatura){
        if(armatura == null) throw new NullPointerException("Armatura passata nulla");

        this.escludiOggetto(armatura);

        if(this.armaturaEquipaggiata != null){
            this.inventario.inserisciOggetto(this.armaturaEquipaggiata);
        }

        this.armaturaEquipaggiata = armatura;
    }

    public boolean disequipaggiaArmatura(){
        if(this.armaturaEquipaggiata == null) return false;

        this.inventario.inserisciOggetto(this.armaturaEquipaggiata);
        this.armaturaEquipaggiata = null;

        return true;
    }

    /**
     * Utilizza un consumabile, indipendentemente da quale effetto esso abbia.
     *
     * @param consumabile che si intende utilizzare
     * @return {@code true} se passano tutti i controlli e il consumabile viene utilizzato
     */
    public boolean usaConsumabile(Consumabile consumabile){
        Objects.requireNonNull(consumabile, "Consumabile nullo");
        if(!this.isVivo()) return false;

        if(!(consumabile.puoEssereUsato(this))) return false;
        boolean eliminato = this.inventario.rimuoviOggetto(consumabile);
        if(!eliminato) return false;

        consumabile.applicaEffetto(this);
        return true;
    }

    /**
     * Elimina l'oggetto che si sta equipaggiando dall'Inventario.
     *
     * @param oggetto
     * @throws IllegalArgumentException se l'oggetto che si sta equipaggiando non è contenuto nell'inventario
     */
    private void escludiOggetto(Oggetto oggetto){
        boolean eliminata = this.inventario.rimuoviOggetto(oggetto);
        if(!eliminata) throw new IllegalArgumentException("L'oggetto non è contenuta nell'inventario");
    }



    /**
     * Calcola l'attacco corrente sommando, se presente, il bonus dell'arma equipaggiata.
     *
     * @return l'attacco base, maggiorato del bonus dell'arma equipaggiata quando presente
     */
    @Override
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
    @Override
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

    public boolean isSaluteAlMassimo(){
        return super.getSalute() == super.getSaluteMassima();
    }
}
