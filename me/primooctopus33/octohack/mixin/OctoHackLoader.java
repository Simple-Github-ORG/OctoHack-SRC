package me.primooctopus33.octohack.mixin;

import java.util.Map;
import me.primooctopus33.octohack.OctoHack;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class OctoHackLoader
implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public OctoHackLoader() {
        OctoHack.LOGGER.info("\n\nLoading mixins by Primooctopus33");
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.octohack.json", "mixins.baritone.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        OctoHack.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
