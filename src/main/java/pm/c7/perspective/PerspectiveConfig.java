package pm.c7.perspective;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;

@Config(name = "perspectivemod")
public class PerspectiveConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.TransitiveObject
    public CategoryMain main = new CategoryMain();

    @Config(name = "main")
    public static class CategoryMain implements ConfigData {
        public boolean holdMode = false;
    }

    // https://github.com/shedaniel/RoughlyEnoughItems/blob/3.x/src/main/java/me/shedaniel/rei/impl/ConfigManagerImpl.java
    // using this just to change localization strings apposed to using it for an extra button like blanket does
    @SuppressWarnings("deprecation")
    public static Screen getConfigScreen(Screen parent) {
        try {
            ConfigScreenProvider<PerspectiveConfig> provider = (ConfigScreenProvider<PerspectiveConfig>) AutoConfig.getConfigScreen(PerspectiveConfig.class, parent);
            provider.setI13nFunction(manager -> "config.perspectivemod");
            provider.setOptionFunction((baseI13n, field) -> String.format("%s.%s", baseI13n, field.getName()));
            provider.setCategoryFunction((baseI13n, categoryName) -> String.format("%s.%s", baseI13n, categoryName));

            return provider.get();
        } catch (Exception e) {
            e.printStackTrace();
            ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
            toastManager.add(new SystemToast(SystemToast.Type.PACK_LOAD_FAILURE, new LiteralText("Error loading screen"), new LiteralText("Check console for details.")));
        }

        return new Screen(new LiteralText("")) {
            @Override
            public void render(int int_1, int int_2, float float_1) {
                super.render(int_1, int_2, float_1);
            }

            @Override
            protected void init() {
                this.minecraft.openScreen(parent);
            }
        };
    }
}
