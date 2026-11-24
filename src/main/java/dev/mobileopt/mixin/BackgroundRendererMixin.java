package dev.mobileopt.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mobileopt.OptConfig;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void mobileopt$noFog(Camera camera, BackgroundRenderer.FogType fogType,
                                        float viewDistance, boolean thickFog, float tickDelta,
                                        CallbackInfo ci) {
        if (!OptConfig.noFog) return;
        var submersion = camera.getSubmersionType();
        if (submersion != null && !"NONE".equals(submersion.name())) return;
        RenderSystem.setShaderFogStart(1.0E7F);
        RenderSystem.setShaderFogEnd(2.0E7F);
    }
}
