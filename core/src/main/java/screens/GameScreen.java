package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.orion.echoes.EchoesMarsGame;
import entities.Astronauta;
import entities.Item;
import events.GameEvent;
import managers.AssetManager;
import managers.ParticleManager;
import entities.Obstacle;
import entities.Portal;
import events.EventBus;
import events.EventType;
import input.GameInputProcessor;
import managers.AssetManager;
import managers.ParticleManager;
import physics.PhysicsWorld;

public class GameScreen implements Screen, EventBus.EventListener {
    private final EchoesMarsGame game;
    private final SpriteBatch batch;
    private final AssetManager assets;
    private Astronauta astronauta;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Portal portal;
    private PhysicsWorld physicsWorld;
    private GameInputProcessor inputProcessor;
    private EventBus eventBus;
    private Array<Item> itens;
    private Array<Obstacle> obstacles;
    private Hud hud;
    private ParticleManager particleManager;
    private String currentPhase = "MARTE";
    private float obstacleCooldown = 0f;
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

        physicsWorld = new PhysicsWorld();
        physicsWorld.setPhaseGravity(currentPhase);

        inputProcessor = new GameInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        eventBus = EventBus.getInstance();
        eventBus.subscribe(EventType.PLAYER_COLLIDED_OBSTACLE, this);
        eventBus.subscribe(EventType.PLAYER_COLLIDED_WALL, this);
        eventBus.subscribe(EventType.PORTAL_ENTERED, this);
        eventBus.subscribe(EventType.PLAYER_DIED, this);

        astronauta = new Astronauta(200, 200, assets, physicsWorld);
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        itens = new Array<>();
        itens.add(new Item(400, 300, "oxigenio", assets));
        itens.add(new Item(800, 600, "oxigenio", assets));
        itens.add(new Item(600, 400, "comida", assets));
        itens.add(new Item(1000, 700, "comida", assets));
        itens.add(new Item(300, 500, "comida", assets));
        itens.add(new Item(700, 200, "oxigenio", assets));
        itens.add(new Item(900, 350, "abrigo", assets));

        obstacles = new Array<>();
        Texture obstacleTex = new Texture(Gdx.files.internal("textures/obstacle.png"));

        obstacles.add(new Obstacle(300, 120, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(420, 180, obstacleTex, physicsWorld));

        obstacles.add(new Obstacle(600, 100, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(600, 280, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(780, 160, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(780, 340, obstacleTex, physicsWorld));

        obstacles.add(new Obstacle(980, 120, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(980, 300, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1150, 200, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1150, 380, obstacleTex, physicsWorld));

        obstacles.add(new Obstacle(1250, 120, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1250, 450, obstacleTex, physicsWorld));

        Texture portalTex = new Texture(Gdx.files.internal("textures/portal.png"));
        portal = new Portal(1700, 320, portalTex, "LUA", physicsWorld);

        for (Item item : itens) {
            if (item.getTipo().equals("abrigo")) {
                abrigo = item;
                break;
            }
        }
        hud = new Hud(assets);
        pausado = false;

        // Particle System
        particleManager = new ParticleManager();
        particleManager.loadEffect("poeira", "particles/poeira.p", "particles/");
        particleManager.loadEffect("faisca", "particles/faisca.p", "particles/");
        particleManager.loadEffect("alerta", "particles/alerta_oxigenio.p", "particles/");
        particleManager.loadEffect("coleta", "particles/coleta.p", "particles/");
        particleManager.loadEffect("explosao", "particles/explosao.p", "particles/");
        particleManager.loadEffect("rastro", "particles/rastro.p", "particles/");

        physicsWorld.createStaticBody(640, 15, 1600, 30, "WALL");
        physicsWorld.createStaticBody(-20, 360, 40, 800, "WALL");
        physicsWorld.createStaticBody(1620, 360, 40, 800, "WALL");
    }

    @Override
    public void render(float delta) {
        // Pausa com ESC
        if (inputProcessor.isPauseJustPressed()) {
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

        if (obstacleCooldown > 0f) {
            obstacleCooldown -= delta;
        }

        physicsWorld.update(delta);

        Vector2 dir = inputProcessor.getDirection();
        astronauta.move(dir.x, dir.y, delta);
        astronauta.update(delta);

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
        if (currentPhase.equals("LUA")) {
            Gdx.gl.glClearColor(0.15f, 0.15f, 0.20f, 1f);
        } else {
            Gdx.gl.glClearColor(0.65f, 0.35f, 0.2f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Item item : itens) {
            item.render(batch);
        }

        for (Obstacle obs : obstacles) {
            obs.render(batch);
        }

        if (portal != null) {
            portal.render(batch);
        }

        astronauta.render(batch);
        particleManager.render(batch);

        // Colisão
        boolean dentroDoAbrigo = false;
        for (Item item : itens) {
            if (item.isColetado()) continue;
            if (astronauta.getBounds().overlaps(item.getBounds())) {

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
                    astronauta.getPosition().y + 4);
                    poeiraTimer = 0f;
            }
        } else {
            poeiraTimer = 0f;
        }

        if (abrigo != null) {
            faiscaTimer += delta;
            float antenaX = abrigo.getPosition().x + 80;
            float antenaY = abrigo.getPosition().y + 140;
            particleManager.play("faisca", antenaX, antenaY, 0.7f);
            faiscaTimer = 0f;
        }

        if (astronauta.getOxigenio() < 30f) {
            alertaTimer += delta;
            if (alertaTimer >= 0.25f) {
                particleManager.play("alerta",
                    astronauta.getPosition().x + 16,
                    astronauta.getPosition().y + 40,
                    0.9f);
                alertaTimer = 0f;
            }
        } else {
            alertaTimer = 0f;
        }

        if (astronauta.isMoving() && astronauta.getEnergia() < 40f) {
            particleManager.play("rastro",
                astronauta.getPosition().x + 16,
                astronauta.getPosition().y + 10,
                0.6f);
        }

        hud.render(batch, astronauta, camera.position.x, camera.position.y);

        if (astronauta.isMorto()) {
            particleManager.play("explosao",
            astronauta.getPosition().x + 16,
            astronauta.getPosition().y + 20,
            1.4f);
            eventBus.publish(EventType.PLAYER_DIED);
        }

        if (astronauta.getTempoVivo() >= TEMPO_VITORIA) {
            game.setScreen(new VictoryScreen(game, batch, assets, astronauta.getTempoVivo()));
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case PLAYER_COLLIDED_OBSTACLE:
                if (obstacleCooldown <= 0f) {
                    astronauta.oxigenioRecuperada(-12f);
                    obstacleCooldown = 1.0f;
                }
                break;

            case  PLAYER_COLLIDED_WALL:
                break;

            case PORTAL_ENTERED:
                if (portal != null) {
                    changePhase(portal.getTargetPhase());
                }
                break;

            case PLAYER_DIED:
                game.setScreen(new GameOverScreen(game, batch, assets, "Voce morreu!"));
                break;
        }
    }

    private void changePhase(String newPhase) {
        Gdx.app.log("PHASE", "Mudando de " + currentPhase + " para " + newPhase);
        currentPhase = newPhase;
        physicsWorld.setPhaseGravity(newPhase);
        eventBus.publish(EventType.PHASE_CHANGED, newPhase);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        if (physicsWorld != null) physicsWorld.dispose();
        if (astronauta != null) astronauta.dispose();
        if (hud != null) hud.dispose();
        if (particleManager != null) particleManager.dispose();
        for (Item item : itens) {
            if (item != null) item.dispose();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
