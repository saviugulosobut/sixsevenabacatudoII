package dev.mobileopt.gui;

import dev.mobileopt.OptConfig;
import dev.mobileopt.SpUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class OptClientScreen extends Screen {
    private final Screen parent;
    private Text status = Text.empty();
    private int leftX;
    private int rightX;
    private int colW;
    private boolean singleplayer;

    public OptClientScreen(Screen parent) {
        super(Text.literal("OptClient"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        singleplayer = this.client != null && this.client.isInSingleplayer();

        colW = Math.min(150, (this.width - 24) / 2);
        leftX = this.width / 2 - colW - 4;
        rightX = this.width / 2 + 4;
        int top = 38;
        int step = Math.max(21, Math.min(24, (this.height - top - 30) / 7));

        toggle(leftX, top, "Sem HurtCam", () -> OptConfig.noHurtCam, v -> OptConfig.noHurtCam = v);
        toggle(leftX, top + step, "Sem Névoa", () -> OptConfig.noFog, v -> OptConfig.noFog = v);
        toggle(leftX, top + step * 2, "Sem Vinheta", () -> OptConfig.noVignette, v -> OptConfig.noVignette = v);
        toggle(leftX, top + step * 3, "Sem Partíc. Poção", () -> OptConfig.noPotionParticles, v -> OptConfig.noPotionParticles = v);
        toggle(leftX, top + step * 4, "Fullbright", () -> OptConfig.fullbright, v -> OptConfig.fullbright = v);

        this.addDrawableChild(ButtonWidget.builder(distLabel("Mobs", OptConfig.mobDistance), b -> {
            OptConfig.mobDistance = OptConfig.nextDistance(OptConfig.mobDistance);
            OptConfig.save();
            b.setMessage(distLabel("Mobs", OptConfig.mobDistance));
        }).dimensions(leftX, top + step * 5, colW, 20).build());

        this.addDrawableChild(ButtonWidget.builder(distLabel("Itens", OptConfig.itemDistance), b -> {
            OptConfig.itemDistance = OptConfig.nextDistance(OptConfig.itemDistance);
            OptConfig.save();
            b.setMessage(distLabel("Itens", OptConfig.itemDistance));
        }).dimensions(leftX, top + step * 6, colW, 20).build());

        ButtonWidget dup = this.addDrawableChild(ButtonWidget.builder(Text.literal("Duplicar item (x64)"),
                b -> status = SpUtil.duplicateMainHand(this.client).message())
                .dimensions(rightX, top, colW, 20).build());
        dup.active = singleplayer;

        ButtonWidget catalog = this.addDrawableChild(ButtonWidget.builder(Text.literal("Catálogo de Itens"),
                b -> this.client.setScreen(new ItemCatalogScreen(this)))
                .dimensions(rightX, top + step, colW, 20).build());
        catalog.active = singleplayer;

        ButtonWidget blocks = this.addDrawableChild(ButtonWidget.builder(Text.literal("Blocos Técnicos"),
                b -> this.client.setScreen(new BlockPlacerScreen(this)))
                .dimensions(rightX, top + step * 2, colW, 20).build());
        blocks.active = singleplayer;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Concluído"), b -> close())
                .dimensions(this.width / 2 - 60, this.height - 26, 120, 20).build());
    }

    private ButtonWidget toggle(int x, int y, String name, BooleanSupplier get, Consumer<Boolean> set) {
        return this.addDrawableChild(ButtonWidget.builder(toggleLabel(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            OptConfig.save();
            b.setMessage(toggleLabel(name, get.getAsBoolean()));
        }).dimensions(x, y, colW, 20).build());
    }

    private static Text toggleLabel(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "§aON" : "§cOFF"));
    }

    private static Text distLabel(String name, int dist) {
        return Text.literal("Dist. " + name + ": " + (dist == 0 ? "sem limite" : dist + " bl."));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§lOtimizações"), leftX + colW / 2, 27, 0xFFFFFF);
        Text right = singleplayer ? Text.literal("§lSingleplayer") : Text.literal("§lSingleplayer §8(indisponível)");
        context.drawCenteredTextWithShadow(this.textRenderer, right, rightX + colW / 2, 27, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, status, this.width / 2, this.height - 38, 0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
