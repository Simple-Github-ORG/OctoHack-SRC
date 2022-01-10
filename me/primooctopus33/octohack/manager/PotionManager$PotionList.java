package me.primooctopus33.octohack.manager;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.potion.PotionEffect;

public class PotionManager$PotionList {
    private final List<PotionEffect> effects = new ArrayList<PotionEffect>();

    public void addEffect(PotionEffect effect) {
        if (effect != null) {
            this.effects.add(effect);
        }
    }

    public List<PotionEffect> getEffects() {
        return this.effects;
    }
}
