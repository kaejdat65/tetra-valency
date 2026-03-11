import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.td.game.systems.SaveData;

public class TestJson {
    public static void main(String[] args) {
        SaveData data = new SaveData();
        data.staffOrb = "FIRE";
        System.out.println(data.toJson());
    }
}
