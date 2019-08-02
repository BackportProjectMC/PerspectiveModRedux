package pm.c7.perspective;

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

    public boolean PERSPECTIVE_ENABLED;
    public float cameraPitch;
    public float cameraYaw;

    public PerspectiveMod(){
        this.client = MinecraftClient.getInstance();
        this.PERSPECTIVE_ENABLED = false;
        PerspectiveMod.INSTANCE = this;
    }

    @Override
    public void onInitializeClient() {
        KeyBindingRegistryImpl.INSTANCE.addCategory(KEYBIND_CATEGORY);
        KeyBindingRegistryImpl.INSTANCE.register(toggleKey = FabricKeyBinding.Builder.create(TOGGLE_KEYBIND, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, KEYBIND_CATEGORY).build());

        ClientTickCallback.EVENT.register(e -> {
           if (toggleKey.wasPressed()) {
               this.PERSPECTIVE_ENABLED = !this.PERSPECTIVE_ENABLED;

               this.cameraPitch = this.client.player.pitch;
               this.cameraYaw = this.client.player.yaw;

               this.client.options.perspective = this.PERSPECTIVE_ENABLED ? 1 : 0;
           }

           if (this.PERSPECTIVE_ENABLED && this.client.options.perspective != 1){
               this.PERSPECTIVE_ENABLED = false;
           }
        });
    }
}