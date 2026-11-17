package dev.axohile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class VexionMainMenuScreen extends Screen {
    public VexionMainMenuScreen() {
        super(Text.literal("Vexion Client"));
    }

    @Override
    protected void init() {
        int buttonWidth = Math.min(230, Math.max(170, this.width / 4));
        int buttonHeight = 25;
        int x = this.width - buttonWidth - Math.max(28, this.width / 12);
        int startY = Math.max(42, this.height / 2 - 82);
        int gap = 34;

        addDrawableChild(ButtonWidget.builder(Text.literal("Tek Oyunculu"),
                button -> client.setScreen(new SelectWorldScreen(this)))
                .dimensions(x, startY, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Çok Oyunculu"),
                button -> client.setScreen(new MultiplayerScreen(this)))
                .dimensions(x, startY + gap, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Ayarlar"),
                button -> client.setScreen(new OptionsScreen(this, client.options)))
                .dimensions(x, startY + gap * 2, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Dil"),
                button -> client.setScreen(new LanguageOptionsScreen(this, client.options, client.getLanguageManager())))
                .dimensions(x, startY + gap * 3, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Account Manager"),
                button -> client.setScreen(new VexionAccountManagerScreen(this)))
                .dimensions(x, startY + gap * 4, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Oyundan Çık"),
                button -> client.scheduleStop())
                .dimensions(x, startY + gap * 5 + 8, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        VexionBackgroundRenderer.render(context, this.width, this.height);

        int left = Math.max(28, this.width / 12);
        int logoY = this.height / 2 - 86;

        // Neon parlama katmanları
        context.drawText(this.textRenderer, "VC", left - 3, logoY - 5, 0x553A9CFF, true);
        context.drawText(this.textRenderer, "VC", left - 2, logoY - 4, 0x9956B8FF, true);
        context.drawText(this.textRenderer, "VC", left, logoY - 2, 0xFFBDEBFF, true);
        context.drawText(this.textRenderer, "VEXION CLIENT", left + 2, logoY + 38, 0xFFE7F2FF, true);
        context.drawText(this.textRenderer, "Minecraft 1.21.4", left + 2, logoY + 55, 0xFF91A9D4, true);
        context.fill(left + 2, logoY + 73, left + 150, logoY + 75, 0xFF4D9DFF);
        context.drawText(this.textRenderer, "CUSTOM CLIENT", left + 2, logoY + 88, 0xFF8298C4, true);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        // Ana menüden ESC ile çıkış ekranına dönülmez.
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
