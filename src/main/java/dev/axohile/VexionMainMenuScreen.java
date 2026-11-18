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
        VexionWelcomeSound.playOnce();

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
        // Fotoğraf arka planı yok: sade, koyu ve renk vurgulu tasarım.
        context.fill(0, 0, this.width, this.height, 0xFF10131F);
        context.fill(0, 0, 7, this.height, 0xFF16D9E8);
        context.fill(7, 0, 10, this.height, 0xFF8B5CFF);

        int left = Math.max(34, this.width / 14);
        int logoY = this.height / 2 - 100;

        // Büyük, yan yana ve parlak VC logosu.
        context.getMatrices().push();
        context.getMatrices().translate(left, logoY, 0);
        context.getMatrices().scale(4.0F, 4.0F, 1.0F);
        context.drawText(this.textRenderer, "V", 0, 0, 0xFF27E6FF, true);
        context.drawText(this.textRenderer, "C", 15, 0, 0xFFFF55D6, true);
        context.getMatrices().pop();

        // Sürüm ve o anki oyuncu adı.
        context.drawText(this.textRenderer, "1.21.4",
                left + 2, logoY + 48, 0xFFB8FF4D, true);

        String playerName = MinecraftClient.getInstance().getSession().getUsername();
        context.drawText(this.textRenderer, playerName,
                left + 2, logoY + 68, 0xFFFFC857, true);

        // Logo altındaki renkli vurgu çizgisi.
        context.fill(left + 2, logoY + 88, left + 118, logoY + 91, 0xFF27E6FF);
        context.fill(left + 118, logoY + 88, left + 166, logoY + 91, 0xFFFF55D6);

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
