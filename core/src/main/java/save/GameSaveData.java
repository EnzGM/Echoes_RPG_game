package save;

public class GameSaveData {

    public float posX;

    public float posY;

    public float oxigenio;

    public float energia;

    public float tempoVivo;

    public String fase;

    public int oxigenioColetados;

    public int comidaColetada;

    public boolean abrigoDescoberto;

    public int versao = 1;

    public GameSaveData() {

    }

    public GameSaveData (float posX, float posY, float oxigenio, float energia,float tempoVivo, String fase){

        this.posX = posX;
        this.posY = posY;
        this.oxigenio = oxigenio;
        this.energia = energia;
        this.tempoVivo = tempoVivo;
        this.fase = fase;
        this.oxigenioColetados = 0;
        this.comidaColetada = 0;
        this.abrigoDescoberto = false;
        this.versao = 1;
    }

    @Override
    public String toString() {
        return "GameSaveData{" +
            ", posX = " + posX+
            ", oxigenio = " + posY +
            ", energia = " + oxigenio +
            ", tempoVivo = " + tempoVivo +
            ", fase = " + fase + '\'' +
            ", versao = " + versao +
            '}';
    }

}
