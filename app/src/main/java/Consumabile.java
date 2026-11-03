public class Consumabile extends Oggetto {

    private final int puntiCura;

    public Consumabile(String nome, int puntiCura){
        super(nome);
        if(puntiCura <= 0) throw new IllegalArgumentException("");

        this.puntiCura = puntiCura;
    }

    public int getPuntiCura() {
        return puntiCura;
    }
}
