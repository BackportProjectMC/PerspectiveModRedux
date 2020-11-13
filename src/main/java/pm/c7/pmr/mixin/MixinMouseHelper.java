package pm.c7.pmr.mixin;

import net.minecraft.client.MouseHelper;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.pmr.PerspectiveMod;

@Mixin(MouseHelper.class)
public class MixinMouseHelper {
    @Inject(
            method = "updatePlayerLook()V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/tutorial/Tutorial.onMouseMove(DD)V"
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void perspectiveUpdatePitchYaw(CallbackInfo info, double time, double timeDelta, double sens, double adjustedSens, double x, double y, int invert) {
        PerspectiveMod.LOGGER.info("{} {} {} {} {} {} {}", time, timeDelta, sens, adjustedSens, x, y, invert);
        if (PerspectiveMod.getInstance().perspectiveEnabled) {
            PerspectiveMod.getInstance().cameraYaw += x / 8.0F;
            PerspectiveMod.getInstance().cameraPitch += (y * invert) / 8.0F;

            if (Math.abs(PerspectiveMod.getInstance().cameraPitch) > 90.0F) {
                PerspectiveMod.getInstance().cameraPitch = PerspectiveMod.getInstance().cameraPitch > 0.0F ? 90.0F : -90.0F;
            }
        }
    }

    @Redirect(
            method = "updatePlayerLook()V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/entity/player/ClientPlayerEntity.rotateTowards(DD)V"
            )
    )
    private void perspectivePreventPlayerMovement(ClientPlayerEntity player, double x, double y) {
        if (!PerspectiveMod.getInstance().perspectiveEnabled) {
            player.rotateTowards(x, y);
        }
    }
}
