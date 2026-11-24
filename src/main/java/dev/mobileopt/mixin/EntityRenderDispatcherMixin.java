package dev.mobileopt.mixin;

import dev.mobileopt.OptConfig;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(
            method = "shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Frustum;DDD)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mobileopt$limitDistance(Entity entity, Frustum frustum, double camX, double camY, double camZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        int limit;
        if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity) {
            limit = OptConfig.itemDistance;
        } else if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
            limit = OptConfig.mobDistance;
        } else {
            return;
        }
        if (limit <= 0) return;
        double max = (double) limit * limit;
        if (entity.squaredDistanceTo(camX, camY, camZ) > max) {
            cir.setReturnValue(false);
        }
    }
      }
