package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import managers.AssetManager;

public class Astronauta extends Entidade implements Interagivel {
    private float oxigenio = 100f;
    private float energia = 100f;
    private float speed = 200f;
    private final Sprite sprite;
    private boolean protegido = false;
    private boolean viradoEsquerda = false;
    private float tempoVivo = 0f; // Tempo de sobrevivência

    public Astronauta(float x, float y, AssetManager assets) {
        super(x, y, 32, 48);
        this.sprite = new Sprite(assets.astronautaTexture);
        this.sprite.setSize(32, 48);
    }

    @Override
    public void update(float delta) {
        if (!ativo) return;
        velocity.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.y = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.y = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -speed;
            viradoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = speed;
            viradoEsquerda = false;
        }

        if (velocity.len() > 0) {
            velocity.nor().scl(speed);
            energia -= 2.5f * delta; // gasta energia ao andar
        }

        // Flip do sprite
        if (viradoEsquerda) {
            sprite.setFlip(true, false);
        } else {
            sprite.setFlip(false, false);
        }

        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position);
        sprite.setPosition(position.x, position.y);

        // Consumo de oxigênio
        if (!protegido) {
            oxigenio -= 4.5f * delta;
        }

        // Tempo vivo
        tempoVivo += delta;

        // Limites
        if (oxigenio <= 0) {
            oxigenio = 0;
            ativo = false;
        }
        if (energia <= 0) {
            energia = 0;
            ativo = false;
        }
    }

    public void setProtegido(boolean protegido) {
        this.protegido = protegido;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (ativo) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {}

    @Override
    public void interagir(Entidade outra) {}

    @Override
    public boolean podeInteragir() {
        return ativo && oxigenio > 15;
    }

    public float getOxigenio() { return oxigenio; }
    public float getEnergia() { return energia; }
    public float getTempoVivo() { return tempoVivo; }

    public void oxigenioRecuperada(float quantidade) {
        oxigenio = Math.min(100f, oxigenio + quantidade);
    }

    public void energiaRecuperada(float quantidade) {
        energia = Math.min(100f, energia + quantidade);
    }

    public boolean isMorto() {
        return !ativo;
    }
}
