public class Nemico extends Personaggio{

    private Oggetto bottino;

    public Nemico(String nome, int saluteMassima, int attaccoBase, int difesa, Oggetto bottino){
        super(nome, saluteMassima, attaccoBase, difesa);
        this.bottino = bottino;
    }

    public Oggetto getBottino() {
        return bottino;
    }
}
