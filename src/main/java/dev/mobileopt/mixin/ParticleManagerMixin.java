package dev.mobileopt.mixin;

import dev.mobileopt.OptConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Inject(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mobileopt$noOwnPotionParticles(ParticleEffect effect, double x, double y, double z,
                                                double vx, double vy, double vz,
                                                CallbackInfoReturnable<Particle> cir) {
        if (!OptConfig.noPotionParticles) return;
        if (effect.getType() != ParticleTypes.ENTITY_EFFECT) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.squaredDistanceTo(x, y, z) < 6.25) {
            cir.setReturnValue(null);
        }
    }
}
