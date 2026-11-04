package it.unicam.cs.mpgc.rpg123058;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Giocatore extends Personaggio {

    private @Nullable Arma armaEquipaggiata = null;
    private @Nullable Armatura armaturaEquipaggiata;
    private final Inventario inventario;


    public Giocatore(String nome, int saluteMassima, int attaccoBase, int difesa){
        super(nome, saluteMassima, attaccoBase, difesa);

        this.inventario = new Inventario();
    }

    public void equipaggiaArma(Arma arma){
        if(arma == null) throw new NullPointerException("it.unicam.cs.mpgc.rpg123058.Arma passata nulla");

        if(this.armaEquipaggiata != null){
            //this.inventario.addOggetto
        }
        this.armaEquipaggiata = arma;
    }

    public int getAttacco(){
        Arma arma = this.armaEquipaggiata;

        if (arma == null) {
            return super.getAttaccoBase();
        }

        return super.getAttaccoBase() + arma.getAttaccoBonus();
    }

    public int getDifesa() {
        if (armaturaEquipaggiata == null)
            return super.getDifesaBase();

        return super.getDifesaBase() + armaturaEquipaggiata.getDifesaBonus();
    }

    public @Nullable Arma getArmaEquipaggiata() {
        return armaEquipaggiata;
    }
    public @Nullable Armatura getArmaturaEquipaggiata(){
        return armaturaEquipaggiata;
    }

    public Inventario getInventario(){
        return inventario;
    }
}
