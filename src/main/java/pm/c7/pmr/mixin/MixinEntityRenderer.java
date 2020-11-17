package pm.c7.pmr.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.pmr.PerspectiveMod;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    private Minecraft mc;

    @Inject(method = "updateCameraAndRender",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V",
            ordinal = 0
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void perspectiveCameraUpdatingSmooth(float partialTicks, long time, CallbackInfo info, boolean flag, float sens, float adjustedSens, float x, float y, int invert, float delta) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            PerspectiveMod.INSTANCE.cameraYaw += x / 8.0F;
            PerspectiveMod.INSTANCE.cameraPitch += (y * invert) / 8.0F;

            if (Math.abs(PerspectiveMod.INSTANCE.cameraPitch) > 90.0f) {
                PerspectiveMod.INSTANCE.cameraPitch = PerspectiveMod.INSTANCE.cameraPitch > 0.0f ? 90.0f : -90.0f;
            }
        }
    }

    @Inject(method = "updateCameraAndRender",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V",
            ordinal = 1
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void perspectiveCameraUpdatingNormal(float partialTicks, long time, CallbackInfo info, boolean flag, float sens, float adjustedSens, float x, float y, int invert) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            PerspectiveMod.INSTANCE.cameraYaw += x / 8.0F;
            PerspectiveMod.INSTANCE.cameraPitch += (y * invert) / 8.0F;

            if (Math.abs(PerspectiveMod.INSTANCE.cameraPitch) > 90.0f) {
                PerspectiveMod.INSTANCE.cameraPitch = PerspectiveMod.INSTANCE.cameraPitch > 0.0f ? 90.0f : -90.0f;
            }
        }
    }

    @Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V"))
    private void perspectivePreventMovement(EntityPlayerSP player, float x, float y) {
        if (!PerspectiveMod.INSTANCE.perspectiveEnabled) {
            player.setAngles(x, y);
        }
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 1))
    private float perspectiveCameraYaw(float orig) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            return PerspectiveMod.INSTANCE.cameraYaw;
        }

        return orig;
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 2))
    private float perspectiveCameraPitch(float orig) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            return PerspectiveMod.INSTANCE.cameraPitch;
        }

        return orig;
    }
}
