package it.unicam.cs.mpgc.rpg123058;

public class Armatura extends Oggetto {

    private final int difesaBonus;

    public Armatura(String nome, int difesaBonus){
        super(nome);
        if(difesaBonus < 0) throw new IllegalArgumentException("La difesaBonus non può essere negativa");

        this.difesaBonus = difesaBonus;
    }

    public int getDifesaBonus() {
        return difesaBonus;
    }
}
