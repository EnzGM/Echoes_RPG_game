package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.orion.echoes.EchoesMarsGame;
import entities.Astronauta;
import entities.Item;
import managers.AssetManager;
import managers.ParticleManager;

import java.lang.reflect.Parameter;

public class GameScreen implements Screen {
    private final EchoesMarsGame game;
    private final SpriteBatch batch;
    private final AssetManager assets;
    private Astronauta astronauta;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Array<Item> itens;
    private Hud hud;
    private ParticleManager particleManager;
    private float poeiraTimer = 0f;
    private float faiscaTimer = 0f;
    private float alertaTimer = 0f;
    private Item abrigo = null;
    private boolean pausado = false;
    private final float TEMPO_VITORIA = 60f; // 60 segundos para vencer

    public GameScreen(EchoesMarsGame game, SpriteBatch batch, AssetManager assets) {
        this.game = game;
        this.batch = batch;
        this.assets = assets;
    }

    @Override
    public void show() {
        astronauta = new Astronauta(200, 200, assets);
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        itens = new Array<>();
        itens.add(new Item(400, 300, "oxigenio", assets));
        itens.add(new Item(800, 600, "oxigenio", assets));
        itens.add(new Item(600, 469, "comida", assets));
        itens.add(new Item(1000, 700, "comida", assets));
        itens.add(new Item(300, 500, "comida", assets));
        itens.add(new Item(700, 200, "oxigenio", assets));
        itens.add(new Item(900, 350, "abrigo", assets));

        for (Item item : itens) {
            if (item.getTipo().equals("abrigo")){
                abrigo = item;
                break;
            }
        }

        hud = new Hud(assets);
        pausado = false;

        particleManager = new ParticleManager();
        particleManager.loadEffect("poeira", "particles/poeira.p", "particles/");
        particleManager.loadEffect("faisca", "particles/faisca.p", "particles/");
        particleManager.loadEffect("alerta", "particles/alerta_oxigenio.p", "particles/");
        particleManager.loadEffect("coleta", "particles/coleta.p", "particles/");
        particleManager.loadEffect("explosao", "particles/explosao.p", "particles/");
        particleManager.loadEffect("rastro", "particles/rastro.p", "particles/");
    }

    @Override
    public void render(float delta) {
        // Pausa com ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pausado = !pausado;
        }

        if (pausado) {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            assets.font.getData().setScale(2.5f);
            assets.font.setColor(1f, 1f, 0.6f, 1f);
            assets.font.draw(batch, "PAUSADO", 540, 400);
            assets.font.getData().setScale(1.3f);
            assets.font.setColor(0.8f, 0.8f, 0.7f, 1f);
            assets.font.draw(batch, "Pressione ESC para continuar", 470, 330);
            batch.end();
            return;
        }

        astronauta.update(delta);
        particleManager.update(delta);

        // Verifica morte
        if (astronauta.isMorto()) {
            particleManager.play("explosao",
                astronauta.getPosition().x + 16,
                astronauta.getPosition().y + 20,
                1.5f);

            String motivo = astronauta.getOxigenio() <= 0 ?
                "Voce morreu por falta de Oxigenio!" :
                "Voce morreu por falta de Energia!";
            game.setScreen(new GameOverScreen(game, batch, assets, motivo));
            return;
        }

        // Verifica vitória (60 segundos)
        if (astronauta.getTempoVivo() >= TEMPO_VITORIA) {
            game.setScreen(new VictoryScreen(game, batch, assets, astronauta.getTempoVivo()));
            return;
        }

        // Câmera
        camera.position.x += (astronauta.getPosition().x - camera.position.x) * 0.1f;
        camera.position.y += (astronauta.getPosition().y - camera.position.y) * 0.1f;
        camera.update();

        // Fundo
        Gdx.gl.glClearColor(0.65f, 0.35f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Item item : itens) {
            item.render(batch);
        }

        astronauta.render(batch);

        particleManager.render(batch);

        // Colisão
        boolean dentroDoAbrigo = false;
        for (Item item : itens) {
            if (!item.isColetado() && astronauta.getBounds().overlaps(item.getBounds())) {

                particleManager.play("coleta",
                    item.getPosition().x + 16,
                    item.getPosition().y + 16,
                    1.1f);

                if (item.getTipo().equals("oxigenio")) {
                    astronauta.oxigenioRecuperada(30);
                    item.coletar();
                } else if (item.getTipo().equals("comida")) {
                    astronauta.energiaRecuperada(40);
                    item.coletar();
                } else if (item.getTipo().equals("abrigo")) {
                    dentroDoAbrigo = true;
                    astronauta.oxigenioRecuperada(100);
                }
            }
        }
        astronauta.setProtegido(dentroDoAbrigo);

        batch.end();

        if (astronauta.isMoving()) {
            poeiraTimer += delta;
            if (poeiraTimer >= 0.08f) {
                particleManager.play("poeira",
                    astronauta.getPosition().x + 16,
                    astronauta.getPosition().y + 40,
                    0.9f);
                alertaTimer = 0f;
            }
        }else {
            alertaTimer = 0f;
        }
        if (astronauta.isMoving() && astronauta.getEnergia() < 40f) {
            particleManager.play("rastro",
                astronauta.getPosition().x + 16,
                astronauta.getPosition().y + 10,
                0.6f);
        }

        // HUD
        hud.render(batch, astronauta, camera.position.x, camera.position.y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        if (astronauta != null) astronauta.dispose();
        if (hud != null) hud.dispose();
        if (particleManager != null) particleManager.dispose();
        for (Item item : itens) {
            if (item != null) item.dispose();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
