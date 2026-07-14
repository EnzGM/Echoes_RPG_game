package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import managers.AssetManager;

public class Item extends Entidade {
    private final Sprite sprite;
    private final String tipo;
    private boolean coletado = false;

    public Item(float x, float y, String tipo, AssetManager assets) {
        super(x, y, tipo.equals("abrigo") ? 320 : 32, tipo.equals("abrigo") ? 220 : 48);
        this.tipo = tipo;

        switch (tipo) {
            case "oxigenio":
                sprite = new Sprite(assets.oxigenioTexture);
                sprite.setSize(32, 48);
                break;
            case "comida":
                sprite = new Sprite(assets.comidaTexture);
                sprite.setSize(40, 40);
                break;
            case "abrigo":
                sprite = new Sprite(assets.abrigoTexture);
                sprite.setSize(320, 226);
                break;
            default:
                sprite = new Sprite(assets.oxigenioTexture);
                sprite.setSize(32, 32);
                break;
        }
    }

    @Override
    public void update(float delta) {}

    @Override
    public void render(SpriteBatch batch) {
        if (!coletado) {
            sprite.setPosition(position.x, position.y);
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        // Textura gerenciada pelo AssetManager
    }

    public String getTipo() { return tipo; }
    public boolean isColetado() { return coletado; }

    public void coletar() {
        this.coletado = true;
    }
}
