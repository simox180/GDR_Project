package it.unicam.cs.mpgc.rpg123058;

/**
 * Definisce le caratteristiche e le operazioni comuni ai personaggi del gioco.
 * La salute corrente è mantenuta nell'intervallo da zero alla salute massima.
 */
public abstract class Personaggio {

    private String nome;
    private int salute;
    private final int saluteMassima;
    private final int attaccoBase;
    private final int difesaBase;

    /**
     * Inizializza un personaggio con salute corrente pari alla sua salute massima.
     *
     * @param nome il nome del personaggio, non vuoto
     * @param saluteMassima il valore massimo di salute, che deve essere positivo
     * @param attaccoBase il valore di attacco base, che deve essere positivo
     * @param difesaBase il valore di difesa base
     * @throws IllegalArgumentException se il nome è vuoto, la salute massima o l'attacco base
     *                                  non sono positivi
     */
    public Personaggio(String nome, int saluteMassima, int attaccoBase, int difesaBase){
        if(nome.isEmpty()) throw new IllegalArgumentException("Il nome deve essere assegnato");
        if(saluteMassima <= 0 || attaccoBase <= 0) throw new IllegalArgumentException("La saluteMassima o l'attaccoBase non possono essere minori o uguali a zero");

        this.nome = nome;
        this.saluteMassima = saluteMassima;
        this.salute = this.saluteMassima;
        this.attaccoBase = attaccoBase;
        this.difesaBase = difesaBase;
    }

    /**
     * Riduce la salute corrente del danno ricevuto senza portarla sotto zero.
     *
     * @param danno l'ammontare del danno, che deve essere positivo
     * @throws IllegalArgumentException se il danno non è positivo
     */
    public void subisciDanno(int danno){
        if(danno <= 0) throw new IllegalArgumentException("Il danno non può essere negativo o nullo");

        this.salute = Math.max(0, this.salute - danno);
    }

    /**
     * Aumenta la salute corrente senza superare la salute massima.
     *
     * @param cure l'ammontare della cura, che deve essere positivo
     * @throws IllegalArgumentException se la cura non è positiva
     */
    public void cura(int cure){
        if(cure <= 0) throw new IllegalArgumentException("Le cure non possono essere negative o nulle");

        this.salute = Math.min(this.salute += cure, this.saluteMassima);
    }


    /**
     * Verifica se il personaggio possiede ancora almeno un punto salute.
     *
     * @return {@code true} se la salute corrente è diversa da zero, altrimenti {@code false}
     */
    public boolean isVivo(){ return (this.salute != 0); }

//------------------------------------------GETTER--------------------------------------
    /**
     * Restituisce il nome del personaggio.
     *
     * @return il nome del personaggio
     */
    public String getNome() { return nome; }

    /**
     * Restituisce la salute corrente del personaggio.
     *
     * @return la salute corrente
     */
    public int getSalute() { return salute; }

    /**
     * Restituisce la salute massima del personaggio.
     *
     * @return la salute massima
     */
    public int getSaluteMassima() { return saluteMassima; }

    /**
     * Restituisce il valore di attacco base del personaggio.
     *
     * @return l'attacco base
     */
    public int getAttaccoBase() { return attaccoBase; }

    /**
     * Restituisce il valore di difesa base del personaggio.
     *
     * @return la difesa base
     */
    public int getDifesaBase() { return difesaBase; }
//---------------------------------------------------------------------------------------

}
