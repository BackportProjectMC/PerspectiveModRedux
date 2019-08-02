package pm.c7.perspective.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.GlfwUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.c7.perspective.PerspectiveMod;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow
    private MinecraftClient client;
    @Shadow
    private double cursorDeltaX;
    @Shadow
    private double cursorDeltaY;
    @Shadow
    private double field_1785 = Double.MIN_VALUE;
    @Shadow
    private boolean isCursorLocked;

    @Inject(method="updateMouse",at=@At("HEAD"),cancellable=true)
    private void noPlayerRotation(CallbackInfo info) {
        double time = GlfwUtil.getTime();
        double min = time - this.field_1785;
        this.field_1785 = time;
        if (this.isCursorLocked() && this.client.isWindowFocused()) {
            if (PerspectiveMod.INSTANCE.PERSPECTIVE_ENABLED) {
                double sens = this.client.options.mouseSensitivity * 0.6000000238418579D + 0.20000000298023224D;
                double mult = sens * sens * sens * 8.0D;
                double deltaX = this.cursorDeltaX * mult;
                double deltaY = this.cursorDeltaY * mult;

                PerspectiveMod.INSTANCE.cameraYaw += deltaX / 8.0D;
                PerspectiveMod.INSTANCE.cameraPitch += deltaY / 8.0D;

                if (Math.abs(PerspectiveMod.INSTANCE.cameraPitch) > 90.0f) {
                    PerspectiveMod.INSTANCE.cameraPitch = (PerspectiveMod.INSTANCE.cameraPitch > 0.0F) ? 90.0F : -90.0F;
                }

                this.cursorDeltaX = 0.0D;
                this.cursorDeltaY = 0.0D;
                int yDir = 1;
                if (this.client.options.invertYMouse) {
                    yDir = -1;
                }

                /*this.client.getTutorialManager().onUpdateMouse(deltaX, deltaY);
                if (this.client.player != null) {
                    this.client.player.changeLookDirection(deltaX, deltaY * (double)yDir);
                }*/

                info.cancel();
            }
        }
    }

    @Shadow
    public boolean isCursorLocked() {
        return this.isCursorLocked;
    }
}
