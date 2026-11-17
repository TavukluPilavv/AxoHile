package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AxoHileMenuScreen extends Screen {
    private String selectedCategory = "Combat";
    private boolean attackAuraSubMenu = false;
    private boolean elytraTargetSubMenu = false;
    private boolean autoTotemSubMenu = false;
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
        VexionBackgroundRenderer.render(context, this.width, this.height);

        context.fill(this.width / 2 - 180, this.height / 2 - 120, this.width / 2 + 180, this.height / 2 + 120, 0xF0101010);
        
        context.fill(this.width / 2 - 180, this.height / 2 - 120, this.width / 2 - 95, this.height / 2 + 120, 0xF0181818);
        context.drawTextWithShadow(this.textRenderer, "§bVexionClient", this.width / 2 - 170, this.height / 2 - 105, 0xFFFFFF);

        String[] categories = {"Combat", "Move", "Render", "Accounts", "Themes"};
        int catY = this.height / 2 - 70;
        for (String cat : categories) {
            boolean isSelected = selectedCategory.equals(cat);
            int color = isSelected ? AxoHileClient.themeColor : 0xFFFFFF;
            context.drawTextWithShadow(this.textRenderer, (isSelected ? "> " : "  ") + cat, this.width / 2 - 170, catY, color);
            catY += 22;
        }

        int startX = this.width / 2 - 85;
        int startY = this.height / 2 - 105;

        if (selectedCategory.equals("Combat")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Combat Modülleri]", startX, startY, 0xFFFFFF);

            int auraColor = AxoHileClient.killauraEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 15, startX + 250, startY + 45, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "⚔️ AttackAura", startX + 10, startY + 23, auraColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.killauraEnabled ? "AÇIK" : "KAPALI", startX + 200, startY + 23, auraColor);

            if (attackAuraSubMenu) {
                context.fill(startX, startY + 47, startX + 250, startY + 115, 0x90151515);
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.attackBypassType, startX + 15, startY + 55, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Full Hit: " + (AxoHileClient.fullHitEnabled ? "Zıpla & Crit" : "Normal (Cooldown)"), startX + 15, startY + 75, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Balta Kalkan: " + (AxoHileClient.shieldBreakerAxeMode ? "Açık" : "Kapalı"), startX + 15, startY + 95, 0xCCCCCC);
            }

            int elytraBoxY = attackAuraSubMenu ? startY + 125 : startY + 55;
            int elytraColor = AxoHileClient.elytraFlightEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, elytraBoxY, startX + 250, elytraBoxY + 30, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "🦅 ElytraTarget", startX + 10, elytraBoxY + 8, elytraColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.elytraFlightEnabled ? "AÇIK" : "KAPALI", startX + 200, elytraBoxY + 8, elytraColor);

            if (elytraTargetSubMenu) {
                context.fill(startX, elytraBoxY + 32, startX + 250, elytraBoxY + 95, 0x90151515);
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.elytraBypassType, startX + 15, elytraBoxY + 40, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Oto Fişek Hızı: " + AxoHileClient.autoFireworkSpeed + "/10", startX + 15, elytraBoxY + 60, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Hedef Mesafe: " + AxoHileClient.elytraTargetRange + " Blok", startX + 15, elytraBoxY + 80, 0xCCCCCC);
            }
        } 
        else if (selectedCategory.equals("Move")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Move / Player Modülleri]", startX, startY, 0xFFFFFF);

            int totemColor = AxoHileClient.autoTotemEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 15, startX + 250, startY + 45, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "🛡️ AutoTotem", startX + 10, startY + 23, totemColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.autoTotemEnabled ? "AÇIK" : "KAPALI", startX + 200, startY + 23, totemColor);

            if (autoTotemSubMenu) {
                context.fill(startX, startY + 47, startX + 250, startY + 85, 0x90151515);
                context.drawTextWithShadow(this.textRenderer, "Can Eşiği: " + AxoHileClient.autoTotemHealth + " Kalp", startX + 15, startY + 58, 0xCCCCCC);
            }
        }
        else if (selectedCategory.equals("Render")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Render / HUD Bileşenleri]", startX, startY, 0xFFFFFF);
            
            int staffColor = AxoHileClient.hudStaffList ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 15, startX + 250, startY + 38, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "📋 Staff List", startX + 10, startY + 20, staffColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudStaffList ? "AÇIK" : "KAPALI", startX + 200, startY + 20, staffColor);

            int keyColor = AxoHileClient.hudKeybinds ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 42, startX + 250, startY + 65, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "⌨️ Keybinds", startX + 10, startY + 47, keyColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudKeybinds ? "AÇIK" : "KAPALI", startX + 200, startY + 47, keyColor);

            int potColor = AxoHileClient.hudPotions ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 69, startX + 250, startY + 92, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "🧪 Potions", startX + 10, startY + 74, potColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudPotions ? "AÇIK" : "KAPALI", startX + 200, startY + 74, potColor);

            int mainColor = AxoHileClient.hudMainMenu ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 96, startX + 250, startY + 119, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "🖥️ Ana Menü HUD", startX + 10, startY + 101, mainColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudMainMenu ? "AÇIK" : "KAPALI", startX + 200, startY + 101, mainColor);

            int targetColor = AxoHileClient.hudTargetHud ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 123, startX + 250, startY + 146, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "🎯 TargetHUD", startX + 10, startY + 128, targetColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudTargetHud ? "AÇIK" : "KAPALI", startX + 200, startY + 128, targetColor);
        }
        else if (selectedCategory.equals("Accounts")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Çevrimdışı Hesap Yöneticisi]", startX, startY, 0xFFFFFF);
            
            context.fill(startX, startY + 18, startX + 250, startY + 42, 0x80202020);
            context.drawTextWithShadow(this.textRenderer, "Kullanıcı adı: " + AxoHileClient.typedAccountInput + "_", startX + 10, startY + 26, 0xFFFFFF);

            context.fill(startX, startY + 48, startX + 120, startY + 72, 0xFF333333);
            context.drawCenteredTextWithShadow(this.textRenderer, "+ Offline Ekle", startX + 60, startY + 56, 0x55FF55);

            context.fill(startX + 130, startY + 48, startX + 250, startY + 72, 0xFF333333);
            context.drawCenteredTextWithShadow(this.textRenderer, "🗑️ Seçileni Sil", startX + 190, startY + 56, 0xFF5555);

            context.drawTextWithShadow(this.textRenderer, "Kayıtlı çevrimdışı profiller:", startX, startY + 82, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer, "§7Not: Bu liste Microsoft oturumunu değiştirmez.", startX, startY + 224, 0xAAAAAA);
            int accY = startY + 98;
            for (String acc : AxoHileClient.accountList) {
                boolean isSel = acc.equals(AxoHileClient.selectedAccount);
                int bg = isSel ? 0xFF224488 : 0x80252525;
                context.fill(startX, accY, startX + 250, accY + 20, bg);
                context.drawTextWithShadow(this.textRenderer, (isSel ? "▶ " : "• ") + acc, startX + 10, accY + 6, isSel ? 0x55FFFF : 0xFFFFFF);
                accY += 24;
            }
        }
        else if (selectedCategory.equals("Themes")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Temalar & Renkler]", startX, startY, 0xFFFFFF);
            
            context.fill(startX + 10, startY + 20, startX + 60, startY + 70, 0xFFFF5555);
            context.fill(startX + 70, startY + 20, startX + 120, startY + 70, 0xFF5555FF);
            context.fill(startX + 130, startY + 20, startX + 180, startY + 70, 0xFFFFFF55);
            context.fill(startX + 10, startY + 80, startX + 60, startY + 130, 0xFFFF55FF);
            context.fill(startX + 70, startY + 80, startX + 120, startY + 130, 0xFFFFAA00);
            context.fill(startX + 130, startY + 80, startX + 180, startY + 130, 0xFF9933FF);
        }

        if (awaitingKeyBind) {
            context.fill(this.width / 2 - 100, this.height / 2 - 30, this.width / 2 + 100, this.height / 2 + 30, 0xF0000000);
            context.drawCenteredTextWithShadow(this.textRenderer, "§e" + moduleToBind + " için bir tuşa basın...", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (awaitingKeyBind) return super.mouseClicked(mouseX, mouseY, button);

        if (mouseX >= centerX - 180 && mouseX <= centerX - 95) {
            String[] categories = {"Combat", "Move", "Render", "Accounts", "Themes"};
            int catY = centerY - 70;
            for (String cat : categories) {
                if (mouseY >= catY && mouseY <= catY + 18) {
                    selectedCategory = cat;
                    attackAuraSubMenu = false;
                    elytraTargetSubMenu = false;
                    autoTotemSubMenu = false;
                }
                catY += 22;
            }
        }

        int startX = centerX - 85;
        int startY = centerY - 105;

        if (selectedCategory.equals("Combat")) {
            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= startY + 15 && mouseY <= startY + 45) {
                if (button == 0) AxoHileClient.killauraEnabled = !AxoHileClient.killauraEnabled;
                else if (button == 1) { attackAuraSubMenu = !attackAuraSubMenu; elytraTargetSubMenu = false; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "AttackAura"; }
            }

            if (attackAuraSubMenu && mouseX >= startX && mouseX <= startX + 250 && mouseY >= startY + 47 && mouseY <= startY + 115) {
                if (mouseY >= startY + 50 && mouseY <= startY + 70) {
                    if (AxoHileClient.attackBypassType.equals("Vulcan")) AxoHileClient.attackBypassType = "Grim";
                    else if (AxoHileClient.attackBypassType.equals("Grim")) AxoHileClient.attackBypassType = "Polar";
                    else if (AxoHileClient.attackBypassType.equals("Polar")) AxoHileClient.attackBypassType = "Fun";
                    else AxoHileClient.attackBypassType = "Vulcan";
                } else if (mouseY >= startY + 70 && mouseY <= startY + 90) {
                    AxoHileClient.fullHitEnabled = !AxoHileClient.fullHitEnabled;
                } else if (mouseY >= startY + 90 && mouseY <= startY + 110) {
                    AxoHileClient.shieldBreakerAxeMode = !AxoHileClient.shieldBreakerAxeMode;
                }
            }

            int elytraBoxY = attackAuraSubMenu ? startY + 125 : startY + 55;
            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= elytraBoxY && mouseY <= elytraBoxY + 30) {
                if (button == 0) AxoHileClient.elytraFlightEnabled = !AxoHileClient.elytraFlightEnabled;
                else if (button == 1) { elytraTargetSubMenu = !elytraTargetSubMenu; attackAuraSubMenu = false; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "ElytraTarget"; }
            }

            if (elytraTargetSubMenu && mouseX >= startX && mouseX <= startX + 250 && mouseY >= elytraBoxY + 32 && mouseY <= elytraBoxY + 95) {
                if (mouseY >= elytraBoxY + 35 && mouseY <= elytraBoxY + 55) {
                    if (AxoHileClient.elytraBypassType.equals("Vulcan")) AxoHileClient.elytraBypassType = "Grim";
                    else if (AxoHileClient.elytraBypassType.equals("Grim")) AxoHileClient.elytraBypassType = "Polar";
                    else if (AxoHileClient.elytraBypassType.equals("Polar")) AxoHileClient.elytraBypassType = "Fun";
                    else AxoHileClient.elytraBypassType = "Vulcan";
                } else if (mouseY >= elytraBoxY + 55 && mouseY <= elytraBoxY + 75) {
                    AxoHileClient.autoFireworkSpeed = AxoHileClient.autoFireworkSpeed >= 10 ? 1 : AxoHileClient.autoFireworkSpeed + 1;
                } else if (mouseY >= elytraBoxY + 75 && mouseY <= elytraBoxY + 92) {
                    AxoHileClient.elytraTargetRange = AxoHileClient.elytraTargetRange >= 100 ? 20 : AxoHileClient.elytraTargetRange + 10;
                }
            }
        }
        else if (selectedCategory.equals("Move")) {
            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= startY + 15 && mouseY <= startY + 45) {
                if (button == 0) AxoHileClient.autoTotemEnabled = !AxoHileClient.autoTotemEnabled;
                else if (button == 1) { autoTotemSubMenu = !autoTotemSubMenu; }
            }
            if (autoTotemSubMenu && mouseX >= startX && mouseX <= startX + 250 && mouseY >= startY + 47 && mouseY <= startY + 85) {
                if (mouseY >= startY + 50 && mouseY <= startY + 75) {
                    AxoHileClient.autoTotemHealth = AxoHileClient.autoTotemHealth >= 10 ? 1 : AxoHileClient.autoTotemHealth + 1;
                }
            }
        }
        else if (selectedCategory.equals("Render")) {
            if (mouseX >= startX && mouseX <= startX + 250) {
                if (mouseY >= startY + 15 && mouseY <= startY + 38) AxoHileClient.hudStaffList = !AxoHileClient.hudStaffList;
                if (mouseY >= startY + 42 && mouseY <= startY + 65) AxoHileClient.hudKeybinds = !AxoHileClient.hudKeybinds;
                if (mouseY >= startY + 69 && mouseY <= startY + 92) AxoHileClient.hudPotions = !AxoHileClient.hudPotions;
                if (mouseY >= startY + 96 && mouseY <= startY + 119) AxoHileClient.hudMainMenu = !AxoHileClient.hudMainMenu;
                if (mouseY >= startY + 123 && mouseY <= startY + 146) AxoHileClient.hudTargetHud = !AxoHileClient.hudTargetHud;
            }
        }
        else if (selectedCategory.equals("Accounts")) {
            if (mouseX >= startX && mouseX <= startX + 120 && mouseY >= startY + 48 && mouseY <= startY + 72) {
                String offlineName = AxoHileClient.typedAccountInput.trim();
                if (offlineName.matches("[A-Za-z0-9_]{3,16}") && !AxoHileClient.accountList.contains(offlineName)) {
                    AxoHileClient.accountList.add(offlineName);
                    AxoHileClient.selectedAccount = offlineName;
                    AxoHileClient.typedAccountInput = "";
                    AxoHileClient.saveOfflineAccounts();
                }
            }
            else if (mouseX >= startX + 130 && mouseX <= startX + 250 && mouseY >= startY + 48 && mouseY <= startY + 72) {
                if (!AxoHileClient.selectedAccount.isEmpty()) {
                    AxoHileClient.accountList.remove(AxoHileClient.selectedAccount);
                    AxoHileClient.selectedAccount = AxoHileClient.accountList.isEmpty() ? "" : AxoHileClient.accountList.get(0);
                    AxoHileClient.saveOfflineAccounts();
                }
            }
            int accY = startY + 98;
            for (String acc : AxoHileClient.accountList) {
                if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= accY && mouseY <= accY + 20) {
                    AxoHileClient.selectedAccount = acc;
                }
                accY += 24;
            }
        }
        else if (selectedCategory.equals("Themes")) {
            if (mouseX >= startX + 10 && mouseX <= startX + 60 && mouseY >= startY + 20 && mouseY <= startY + 70) AxoHileClient.themeColor = 0xFFFF5555;
            if (mouseX >= startX + 70 && mouseX <= startX + 120 && mouseY >= startY + 20 && mouseY <= startY + 70) AxoHileClient.themeColor = 0xFF5555FF;
            if (mouseX >= startX + 130 && mouseX <= startX + 180 && mouseY >= startY + 20 && mouseY <= startY + 70) AxoHileClient.themeColor = 0xFFFFFF55;
            if (mouseX >= startX + 10 && mouseX <= startX + 60 && mouseY >= startY + 80 && mouseY <= startY + 130) AxoHileClient.themeColor = 0xFFFF55FF;
            if (mouseX >= startX + 70 && mouseX <= startX + 120 && mouseY >= startY + 80 && mouseY <= startY + 130) AxoHileClient.themeColor = 0xFFFFAA00;
            if (mouseX >= startX + 130 && mouseX <= startX + 180 && mouseY >= startY + 80 && mouseY <= startY + 130) AxoHileClient.themeColor = 0xFF9933FF;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (selectedCategory.equals("Accounts")) {
            if (chr >= 32 && chr < 127 && AxoHileClient.typedAccountInput.length() < 16) {
                AxoHileClient.typedAccountInput += chr;
                return true;
            }
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (awaitingKeyBind) {
            if (moduleToBind.equals("AttackAura")) AxoHileClient.attackAuraKey = keyCode;
            else if (moduleToBind.equals("ElytraTarget")) AxoHileClient.elytraKey = keyCode;
            awaitingKeyBind = false;
            return true;
        }
        if (selectedCategory.equals("Accounts")) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !AxoHileClient.typedAccountInput.isEmpty()) {
                AxoHileClient.typedAccountInput = AxoHileClient.typedAccountInput.substring(0, AxoHileClient.typedAccountInput.length() - 1);
                return true;
            }
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
