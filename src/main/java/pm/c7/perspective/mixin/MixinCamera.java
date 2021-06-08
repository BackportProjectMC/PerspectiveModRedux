package pm.c7.perspective.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pm.c7.perspective.PerspectiveMod;

@Mixin(Camera.class)
public class MixinCamera {
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/client/render/Camera.moveBy(DDD)V", ordinal = 0))
    private void perspectiveUpdatePitchYaw(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            this.pitch = PerspectiveMod.INSTANCE.cameraPitch;
            this.yaw = PerspectiveMod.INSTANCE.cameraYaw;
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/client/render/Camera.setRotation(FF)V", ordinal = 0))
    private void perspectiveFixRotation(Args args) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            args.set(0, PerspectiveMod.INSTANCE.cameraYaw);
            args.set(1, PerspectiveMod.INSTANCE.cameraPitch);
        }
    }
}
