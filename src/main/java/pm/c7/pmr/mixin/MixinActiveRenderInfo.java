package pm.c7.pmr.mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pm.c7.pmr.PerspectiveMod;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;

    @Inject(method = "update(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ActiveRenderInfo.movePosition(DDD)V", ordinal = 0))
    private void perspectiveUpdatePitchYaw(IBlockReader area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (PerspectiveMod.getInstance().perspectiveEnabled) {
            this.pitch = PerspectiveMod.getInstance().cameraPitch;
            this.yaw = PerspectiveMod.getInstance().cameraYaw;
        }
    }

    @ModifyArgs(method = "update(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ActiveRenderInfo.setDirection(FF)V", ordinal = 0))
    private void perspectiveFixRotation(Args args) {
        if (PerspectiveMod.getInstance().perspectiveEnabled) {
            args.set(0, PerspectiveMod.getInstance().cameraYaw);
            args.set(1, PerspectiveMod.getInstance().cameraPitch);
        }
    }
}
