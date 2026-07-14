package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.orion.echoes.mars.EchoesMarsGame;
import entities.Astronauta;
import entities.Item;

public class GameScreen implements Screen {

    private final EchoesMarsGame game;

    private final SpriteBatch batch;

    private Astronauta astronauta;

    private OrthographicCamera camera;

    private Viewport viewport;

    private Array<Item> itens;

    private Hud hud;

    public GameScreen(EchoesMarsGame game, SpriteBatch batch){
        this.game = game;
        this.batch = batch;
    }

    @Override
    public void show() {
        astronauta = new Astronauta(200, 200);

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        itens = new Array<>();
        itens.add(new Item(400, 300, "oxigenio"));
        itens.add(new Item(800, 600, "oxigenio"));
        itens.add(new Item(600, 400, "comida"));
        itens.add(new Item(1000, 700, "comida"));
        itens.add(new Item(900, 400, "abrigo"));

        hud = new Hud();
    }

    @Override
    public void render (float delta) {
        astronauta.update(delta);

        camera.position.x += (astronauta.getPosition().x - camera.position.x) * 0.1f;
        camera.position.y += (astronauta.getPosition().y - camera.position.y) * 0.1f;
        camera.update();

        Gdx.gl.glClearColor(0.65f, 0.35f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        for (Item item : itens) {
            item.render(batch);
        }
        astronauta.render(batch);

        for (Item item : itens) {
            if (!item.isColetado() && astronauta.getBounds().overlaps(item.getBounds())){
                if (item.getTipo(). equals("oxigenio")) {
                    astronauta.oxigenioRecuperada(30);
                    item.coletar();
                } else if (item.getTipo().equals("comida")) {
                    System.out.println("Comida coletada !");
                    item.coletar();
                } else if (item.getTipo().equals("abrigo")) {
                    astronauta.oxigenioRecuperada(100);
                    System.out.println("Abrigo encontrado! Oxigênio recarregado para 100%");
                }
            }
        }
        batch.end();

        hud.render(batch, astronauta, camera.position.x, camera.position.y);
    }

    @Override
    public void resize(int width, int height) {viewport.update(width, height);}

    @Override
    public void dispose() {
        astronauta.dispose();
        hud.dispose();
        for (Item item : itens) item.dispose();
    }
    @Override public void pause () {}
    @Override public void resume () {}
    @Override public void hide () {}
}
