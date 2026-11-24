package dev.mobileopt.mixin;

import dev.mobileopt.OptConfig;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin {
    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Inject(method = "update", at = @At("TAIL"))
    private void mobileopt$fullbright(float delta, CallbackInfo ci) {
        if (!OptConfig.fullbright) return;
        NativeImage image = this.texture.getImage();
        if (image == null) return;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                image.setColor(x, y, 0xFFFFFFFF);
            }
        }
        this.texture.upload();
    }
}
