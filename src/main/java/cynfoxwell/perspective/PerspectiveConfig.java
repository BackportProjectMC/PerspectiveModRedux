package cynfoxwell.perspective;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public class PerspectiveConfig {

    public static boolean classicMode = false;

    public static void readConfig() {
        Configuration cfg = PerspectiveMod.config;
        try {
            cfg.load();
            initConfig(cfg);
        } catch (Exception e1) {
            PerspectiveMod.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initConfig(Configuration cfg) {
        cfg.addCustomCategoryComment("perspective", "Perspective Config");
        classicMode = cfg.getBoolean("classicMode", "perspective", classicMode, "A toggle for classic (hold) mode and toggle mode.\ntrue = Classic (Hold)\nfalse = Toggle");
    }
}
