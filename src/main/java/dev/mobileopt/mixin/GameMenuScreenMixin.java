package dev.mobileopt.mixin;

import dev.mobileopt.gui.OptClientScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void mobileopt$addButton(CallbackInfo ci) {
        int w = 84;
        int h = 22;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("OptClient"),
                        b -> this.client.setScreen(new OptClientScreen(this)))
                .dimensions(this.width - w - 4, 4, w, h).build());
    }
}
