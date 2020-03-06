package pm.c7.perspective;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class PerspectiveMod implements ClientModInitializer {
    public static final String MOD_ID = "perspectivemod";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static PerspectiveMod INSTANCE;

    private static final String KEYBIND_CATEGORY = "key.perspectivemod.category";
    private static final Identifier TOGGLE_KEYBIND = new Identifier(MOD_ID, "toggle");

    private static FabricKeyBinding toggleKey;

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

        KeyBindingRegistryImpl.INSTANCE.addCategory(KEYBIND_CATEGORY);
        KeyBindingRegistryImpl.INSTANCE.register(toggleKey = FabricKeyBinding.Builder.create(TOGGLE_KEYBIND, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, KEYBIND_CATEGORY).build());

        ClientTickCallback.EVENT.register(e -> {
            if (config.main.holdMode) {
                this.perspectiveEnabled = toggleKey.isPressed();

                if (this.perspectiveEnabled && !this.held) {
                    this.held = true;
                    this.cameraPitch = this.client.player.pitch;
                    this.cameraYaw = this.client.player.yaw;
                } else if(!this.perspectiveEnabled) {
                    this.held = false;
                }

                this.client.options.perspective = this.perspectiveEnabled ? 1 : 0;
            } else {
                if (toggleKey.wasPressed()) {
                    this.perspectiveEnabled = !this.perspectiveEnabled;

                    this.cameraPitch = this.client.player.pitch;
                    this.cameraYaw = this.client.player.yaw;

                    this.client.options.perspective = this.perspectiveEnabled ? 1 : 0;
                }
            }

            if (this.perspectiveEnabled && this.client.options.perspective != 1){
               this.perspectiveEnabled = false;
            }
        });
    }
}