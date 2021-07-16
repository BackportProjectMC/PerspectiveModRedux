package pm.c7.perspective;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class PerspectiveMod implements ClientModInitializer {
    public static final String MOD_ID = "perspectivemod";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static PerspectiveMod INSTANCE;

    private static final String KEYBIND_CATEGORY = "key.perspectivemod.category";
    private static final String TOGGLE_KEYBIND = "key.perspectivemod.toggle";

    private static KeyBinding toggleKey;

    private MinecraftClient client;

    public PerspectiveConfig config;

    public boolean perspectiveEnabled;
    public float cameraPitch;
    public float cameraYaw;
    private boolean held = false;

    public PerspectiveMod(){
        this.client = MinecraftClient.getInstance();
        this.perspectiveEnabled = false;
        PerspectiveMod.INSTANCE = this;
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(
                PerspectiveConfig.class,
                PartitioningSerializer.wrap(JanksonConfigSerializer::new)
        );

        this.config = AutoConfig.getConfigHolder(PerspectiveConfig.class).getConfig();

        KeyBindingRegistryImpl.addCategory(KEYBIND_CATEGORY);
        KeyBindingRegistryImpl.registerKeyBinding(toggleKey = new KeyBinding(TOGGLE_KEYBIND, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, KEYBIND_CATEGORY));

        ClientTickEvents.START_CLIENT_TICK.register(e -> {
            if (this.client.player != null) {
                if (config.main.holdMode) {
                    this.perspectiveEnabled = toggleKey.isPressed();

                    if (this.perspectiveEnabled && !this.held) {
                        this.held = true;
                        this.cameraPitch = this.client.player.getPitch();

                        if (config.main.lookForwards) {
                            this.cameraYaw = this.client.player.getYaw();
                        } else {
                            this.cameraYaw = this.client.player.getYaw() - 180.0F;
                        }

                        this.client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                    }
                } else {
                    if (toggleKey.wasPressed()) {
                        this.perspectiveEnabled = !this.perspectiveEnabled;

                        this.cameraPitch = this.client.player.getPitch();

                        if (config.main.lookForwards) {
                            this.cameraYaw = this.client.player.getYaw();
                        } else {
                            this.cameraYaw = this.client.player.getYaw() - 180.0F;
                        }

                        this.client.options.setPerspective(this.perspectiveEnabled ? Perspective.THIRD_PERSON_BACK : Perspective.FIRST_PERSON);
                    }
                }

                if (!this.perspectiveEnabled && this.held) {
                    this.held = false;
                    this.client.options.setPerspective(Perspective.FIRST_PERSON);
                }

                if (this.perspectiveEnabled && this.client.options.getPerspective() != Perspective.THIRD_PERSON_BACK) {
                    this.perspectiveEnabled = false;
                }
            }
        });
    }
}