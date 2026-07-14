package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;


public class Astronauta extends  Entidade implements Interagivel {

    private float oxigenio = 100f;

    private float speed = 200f;

    private final Sprite sprite;

    private boolean protegido = false;


    public Astronauta(float x, float y){
        super(x, y, 32, 40);
        Texture texture = new Texture(Gdx.files.internal("textures/astronauta.png"));
        sprite = new Sprite(texture);
        sprite.setSize(32,48);
    }

    @Override
    public void update(float delta) {
        velocity.set(0,0);

        if (Gdx.input.isKeyPressed(Input.Keys.W) ||Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.y = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S) ||Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.y = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A) ||Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.x = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D) ||Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.x = speed;

        if (velocity.len() > 0){
            velocity.nor() .scl(speed);
        }

        position.add(velocity.x *delta, velocity.y * delta);
        bounds.setPosition(position);
        sprite.setPosition(position.x, position.y);

        if (!protegido) {
            oxigenio -= 5f * delta;
            if (oxigenio <= 0) ativo = false;
        }
    }



    public  void setProtegido(boolean protegido) {this.protegido = protegido;}

    @Override
    public  void render (SpriteBatch batch) {sprite.draw(batch);}

    @Override
    public void dispose() {sprite.getTexture().dispose();}

    @Override
    public void interagir(Entidade outra) {  }

    @Override
    public boolean podeInteragir() {return ativo && oxigenio > 15; }

    public float getOxigenio() {return oxigenio;}

    public void oxigenioRecuperada(float quantidade) {oxigenio = Math.min(100f, oxigenio + quantidade);}


}
