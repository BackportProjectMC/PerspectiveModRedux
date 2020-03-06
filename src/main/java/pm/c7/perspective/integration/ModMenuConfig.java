package pm.c7.perspective.integration;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import pm.c7.perspective.PerspectiveConfig;
import pm.c7.perspective.PerspectiveMod;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ModMenuConfig implements ModMenuApi {
    @Override
    public String getModId() {
        return PerspectiveMod.MOD_ID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return PerspectiveConfig::getConfigScreen;
    }
}
