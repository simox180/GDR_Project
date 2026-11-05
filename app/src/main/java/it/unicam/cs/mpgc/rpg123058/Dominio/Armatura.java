package it.unicam.cs.mpgc.rpg123058.Dominio;

/**
 * Rappresenta un oggetto equipaggiabile che incrementa la difesa del personaggio.
 */
public class Armatura extends Oggetto {

    private final int difesaBonus;

    /**
     * Crea un'armatura con il nome e il bonus di difesa indicati.
     *
     * @param nome il nome dell'armatura
     * @param difesaBonus il bonus di difesa, che non può essere negativo
     * @throws IllegalArgumentException se il bonus è negativo
     */
    public Armatura(String nome, int difesaBonus){
        super(nome);
        if(difesaBonus < 0) throw new IllegalArgumentException("La difesaBonus non può essere negativa");

        this.difesaBonus = difesaBonus;
    }

    /**
     * Restituisce il bonus di difesa fornito dall'armatura.
     *
     * @return il bonus di difesa
     */
    public int getDifesaBonus() {
        return difesaBonus;
    }
}
