package com.orion.echoes.mars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import screens.GameScreen;

public class EchoesMarsGame extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        setScreen(new GameScreen(this,batch));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
