package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.orion.echoes.EchoesMarsGame;
import managers.AssetManager;

public class MenuScreen implements Screen {
    private final EchoesMarsGame game;
    private final SpriteBatch batch;
    private final AssetManager assets;
    private OrthographicCamera camera;
    private BitmapFont font;
    private Rectangle botaoIniciar;
    private boolean botaoPressionado = false;

    public MenuScreen(EchoesMarsGame game, SpriteBatch batch, AssetManager assets) {
        this.game = game;
        this.batch = batch;
        this.assets = assets;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        font = assets.font;

        // Botão "Iniciar Missão" centralizado
        botaoIniciar = new Rectangle(440, 280, 400, 80);
    }

    @Override
    public void render(float delta) {
        // Fundo marrom de Marte
        Gdx.gl.glClearColor(0.55f, 0.28f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Titulo
        font.getData().setScale(2.8f);
        font.setColor(1f, 0.9f, 0.6f, 1f);
        font.draw(batch, "ECHOES em Marte", 380, 580);

        font.getData().setScale(1.8f);
        font.setColor(0.9f, 0.85f, 0.7f, 1f);
        font.draw(batch, "Survival RPG", 520, 520);

        // Subtitulo
        font.getData().setScale(1.2f);
        font.setColor(0.8f, 0.75f, 0.6f, 1f);
        font.draw(batch, "Base Orion - Missao de Sobrevivencia", 400, 460);

        // Botão
        if (botaoPressionado) {
            font.setColor(0.4f, 0.9f, 0.4f, 1f);
        } else {
            font.setColor(0.3f, 0.8f, 0.3f, 1f);
        }
        font.getData().setScale(2.0f);
        font.draw(batch, "[ INICIAR MISSAO ]", 450, 330);

        // Instruções
        font.getData().setScale(1.0f);
        font.setColor(0.7f, 0.7f, 0.6f, 1f);
        font.draw(batch, "Use WASD ou Setas para se mover", 480, 180);
        font.draw(batch, "Colete oxigenio e comida | Entre no abrigo para se proteger", 380, 150);
        font.draw(batch, "Professor: Ricardo Marcel | Orion ITAO School", 420, 80);

        batch.end();

        // Clique do mouse no botão
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if (botaoIniciar.contains(touch.x, touch.y)) {
                botaoPressionado = true;
                // Troca para a tela do jogo
                game.setScreen(new GameScreen(game, batch, assets));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1280, 720);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        // Não dispose assets aqui (são gerenciados pela Game)
    }
}
