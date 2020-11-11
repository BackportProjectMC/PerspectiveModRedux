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
        } /*else {
            args.set(0, args.get(0));
            args.set(1, args.get(1));
        }*/
    }

    /*@Shadow
    private boolean ready;
    @Shadow
    private BlockView area;
    @Shadow
    private Entity focusedEntity;

    @Shadow
    private boolean thirdPerson;
    @Shadow
    private boolean inverseView;
    @Shadow
    private float cameraY;
    @Shadow
    private float lastCameraY;

    @Inject(method="update",at=@At("HEAD"),cancellable=true)
    private void updateCamera(BlockView area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info){
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            this.ready = true;
            this.area = area;
            this.focusedEntity = entity;
            this.thirdPerson = thirdPerson;
            this.inverseView = inverseView;

            this.setRotation(PerspectiveMod.INSTANCE.cameraYaw, PerspectiveMod.INSTANCE.cameraPitch);
            this.setPos(MathHelper.lerp((double)tickDelta, focusedEntity.prevX, focusedEntity.getX()), MathHelper.lerp((double)tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY), MathHelper.lerp((double)tickDelta, focusedEntity.prevZ, focusedEntity.getZ()));
            this.pitch = PerspectiveMod.INSTANCE.cameraPitch;
            this.yaw = PerspectiveMod.INSTANCE.cameraYaw;
            this.moveBy(-this.clipToSpace(4.0D), 0.0D, 0.0D);

            info.cancel();
        }
    }

    @Shadow
    protected void setRotation(float yaw, float pitch) {}
    @Shadow
    protected void setPos(double x, double y, double z) {}
    @Shadow
    protected void moveBy(double x, double y, double z) {}
    @Shadow
    private double clipToSpace(double desiredCameraDistance) {
        return desiredCameraDistance;
    }*/
}
