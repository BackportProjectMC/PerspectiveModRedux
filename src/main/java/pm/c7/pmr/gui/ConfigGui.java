package pm.c7.pmr.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import pm.c7.pmr.PerspectiveMod;

import java.util.List;

public class ConfigGui extends GuiConfig {
    public ConfigGui(final GuiScreen parent) {
        super(parent, new ConfigElement(PerspectiveMod.config.getCategory("main")).getChildElements(), PerspectiveMod.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(PerspectiveMod.config.getConfigFile().getPath()));
    }
}
