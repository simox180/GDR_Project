package it.unicam.cs.mpgc.rpg123058;

/**
 * Rappresenta un oggetto che fornisce punti cura quando viene utilizzato.
 */
public class Consumabile extends Oggetto {

    private final int puntiCura;

    /**
     * Crea un consumabile con il nome e i punti cura indicati.
     *
     * @param nome il nome del consumabile
     * @param puntiCura i punti cura forniti, che devono essere positivi
     * @throws IllegalArgumentException se i punti cura non sono positivi
     */
    public Consumabile(String nome, int puntiCura){
        super(nome);
        if(puntiCura <= 0) throw new IllegalArgumentException("");

        this.puntiCura = puntiCura;
    }

    /**
     * Restituisce i punti cura associati al consumabile.
     *
     * @return i punti cura forniti dall'oggetto
     */
    public int getPuntiCura() {
        return puntiCura;
    }
}
