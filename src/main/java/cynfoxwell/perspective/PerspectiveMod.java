package cynfoxwell.perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = PerspectiveMod.MODID, name = PerspectiveMod.NAME, version = PerspectiveMod.VERSION, useMetadata = true)
public class PerspectiveMod {
    public static final String MODID = "perspectivemodredux";
    public static final String NAME = "Perspective Mod Redux";
    public static final String VERSION = "GRADLE:VERSION";
    public static Logger logger;

    private Minecraft mc;
    private KeyBinding keyBinding;
    private KeyBinding keyModeChange;

    public boolean cameraToggled;
    public float cameraYaw;
    public float cameraPitch;

    private EntityRenderer renderDefault;
    private EntityRenderer renderCustom;

    public static Configuration config;

    @Mod.Instance
    public static PerspectiveMod instance;

    public PerspectiveMod() {
        this.mc = Minecraft.getMinecraft();
        this.cameraToggled = false;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "perspectivemodredux.cfg"));
        PerspectiveConfig.readConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        this.renderDefault = this.mc.entityRenderer;
        this.renderCustom = new PerspectiveRenderer(this, this.mc, this.mc.getResourceManager());
        ClientRegistry.registerKeyBinding(this.keyBinding = new KeyBinding("Toggle Perspective", 62, "Perspective"));
        ClientRegistry.registerKeyBinding(this.keyModeChange = new KeyBinding("Change Perspective Mode", 0, "Perspective"));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (this.keyBinding.isPressed()) {
            this.cameraToggled = !this.cameraToggled;
            this.cameraYaw = this.mc.player.rotationYaw;
            this.cameraPitch = this.mc.player.rotationPitch;
            if (this.cameraToggled) {
                this.mc.gameSettings.thirdPersonView = 1;
            } else {
                this.mc.gameSettings.thirdPersonView = 0;
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (this.cameraToggled) {
            this.mc.entityRenderer = this.renderCustom;
        } else {
            this.mc.entityRenderer = this.renderDefault;
        }
    }
}
