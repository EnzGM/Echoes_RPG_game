package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import managers.AssetManager;
import physics.PhysicsWorld;
import save.GameSaveData;

public class Astronauta extends Entidade implements Interagivel {

    //Status
    private float oxigenio = 100f;
    private float energia = 100f;
    private float speed = 100f;
    private boolean protegido = false;
    private boolean viradoEsquerda = false;
    private float tempoVivo = 0f;

    private final Sprite sprite;
    private Body body;
    private PhysicsWorld physicsWorld;

    private final float width = 48f;
    private final float height = 64f;

    public Astronauta(float x, float y, AssetManager assets, PhysicsWorld physicsWorld) {
        super(x, y, 48f, 64f);
        this.physicsWorld = physicsWorld;

        Texture texture = new Texture(Gdx.files.internal("textures/astronauta.png"));
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);

        position.set(x, y);
        bounds.set(x, y, width, height);
        ativo = true;

        body = physicsWorld.createDynamicBody(
            x + width / 2f,
            y + height / 2f,
            width,
            height,
            "ASTRONAUTA"
        );
    }

    public void move(float dirX, float dirY, float delta) {
        if (!ativo || body == null) return;

        if (dirX < 0) viradoEsquerda = true;
        else if (dirX > 0) viradoEsquerda = false;

        sprite.setFlip(viradoEsquerda, false);

        float velocityX = dirX * speed / PhysicsWorld.PPM;
        float velocityY = dirY * speed / PhysicsWorld.PPM;

        body.setLinearVelocity(velocityX, velocityY);

        Vector2 bodyPos = body.getPosition();
        position.set(
            bodyPos.x * PhysicsWorld.PPM - width / 2f,
            bodyPos.y * PhysicsWorld.PPM - height /2f
        );

        sprite.setPosition(position.x, position.y);
        bounds.setPosition(position.x, position.y);

        if (dirX != 0 || dirY != 0) {
            energia -= 2.5f * delta;
            if (!protegido) {
                oxigenio -= 1.5f * delta;
            }
        }
    }

    @Override
    public void update(float delta) {
        if (!ativo) return;

        tempoVivo += delta;

        if (!protegido) {
            oxigenio -= 3.0f * delta;
        }

        if (oxigenio <= 0) {
            oxigenio = 0;
            ativo = false;
        }
        if (energia <= 0) {
            energia = 0;
            ativo = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (ativo && sprite != null) {
            sprite.draw(batch);
        }
    }

    public GameSaveData toSaveData( String faseAtual) {
        GameSaveData data = new GameSaveData();
        data.posX = position.x;
        data.posY = position.y;
        data.oxigenio = oxigenio;
        data.energia = energia;
        data.tempoVivo = tempoVivo;
        data.fase = faseAtual;
        data.versao = 1;
        return data;
    }

    public void fromSaveData(GameSaveData data) {
        if (data == null) return;

        position.set(data.posX, data.posY);

        if (sprite != null) {
            sprite.setPosition(data.posX, data.posY);
        }

        if (body != null) {
            body.setTransform(
                (data.posX + width / 2f) / PhysicsWorld.PPM,
                (data.posY + height / 2f) / PhysicsWorld.PPM,
                0
            );
            body.setLinearVelocity(0,0);
        }
        oxigenio = data.oxigenio;
        energia = data.energia;
        tempoVivo = data.tempoVivo;
        ativo = true;
    }

    @Override
    public void dispose() {}

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isMoving() {
        return body != null && body.getLinearVelocity().len() > 0.4f;
    }

    public void setProtegido(boolean protegido) {
        this.protegido = protegido;
    }

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
