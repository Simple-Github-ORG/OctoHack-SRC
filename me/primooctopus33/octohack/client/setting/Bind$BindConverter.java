package me.primooctopus33.octohack.client.setting;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.primooctopus33.octohack.client.setting.Bind;
import org.lwjgl.input.Keyboard;

public class Bind$BindConverter
extends Converter<Bind, JsonElement> {
    @Override
    public JsonElement doForward(Bind bind) {
        return new JsonPrimitive(bind.toString());
    }

    @Override
    public Bind doBackward(JsonElement jsonElement) {
        String s = jsonElement.getAsString();
        if (s.equalsIgnoreCase("None")) {
            return Bind.none();
        }
        int key = -1;
        try {
            key = Keyboard.getKeyIndex(s.toUpperCase());
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (key == 0) {
            return Bind.none();
        }
        return new Bind(key);
    }
}
