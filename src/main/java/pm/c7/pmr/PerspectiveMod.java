package pm.c7.pmr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pm.c7.pmr.gui.ConfigGui;

@Mod(modid = PerspectiveMod.MOD_ID, version = PerspectiveMod.VERSION, acceptedMinecraftVersions = "@MOD_ACCEPTED@", name = PerspectiveMod.MOD_NAME, clientSideOnly = true, guiFactory = "pm.c7.pmr.gui.GuiFactory")
public class PerspectiveMod
{
    public static final String MOD_ID = "@MOD_ID@";
    public static final String MOD_NAME = "@MOD_NAME@";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static PerspectiveMod INSTANCE;

    public static Configuration config;

    private Minecraft client;
    private KeyBinding toggleKey;
    private boolean showConfig = false;

    private static boolean holdToUse = false;

    public boolean perspectiveEnabled = false;
    public float cameraPitch;
    public float cameraYaw;
    private boolean held = false;

    public PerspectiveMod() {
        this.client = Minecraft.getMinecraft();
        PerspectiveMod.INSTANCE = this;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new ConfigCommand());
        ClientRegistry.registerKeyBinding(this.toggleKey = new KeyBinding("Toggle Perspective", 62, "Perspective Mod Redux"));
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        saveConfig();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID)) {
            saveConfig();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.showConfig) {
            this.client.displayGuiScreen(new ConfigGui(null));
            this.showConfig = false;
        }

        if (this.client.thePlayer != null) {
            if (!this.perspectiveEnabled && this.held) {
                this.held = false;

                this.client.gameSettings.thirdPersonView = 0;
            }

            if (this.perspectiveEnabled && this.client.gameSettings.thirdPersonView != 1) {
                this.perspectiveEnabled = false;
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (this.client.thePlayer != null) {
            if (holdToUse) {
                this.perspectiveEnabled = this.toggleKey.isKeyDown();

                if (this.perspectiveEnabled && !this.held) {
                    this.held = true;

                    this.cameraPitch = this.client.thePlayer.rotationPitch;
                    this.cameraYaw = this.client.thePlayer.rotationYaw + 180.0F;

                    this.client.gameSettings.thirdPersonView = 1;
                }
            } else {
                if (this.toggleKey.isPressed()) {
                    this.perspectiveEnabled = !this.perspectiveEnabled;

                    this.cameraPitch = this.client.thePlayer.rotationPitch;
                    this.cameraYaw = this.client.thePlayer.rotationYaw;

                    this.client.gameSettings.thirdPersonView = this.perspectiveEnabled ? 1 : 0;
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

    public static void saveConfig() {
        holdToUse = config.getBoolean("Hold To Use", "main", false, null);

        config.save();
    }

    public class ConfigCommand extends CommandBase {

        @Override
        public String getCommandName() {
            return "perspective";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/perspective";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            showConfig = true;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
}
