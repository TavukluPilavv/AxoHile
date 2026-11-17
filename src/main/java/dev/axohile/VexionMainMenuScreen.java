package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class VexionMainMenuScreen extends Screen {
    public VexionMainMenuScreen() { super(Text.literal("Vexion Client")); }

    @Override
    protected void init() {
        int buttonWidth = Math.min(250, Math.max(175, this.width / 4));
        int buttonHeight = 28;
        int x = this.width - buttonWidth - Math.max(24, this.width / 12);
        int startY = Math.max(34, this.height / 2 - 90);
        int gap = 35;
        addDrawableChild(brightButton("Tek Oyunculu", x, startY, buttonWidth, buttonHeight,
                b -> client.setScreen(new SelectWorldScreen(this))));
        addDrawableChild(brightButton("Çok Oyunculu", x, startY + gap, buttonWidth, buttonHeight,
                b -> client.setScreen(new MultiplayerScreen(this))));
        addDrawableChild(brightButton("Ayarlar", x, startY + gap * 2, buttonWidth, buttonHeight,
                b -> client.setScreen(new OptionsScreen(this, client.options))));
        addDrawableChild(brightButton("Dil", x, startY + gap * 3, buttonWidth, buttonHeight,
                b -> client.setScreen(new LanguageOptionsScreen(this, client.options, client.getLanguageManager()))));
        addDrawableChild(brightButton("Account Manager", x, startY + gap * 4, buttonWidth, buttonHeight,
                b -> client.setScreen(new VexionAccountManagerScreen(this))));
        addDrawableChild(brightButton("Oyundan Çık", x, startY + gap * 5, buttonWidth, buttonHeight,
                b -> client.scheduleStop()));
    }

    private ButtonWidget brightButton(String label, int x, int y, int width, int height,
                                      ButtonWidget.PressAction action) {
        return new ButtonWidget(x, y, width, height, Text.literal(label), action,
                DEFAULT_NARRATION_SUPPLIER) {
            @Override
            protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                int fill = isHovered() ? 0xFF527FC4 : 0xFF344F78;
                context.fill(getX() + 2, getY() + 3, getX() + width + 2, getY() + height + 3, 0x66000000);
                context.fill(getX(), getY(), getX() + width, getY() + height, fill);
                context.fill(getX(), getY(), getX() + width, getY() + 2, 0xFF9EDBFF);
                context.fill(getX(), getY() + height - 2, getX() + width, getY() + height, 0xFF182B49);
                context.drawCenteredTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                        getMessage(), getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFFFF);
            }
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        VexionBackgroundRenderer.render(context, width, height);
        int left = Math.max(28, width / 12);
        int logoY = height / 2 - 82;
        context.getMatrices().push();
        context.getMatrices().translate(left, logoY, 0.0);
        context.getMatrices().scale(5.0f, 5.0f, 1.0f);
        context.drawTextWithShadow(textRenderer, Text.literal("V"), 0, 0, 0xFF249BFF);
        context.drawTextWithShadow(textRenderer, Text.literal("C"), 12, 0, 0xFFFF354D);
        context.getMatrices().pop();
        context.drawTextWithShadow(textRenderer, "VEXION CLIENT", left + 2, logoY + 42, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Minecraft 1.21.4", left + 2, logoY + 62, 0xFFFFD52E);
        context.fill(left + 2, logoY + 80, left + 164, logoY + 82, 0xFFFFD52E);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public void close() {}
    @Override public boolean shouldPause() { return false; }
}
