package pm.c7.perspective.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.c7.perspective.PerspectiveMod;

@Mixin(Camera.class)
public class MixinCamera {
    @Shadow
    private boolean ready;
    @Shadow
    private BlockView area;
    @Shadow
    private Entity focusedEntity;
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;
    @Shadow
    private boolean thirdPerson;
    @Shadow
    private boolean inverseView;
    @Shadow
    private float cameraY;
    @Shadow
    private float lastCameraY;

    @Inject(method="update",at=@At("HEAD"),cancellable=true)
    private void updateCamera(BlockView blockView, Entity entity, boolean thirdPerson, boolean inverseView, float center, CallbackInfo info){
        if (PerspectiveMod.INSTANCE.PERSPECTIVE_ENABLED) {
            this.ready = true;
            this.area = blockView;
            this.focusedEntity = entity;
            this.thirdPerson = thirdPerson;
            this.inverseView = inverseView;

            this.setRotation(entity.getYaw(center),entity.getPitch(center));
            this.setPos(MathHelper.lerp((double)center, entity.prevX, entity.x), MathHelper.lerp((double)center, entity.prevY, entity.y) + (double)MathHelper.lerp(center, this.lastCameraY, this.cameraY), MathHelper.lerp((double)center, entity.prevZ, entity.z));
            this.pitch = PerspectiveMod.INSTANCE.cameraPitch;
            this.yaw = PerspectiveMod.INSTANCE.cameraYaw;
            this.updateRotation();
            this.moveBy(-this.clipToSpace(4.0D), 0.0D, 0.0D);

            GlStateManager.rotatef(PerspectiveMod.INSTANCE.cameraPitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(PerspectiveMod.INSTANCE.cameraYaw + 180.0F, 0.0F, 1.0F, 0.0F);

            info.cancel();
        }
    }

    @Shadow
    protected void setRotation(float float_1, float float_2) {}
    @Shadow
    protected void setPos(double double_1, double double_2, double double_3) {}
    @Shadow
    protected void moveBy(double double_1, double double_2, double double_3) {}
    @Shadow
    protected void updateRotation() {}
    @Shadow
    private double clipToSpace(double double_1) {
        return double_1;
    }
}
