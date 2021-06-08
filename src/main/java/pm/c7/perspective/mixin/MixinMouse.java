package pm.c7.perspective.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.perspective.PerspectiveMod;

@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(
        method = "updateMouse",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/tutorial/TutorialManager.onUpdateMouse(DD)V"
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void perspectiveUpdatePitchYaw(CallbackInfo info, double adjustedSens, double x, double y, int invert) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            PerspectiveMod.INSTANCE.cameraYaw += x / 8.0F;
            PerspectiveMod.INSTANCE.cameraPitch += (y * invert) / 8.0F;

            if (Math.abs(PerspectiveMod.INSTANCE.cameraPitch) > 90.0F) {
                PerspectiveMod.INSTANCE.cameraPitch = PerspectiveMod.INSTANCE.cameraPitch > 0.0F ? 90.0F : -90.0F;
            }
        }
    }

    @Redirect(
        method = "updateMouse",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/network/ClientPlayerEntity.changeLookDirection(DD)V"
        )
    )
    private void perspectivePreventPlayerMovement(ClientPlayerEntity player, double x, double y) {
        if (!PerspectiveMod.INSTANCE.perspectiveEnabled) {
            player.changeLookDirection(x, y);
        }
    }
}
