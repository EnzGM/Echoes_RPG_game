package screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entities.Astronauta;

public class Hud {
    private final BitmapFont font;

    public Hud() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData(). setScale(1.6f);
    }

    public void render(SpriteBatch batch, Astronauta astronauta, float cameraX, float cameraY) {
        batch.begin();

        float hudX = cameraX - 300;
        float hudY = cameraY + 280;

        font.draw(batch, "ECHOES em Marte - Survival RPG", hudX, hudY + 50);
        font.draw(batch, "Base Orion - Marte", hudX, hudY + 20);

        font.draw(batch, "Oxigênio" + (int) astronauta.getOxigenio() + "%", hudX, hudY - 20);

        if (astronauta.getOxigenio() < 30) {
            font. setColor(Color.RED);
            font.draw(batch, "OXIGÊNIO CRÍTICO!", hudX, hudY - 50);
            font.setColor(Color.WHITE);
        }
        batch.end();
    }
    public void dispose() {font.dispose();}
}
