package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AxoHileMenuScreen extends Screen {
    private String selectedCategory = "Combat";
    private boolean subMenuOpen = false;
    private String activeSubMenuModule = "";
    private boolean awaitingKeyBind = false;
    private String moduleToBind = "";

    public AxoHileMenuScreen() {
        super(Text.literal("VexionClient Menu"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Siyah arka plan
        context.fill(this.width / 2 - 180, this.height / 2 - 120, this.width / 2 + 180, this.height / 2 + 120, 0xF0101010);
        
        // Sol Kategori Paneli
        context.fill(this.width / 2 - 180, this.height / 2 - 120, this.width / 2 - 100, this.height / 2 + 120, 0xF0181818);
        context.drawTextWithShadow(this.textRenderer, "§bVexionClient", this.width / 2 - 170, this.height / 2 - 105, 0xFFFFFF);

        // Kategoriler
        String[] categories = {"Combat", "Move", "Render", "Player", "Themes"};
        int catY = this.height / 2 - 70;
        for (String cat : categories) {
            boolean isSelected = selectedCategory.equals(cat);
            int color = isSelected ? 0x5555FFFF : 0xFFFFFF;
            context.drawTextWithShadow(this.textRenderer, (isSelected ? "> " : "  ") + cat, this.width / 2 - 170, catY, color);
            catY += 22;
        }

        // Sağ Taraf - İçerik
        if (selectedCategory.equals("Combat")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Combat Modülleri]", this.width / 2 - 85, this.height / 2 - 105, 0xFFFFFF);

            // AttackAura
            String auraStatus = AxoHileClient.killauraEnabled ? "§a[AÇIK]" : "§c[KAPALI]";
            context.drawTextWithShadow(this.textRenderer, "AttackAura " + auraStatus, this.width / 2 - 85, this.height / 2 - 70, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7(Sağ tık: Ayarlar | Orta tık: Tuş Ata)", this.width / 2 - 85, this.height / 2 - 58, 0x888888);

            // ElytraTarget
            String elytraStatus = AxoHileClient.elytraFlightEnabled ? "§a[AÇIK]" : "§c[KAPALI]";
            context.drawTextWithShadow(this.textRenderer, "ElytraTarget " + elytraStatus, this.width / 2 - 85, this.height / 2 - 25, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7(Sağ tık: Ayarlar | Orta tık: Tuş Ata)", this.width / 2 - 85, this.height / 2 - 13, 0x888888);
        } 
        else if (selectedCategory.equals("Render")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Render / HUD Bileşenleri]", this.width / 2 - 85, this.height / 2 - 105, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "HUD Öğeleri (Sağ tık ile yönet)", this.width / 2 - 85, this.height / 2 - 70, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "📋 Staff List  " + (AxoHileClient.hudStaffList ? "§a[AÇIK]" : "§c[KAPALI]"), this.width / 2 - 85, this.height / 2 - 45, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "⌨️ Keybinds  " + (AxoHileClient.hudKeybinds ? "§a[AÇIK]" : "§c[KAPALI]"), this.width / 2 - 85, this.height / 2 - 25, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "🧪 Potions    " + (AxoHileClient.hudPotions ? "§a[AÇIK]" : "§c[KAPALI]"), this.width / 2 - 85, this.height / 2 - 5, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "🖥️ Ana Menü  " + (AxoHileClient.hudMainMenu ? "§a[AÇIK]" : "§c[KAPALI]"), this.width / 2 - 85, this.height / 2 + 15, 0xFFFFFF);
        }
        else if (selectedCategory.equals("Themes")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Temalar & Renkler]", this.width / 2 - 85, this.height / 2 - 105, 0xFFFFFF);
            // Büyük renkli kareler
            context.fill(this.width / 2 - 75, this.height / 2 - 70, this.width / 2 - 35, this.height / 2 - 30, 0xFFFF5555); // Kırmızı
            context.fill(this.width / 2 - 25, this.height / 2 - 70, this.width / 2 + 15, this.height / 2 - 30, 0xFF5555FF); // Mavi
            context.fill(this.width / 2 + 25, this.height / 2 - 70, this.width / 2 + 65, this.height / 2 - 30, 0xFFFFFF55); // Sarı
            context.fill(this.width / 2 - 75, this.height / 2 - 20, this.width / 2 - 35, this.height / 2 + 20, 0xFFFF55FF); // Pembe
            context.fill(this.width / 2 - 25, this.height / 2 - 20, this.width / 2 + 15, this.height / 2 + 20, 0xFFFFAA00); // Turuncu
            context.fill(this.width / 2 + 25, this.height / 2 - 20, this.width / 2 + 65, this.height / 2 + 20, 0xFF9933FF); // Mor
        }

        // Sağ Tık Alt Menü Paneli
        if (subMenuOpen) {
            context.fill(this.width / 2 - 30, this.height / 2 - 40, this.width / 2 + 140, this.height / 2 + 80, 0xF2202020);
            context.drawTextWithShadow(this.textRenderer, "§6§l" + activeSubMenuModule + " Ayarları", this.width / 2 - 20, this.height / 2 - 30, 0xFFFFFF);

            if (activeSubMenuModule.equals("AttackAura")) {
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.attackBypassType, this.width / 2 - 20, this.height / 2 - 5, 0xAAAAAA);
                context.drawTextWithShadow(this.textRenderer, "Full Hit: " + (AxoHileClient.fullHitEnabled ? "§aZıplama Bekler" : "§cNormal"), this.width / 2 - 20, this.height / 2 + 15, 0xAAAAAA);
                context.drawTextWithShadow(this.textRenderer, "Balta Kalkan: " + (AxoHileClient.shieldBreakerAxeMode ? "§aAçık" : "§cKapalı"), this.width / 2 - 20, this.height / 2 + 35, 0xAAAAAA);
            } else if (activeSubMenuModule.equals("ElytraTarget")) {
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.elytraBypassType, this.width / 2 - 20, this.height / 2 - 5, 0xAAAAAA);
                context.drawTextWithShadow(this.textRenderer, "Oto Fişek Hızı: " + AxoHileClient.autoFireworkSpeed + "/10", this.width / 2 - 20, this.height / 2 + 15, 0xAAAAAA);
            }
        }

        if (awaitingKeyBind) {
            context.fill(this.width / 2 - 100, this.height / 2 - 30, this.width / 2 + 100, this.height / 2 + 30, 0xF0000000);
            context.drawCenteredTextWithShadow(this.textRenderer, "§e" + moduleToBind + " için bir tuşa basın...", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (awaitingKeyBind) return super.mouseClicked(mouseX, mouseY, button);

        // Kategori seçimi
        if (mouseX >= centerX - 180 && mouseX <= centerX - 100) {
            String[] categories = {"Combat", "Move", "Render", "Player", "Themes"};
            int catY = centerY - 70;
            for (String cat : categories) {
                if (mouseY >= catY && mouseY <= catY + 18) {
                    selectedCategory = cat;
                    subMenuOpen = false;
                }
                catY += 22;
            }
        }

        // Combat modülleri tıklama
        if (selectedCategory.equals("Combat") && mouseX >= centerX - 85 && mouseX <= centerX + 90) {
            // AttackAura
            if (mouseY >= centerY - 75 && mouseY <= centerY - 40) {
                if (button == 0) AxoHileClient.killauraEnabled = !AxoHileClient.killauraEnabled;
                else if (button == 1) { subMenuOpen = true; activeSubMenuModule = "AttackAura"; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "AttackAura"; }
            }
            // ElytraTarget
            else if (mouseY >= centerY - 30 && mouseY <= centerY + 5) {
                if (button == 0) AxoHileClient.elytraFlightEnabled = !AxoHileClient.elytraFlightEnabled;
                else if (button == 1) { subMenuOpen = true; activeSubMenuModule = "ElytraTarget"; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "ElytraTarget"; }
            }
        }
        else if (selectedCategory.equals("Render") && mouseX >= centerX - 85 && mouseX <= centerX + 90) {
            if (mouseY >= centerY - 45 && mouseY <= centerY - 25) AxoHileClient.hudStaffList = !AxoHileClient.hudStaffList;
            if (mouseY >= centerY - 25 && mouseY <= centerY - 5) AxoHileClient.hudKeybinds = !AxoHileClient.hudKeybinds;
            if (mouseY >= centerY - 5 && mouseY <= centerY + 15) AxoHileClient.hudPotions = !AxoHileClient.hudPotions;
            if (mouseY >= centerY + 15 && mouseY <= centerY + 35) AxoHileClient.hudMainMenu = !AxoHileClient.hudMainMenu;
        }

        // Sağ Tık Alt Menü Ayar Değişimi
        if (subMenuOpen && mouseX >= centerX - 30 && mouseX <= centerX + 140 && mouseY >= centerY - 40 && mouseY <= centerY + 80) {
            if (activeSubMenuModule.equals("AttackAura")) {
                if (mouseY >= centerY - 5 && mouseY + 15 >= centerY) {
                    if (AxoHileClient.attackBypassType.equals("Vulcan")) AxoHileClient.attackBypassType = "Grim";
                    else if (AxoHileClient.attackBypassType.equals("Grim")) AxoHileClient.attackBypassType = "Polar";
                    else if (AxoHileClient.attackBypassType.equals("Polar")) AxoHileClient.attackBypassType = "Fun";
                    else AxoHileClient.attackBypassType = "Vulcan";
                } else if (mouseY >= centerY + 15 && mouseY <= centerY + 35) {
                    AxoHileClient.fullHitEnabled = !AxoHileClient.fullHitEnabled;
                } else if (mouseY >= centerY + 35 && mouseY <= centerY + 55) {
                    AxoHileClient.shieldBreakerAxeMode = !AxoHileClient.shieldBreakerAxeMode;
                }
            } else if (activeSubMenuModule.equals("ElytraTarget")) {
                if (mouseY >= centerY - 5 && mouseY <= centerY + 15) {
                    if (AxoHileClient.elytraBypassType.equals("Vulcan")) AxoHileClient.elytraBypassType = "Grim";
                    else if (AxoHileClient.elytraBypassType.equals("Grim")) AxoHileClient.elytraBypassType = "Polar";
                    else if (AxoHileClient.elytraBypassType.equals("Polar")) AxoHileClient.elytraBypassType = "Fun";
                    else AxoHileClient.elytraBypassType = "Vulcan";
                } else if (mouseY >= centerY + 15 && mouseY <= centerY + 35) {
                    AxoHileClient.autoFireworkSpeed = AxoHileClient.autoFireworkSpeed >= 10 ? 1 : AxoHileClient.autoFireworkSpeed + 1;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (awaitingKeyBind) {
            if (moduleToBind.equals("AttackAura")) AxoHileClient.attackAuraKey = keyCode;
            else if (moduleToBind.equals("ElytraTarget")) AxoHileClient.elytraKey = keyCode;
            awaitingKeyBind = false;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
