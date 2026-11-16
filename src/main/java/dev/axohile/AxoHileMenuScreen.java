package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AxoHileMenuScreen extends Screen {
    
    public AxoHileMenuScreen() {
        super(Text.literal("AxoHile Menu"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Menü Başlığı
        context.drawCenteredTextWithShadow(this.textRenderer, "§b--- AXOHİLE CLIENT ---", this.width / 2, 40, 0xFFFFFF);
        
        // Hile Durumları (Başlangıçta hepsi kapalı başlar)
        String killauraStatus = AxoHileClient.killauraEnabled ? "§a[AÇIK]" : "§c[KAPALI]";
        String elytraStatus = AxoHileClient.elytraFlightEnabled ? "§a[AÇIK]" : "§c[KAPALI]";
        
        context.drawTextWithShadow(this.textRenderer, "Attack Aura (Sağ tık: Bypass/Özellikler) " + killauraStatus, this.width / 2 - 120, 90, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "Elytra Target (Sağ tık: Bypass/Hız) " + elytraStatus, this.width / 2 - 120, 130, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        
        if (mouseX >= centerX - 130 && mouseX <= centerX + 130) {
            if (mouseY >= 85 && mouseY <= 105) {
                if (button == 0) {
                    AxoHileClient.killauraEnabled = !AxoHileClient.killauraEnabled;
                } else if (button == 1) {
                    // AttackAura üzerine sağ tıklandığında alt menü/bypass ayarları açılır
                    System.out.println("AttackAura Sağ Tık Alt Menüsü Açıldı!");
                }
            } else if (mouseY >= 125 && mouseY <= 145) {
                if (button == 0) {
                    AxoHileClient.elytraFlightEnabled = !AxoHileClient.elytraFlightEnabled;
                } else if (button == 1) {
                    // ElytraTarget üzerine sağ tıklandığında alt menü açılır
                    System.out.println("ElytraTarget Sağ Tık Alt Menüsü Açıldı!");
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false; // Menü açıldığında oyun durmaz
    }
}
