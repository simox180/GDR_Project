public class Giocatore extends Personaggio{

    private final Inventario inventario;


    public Giocatore(String nome, int saluteMassima, int attaccoBase, int difesa, Inventario inventario){
        super(nome, saluteMassima, attaccoBase, difesa);

        this.inventario = inventario;
    }
}
