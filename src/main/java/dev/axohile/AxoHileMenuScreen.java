package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AxoHileMenuScreen extends Screen {
    private String selectedCategory = "Combat";
    private boolean subMenuOpen = false;
    private String activeSubMenuModule = "";

    public AxoHileMenuScreen() {
        super(Text.literal("AxoHile Menu"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Arka planı tamamen siyah/koyu tema yapıyoruz
        context.fill(this.width / 2 - 160, this.height / 2 - 110, this.width / 2 + 160, this.height / 2 + 110, 0xF0101010);
        
        // Sol Kategori Paneli
        context.fill(this.width / 2 - 160, this.height / 2 - 110, this.width / 2 - 90, this.height / 2 + 110, 0xF0181818);
        context.drawTextWithShadow(this.textRenderer, "§bAxoHileClient", this.width / 2 - 150, this.height / 2 - 95, 0xFFFFFF);

        // Kategoriler
        String[] categories = {"Combat", "Move", "Render", "Player", "Themes"};
        int catY = this.height / 2 - 60;
        for (String cat : categories) {
            boolean isSelected = selectedCategory.equals(cat);
            int color = isSelected ? 0x5555FFFF : 0xFFFFFF;
            context.drawTextWithShadow(this.textRenderer, (isSelected ? "> " : "  ") + cat, this.width / 2 - 150, catY, color);
            catY += 20;
        }

        // Sağ Taraf (Modüller ve Durumları)
        if (selectedCategory.equals("Combat")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Combat Modülleri]", this.width / 2 - 75, this.height / 2 - 95, 0xFFFFFF);

            // AttackAura
            String auraStatus = AxoHileClient.killauraEnabled ? "§a[AÇIK]" : "§c[KAPALI]";
            context.drawTextWithShadow(this.textRenderer, "AttackAura " + auraStatus, this.width / 2 - 75, this.height / 2 - 60, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7(Sağ tık: Bypass & FullHit)", this.width / 2 - 75, this.height / 2 - 48, 0x888888);

            // ElytraTarget
            String elytraStatus = AxoHileClient.elytraFlightEnabled ? "§a[AÇIK]" : "§c[KAPALI]";
            context.drawTextWithShadow(this.textRenderer, "ElytraTarget " + elytraStatus, this.width / 2 - 75, this.height / 2 - 20, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7(Sağ tık: Bypass & Fişek Hızı)", this.width / 2 - 75, this.height / 2 - 8, 0x888888);
        }

        // Sağ Tık Alt Menü Paneli (Eğer bir modüle sağ tıklandıysa açılır)
        if (subMenuOpen) {
            context.fill(this.width / 2 - 40, this.height / 2 - 30, this.width / 2 + 120, this.height / 2 + 70, 0xF2202020);
            context.drawTextWithShadow(this.textRenderer, "§6§l" + activeSubMenuModule + " Ayarları", this.width / 2 - 30, this.height / 2 - 20, 0xFFFFFF);

            if (activeSubMenuModule.equals("AttackAura")) {
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.attackBypassType, this.width / 2 - 30, this.height / 2 + 5, 0xAAAAAA);
                context.drawTextWithShadow(this.textRenderer, "Full Hit: " + (AxoHileClient.fullHitEnabled ? "§aAçık" : "§cKapalı"), this.width / 2 - 30, this.height / 2 + 20, 0xAAAAAA);
                context.drawTextWithShadow(this.textRenderer, "Balta Kalkan: " + (AxoHileClient.shieldBreakerAxeMode ? "§aAçık" : "§cKapalı"), this.width / 2 - 30, this.height / 2 + 35, 0xAAAAAA);
            } else if (activeSubMenuModule.equals("ElytraTarget")) {
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.elytraBypassType, this.width / 2 - 30, this.height / 2 + 5, 0xAAAAAA);
                context.drawTextWithShadow(this.textRenderer, "Oto Fişek Hızı: " + AxoHileClient.autoFireworkSpeed + "/10", this.width / 2 - 30, this.height / 2 + 20, 0xAAAAAA);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Sol Kategori Seçimi
        if (mouseX >= centerX - 160 && mouseX <= centerX - 90) {
            String[] categories = {"Combat", "Move", "Render", "Player", "Themes"};
            int catY = centerY - 60;
            for (String cat : categories) {
                if (mouseY >= catY && mouseY <= catY + 15) {
                    selectedCategory = cat;
                    subMenuOpen = false;
                }
                catY += 20;
            }
        }

        // Combat Modülleri Tıklama Kontrolü
        if (selectedCategory.equals("Combat") && mouseX >= centerX - 75 && mouseX <= centerX + 80) {
            // AttackAura
            if (mouseY >= centerY - 65 && mouseY <= centerY - 35) {
                if (button == 0) AxoHileClient.killauraEnabled = !AxoHileClient.killauraEnabled;
                else if (button == 1) {
                    subMenuOpen = true;
                    activeSubMenuModule = "AttackAura";
                }
            }
            // ElytraTarget
            else if (mouseY >= centerY - 25 && mouseY <= centerY + 5) {
                if (button == 0) AxoHileClient.elytraFlightEnabled = !AxoHileClient.elytraFlightEnabled;
                else if (button == 1) {
                    subMenuOpen = true;
                    activeSubMenuModule = "ElytraTarget";
                }
            }
        }

        // Sağ Tık Alt Menü İçindeki Ayarları Değiştirme
        if (subMenuOpen && mouseX >= centerX - 40 && mouseX <= centerX + 120 && mouseY >= centerY - 30 && mouseY <= centerY + 70) {
            if (activeSubMenuModule.equals("AttackAura")) {
                if (mouseY >= centerY + 0 && mouseY <= centerY + 15) {
                    if (AxoHileClient.attackBypassType.equals("Vulcan")) AxoHileClient.attackBypassType = "Grim";
                    else if (AxoHileClient.attackBypassType.equals("Grim")) AxoHileClient.attackBypassType = "Polar";
                    else if (AxoHileClient.attackBypassType.equals("Polar")) AxoHileClient.attackBypassType = "Fun";
                    else AxoHileClient.attackBypassType = "Vulcan";
                } else if (mouseY >= centerY + 15 && mouseY <= centerY + 30) {
                    AxoHileClient.fullHitEnabled = !AxoHileClient.fullHitEnabled;
                } else if (mouseY >= centerY + 30 && mouseY <= centerY + 45) {
                    AxoHileClient.shieldBreakerAxeMode = !AxoHileClient.shieldBreakerAxeMode;
                }
            } else if (activeSubMenuModule.equals("ElytraTarget")) {
                if (mouseY >= centerY + 0 && mouseY <= centerY + 15) {
                    // DÜZELTME BURADA YAPILDI (Eksik olan else if eklendi)
                    if (AxoHileClient.elytraBypassType.equals("Vulcan")) AxoHileClient.elytraBypassType = "Grim";
                    else if (AxoHileClient.elytraBypassType.equals("Grim")) AxoHileClient.elytraBypassType = "Polar";
                    else if (AxoHileClient.elytraBypassType.equals("Polar")) AxoHileClient.elytraBypassType = "Fun";
                    else AxoHileClient.elytraBypassType = "Vulcan";
                } else if (mouseY >= centerY + 15 && mouseY <= centerY + 30) {
                    AxoHileClient.autoFireworkSpeed = AxoHileClient.autoFireworkSpeed >= 10 ? 1 : AxoHileClient.autoFireworkSpeed + 1;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
