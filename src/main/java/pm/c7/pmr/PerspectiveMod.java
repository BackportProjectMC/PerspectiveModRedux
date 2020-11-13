package pm.c7.pmr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import static net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import static net.minecraftforge.common.ForgeConfigSpec.Builder;

@Mod("pmr")
public class PerspectiveMod {
    public static final Logger LOGGER = LogManager.getLogger("PerspectiveModRedux");

    private static final Builder CONFIG_BUILDER = new Builder();
    private static final CategoryGeneral GENERAL = new CategoryGeneral();

    public static final class CategoryGeneral {
        public final BooleanValue holdToUse;

        private CategoryGeneral() {
            CONFIG_BUILDER.comment("General mod settings").push("general");

            holdToUse = CONFIG_BUILDER.comment("Whether to hold to use perspective").define("Hold To Use", false);

            CONFIG_BUILDER.pop();
        }
    }

    public static final ForgeConfigSpec CONFIG = CONFIG_BUILDER.build();

    private static PerspectiveMod INSTANCE = null;

    public static PerspectiveMod getInstance() {
        assert INSTANCE != null;
        return INSTANCE;
    }

    private Minecraft client;
    private KeyBinding toggleKey;

    public boolean perspectiveEnabled = false;
    public float cameraPitch;
    public float cameraYaw;

    private boolean held = false;

    public PerspectiveMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIG);

        eventBus.addListener(this::init);
    }

    private void init(final FMLClientSetupEvent event) {
        INSTANCE = (PerspectiveMod)ModLoadingContext.get().getActiveContainer().getMod();
        this.client = Minecraft.getInstance();

        ClientRegistry.registerKeyBinding(toggleKey = new KeyBinding("key.pmr.toggle", GLFW.GLFW_KEY_F4, "key.pmr.category"));

        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (this.client != null && this.client.player != null) {
            if (!this.perspectiveEnabled && this.held) {
                this.held = false;

                this.client.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
            }

            if (this.perspectiveEnabled && this.client.gameSettings.getPointOfView() != PointOfView.THIRD_PERSON_BACK) {
                this.perspectiveEnabled = false;
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (this.client != null && this.client.player != null) {
            if (GENERAL.holdToUse.get()) {
                this.perspectiveEnabled = this.toggleKey.isKeyDown();

                if (this.perspectiveEnabled && !this.held) {
                    this.held = true;

                    this.cameraPitch = this.client.player.rotationPitch;
                    this.cameraYaw = this.client.player.rotationYaw + 180.0F;

                    this.client.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
                }
            } else {
                if (this.toggleKey.isPressed()) {
                    this.perspectiveEnabled = !this.perspectiveEnabled;

                    this.cameraPitch = this.client.player.rotationPitch;
                    this.cameraYaw = this.client.player.rotationYaw + 180.0F;

                    this.client.gameSettings.setPointOfView(this.perspectiveEnabled ? PointOfView.THIRD_PERSON_BACK : PointOfView.FIRST_PERSON);
                }
            }
        }
    }

    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (this.perspectiveEnabled) {
            event.setPitch(this.cameraPitch);
            event.setYaw(this.cameraYaw);
        }
    }
}
