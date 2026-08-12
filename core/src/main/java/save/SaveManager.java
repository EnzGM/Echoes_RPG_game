package save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.Preferences;

public class SaveManager {

    private static final String PREFS_NAME = "echoes_mars_save";
    private static final String KEY_HAS_SAVE = "has_save";
    private static final String KEY_SAVE_DATA = "save_data_json";
    private static final String KEY_LAST_SAVE_TIME = "last_save_time";
    private static final String KEY_VERSION = "save_version";

    private final Preferences prefs;

    private final Json json;

    public SaveManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setTypeName(null);
    }

    public void save(GameSaveData data) {
        try {
            String jsonString = json.toJson(data);

            prefs.putBoolean(KEY_HAS_SAVE, true);
            prefs.putString(KEY_SAVE_DATA, jsonString);
            prefs.putLong(KEY_LAST_SAVE_TIME, System.currentTimeMillis());
            prefs.putInteger(KEY_VERSION, data.versao);
            prefs.flush();

            Gdx.app.log("SaveManager", "Jogo salvo com sucesso!");
            Gdx.app.log("SaveManager", jsonString);
        } catch ( Exception e) {
            Gdx.app.error("SaveManager", "Erro ao salvar: " + e.getMessage());
        }
    }

    public GameSaveData load() {
        if (!hasSave()) {
            Gdx.app.log( "SaveManager", "Nenhum save encontrado.");
            return null;
        }
        try {
            String jsonString = prefs.getString(KEY_SAVE_DATA, null);
            if (jsonString == null || jsonString.isEmpty()) {
                return null;
            }
            GameSaveData data = json.fromJson(GameSaveData.class, jsonString);
            Gdx.app.log("SaveManager", "Save carregado: " + data);
            return data;
        }catch ( Exception e) {
            Gdx.app.error("SaveManager", "Erro ao carregar save: " + e.getMessage());
            return null;
        }
    }

    public boolean hasSave() {
        return prefs.getBoolean(KEY_HAS_SAVE, false);
    }
    public String getLastSaveTime() {
        long time = prefs.getLong(KEY_LAST_SAVE_TIME, 0);
        if (time == 0) return  "Nunca";

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return  sdf.format(new java.util.Date(time));
    }

    public void deleteSave() {
        prefs.clear();
        prefs.flush();
        Gdx.app.log("SaveManager", "Save apagado.");
    }

    public GameSaveData createNewGamerData() {
        return new GameSaveData(200f, 200f, 100f, 100f, 0f,"MARTE" );
    }

}
