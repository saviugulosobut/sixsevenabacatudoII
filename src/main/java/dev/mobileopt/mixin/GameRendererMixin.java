package dev.mobileopt.mixin;

import dev.mobileopt.OptConfig;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void mobileopt$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (OptConfig.noHurtCam) {
            ci.cancel();
        }
    }
}
