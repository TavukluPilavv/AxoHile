package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AxoHileMenuScreen extends Screen {
    private String selectedCategory = "Combat";
    private boolean attackAuraSubMenu = false;
    private boolean triggerbotSubMenu = false;
    private boolean elytraTargetSubMenu = false;
    private boolean autoTotemSubMenu = false;
    private boolean elytraHelperSubMenu = false;
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
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Oyun dünyasını görünür bırak
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(this.width / 2 - 180, this.height / 2 - 120, this.width / 2 + 180, this.height / 2 + 120, 0x9E101010);

        context.fill(this.width / 2 - 180, this.height / 2 - 120, this.width / 2 - 95, this.height / 2 + 120, 0xAE181818);
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
            context.fill(startX, startY + 14, startX + 250, startY + 38, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "⚔️ AttackAura", startX + 10, startY + 21, auraColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.killauraEnabled ? "AÇIK" : "KAPALI", startX + 200, startY + 21, auraColor);

            int currentY = startY + 42;
            if (attackAuraSubMenu) {
                context.fill(startX, currentY, startX + 250, currentY + 105, 0x80151515);
                String speedLabel = String.format("%.1f", AxoHileClient.hitSpeed) + (Math.abs(AxoHileClient.hitSpeed - 5.5) < 0.05 ? " (Tam Hit)" : "");
                context.drawTextWithShadow(this.textRenderer, "Vurma Hızı: " + speedLabel, startX + 12, currentY + 6, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Critical Hit: " + (AxoHileClient.criticalHitEnabled ? "AÇIK" : "KAPALI"), startX + 12, currentY + 22, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Full Hit: " + (AxoHileClient.fullHitEnabled ? "AÇIK" : "KAPALI"), startX + 12, currentY + 38, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Target Lock: " + (AxoHileClient.targetLockEnabled ? "AÇIK" : "KAPALI"), startX + 12, currentY + 54, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Serbest Kamera: " + (AxoHileClient.freeCameraEnabled ? "AÇIK" : "KAPALI"), startX + 12, currentY + 70, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.attackBypassType, startX + 12, currentY + 86, 0xCCCCCC);
                currentY += 110;
            }

            int trigColor = AxoHileClient.triggerbotEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, currentY, startX + 250, currentY + 24, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🎯 Triggerbot", startX + 10, currentY + 8, trigColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.triggerbotEnabled ? "AÇIK" : "KAPALI", startX + 200, currentY + 8, trigColor);
            currentY += 28;

            if (triggerbotSubMenu) {
                context.fill(startX, currentY, startX + 250, currentY + 52, 0x80151515);
                String speedLabel = String.format("%.1f", AxoHileClient.hitSpeed) + (Math.abs(AxoHileClient.hitSpeed - 5.5) < 0.05 ? " (Tam Hit)" : "");
                context.drawTextWithShadow(this.textRenderer, "Vurma Hızı: " + speedLabel, startX + 12, currentY + 6, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Hit: " + (AxoHileClient.triggerbotHitOnlyWhenJumping ? "Sadece Zıplayınca" : "Zıplamadan da"), startX + 12, currentY + 21, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Balta Kalkan: " + (AxoHileClient.shieldBreakerAxeMode ? "Açık" : "Kapalı"), startX + 12, currentY + 36, 0xCCCCCC);
                currentY += 56;
            }

            int elytraColor = AxoHileClient.elytraFlightEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, currentY, startX + 250, currentY + 24, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🦅 ElytraTarget", startX + 10, currentY + 8, elytraColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.elytraFlightEnabled ? "AÇIK" : "KAPALI", startX + 200, currentY + 8, elytraColor);
            currentY += 28;

            if (elytraTargetSubMenu) {
                context.fill(startX, currentY, startX + 250, currentY + 56, 0x80151515);
                context.drawTextWithShadow(this.textRenderer, "Bypass: " + AxoHileClient.elytraBypassType, startX + 12, currentY + 6, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Oto Fişek Hızı: " + AxoHileClient.autoFireworkSpeed + "/10", startX + 12, currentY + 22, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Hedef Mesafe: " + AxoHileClient.elytraTargetRange + " Blok", startX + 12, currentY + 38, 0xCCCCCC);
            }
        }
        else if (selectedCategory.equals("Move")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Move / Player Modülleri]", startX, startY, 0xFFFFFF);

            int totemColor = AxoHileClient.autoTotemEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 15, startX + 250, startY + 45, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🛡️ AutoTotem", startX + 10, startY + 23, totemColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.autoTotemEnabled ? "AÇIK" : "KAPALI", startX + 200, startY + 23, totemColor);

            if (autoTotemSubMenu) {
                context.fill(startX, startY + 47, startX + 250, startY + 85, 0x80151515);
                context.drawTextWithShadow(this.textRenderer, "Can Eşiği: " + AxoHileClient.autoTotemHealth + " Kalp", startX + 15, startY + 58, 0xCCCCCC);
            }

            int helperBoxY = autoTotemSubMenu ? startY + 95 : startY + 55;
            int helperColor = AxoHileClient.elytraHelperEnabled ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, helperBoxY, startX + 250, helperBoxY + 30, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🪂 ElytraHelper", startX + 10, helperBoxY + 8, helperColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.elytraHelperEnabled ? "AÇIK" : "KAPALI", startX + 200, helperBoxY + 8, helperColor);

            if (elytraHelperSubMenu) {
                context.fill(startX, helperBoxY + 32, startX + 250, helperBoxY + 95, 0x80151515);
                String swapKeyName = AxoHileClient.elytraHelperSwapKey == -1 ? "Seçilmedi [...]" : GLFW.glfwGetKeyName(AxoHileClient.elytraHelperSwapKey, 0);
                String fireKeyName = AxoHileClient.elytraHelperFireworkKey == -1 ? "Seçilmedi [...]" : GLFW.glfwGetKeyName(AxoHileClient.elytraHelperFireworkKey, 0);

                context.drawTextWithShadow(this.textRenderer, "Giy/Çıkar Tuşu: " + swapKeyName, startX + 15, helperBoxY + 42, 0xCCCCCC);
                context.drawTextWithShadow(this.textRenderer, "Oto Fişek Tuşu: " + fireKeyName, startX + 15, helperBoxY + 68, 0xCCCCCC);
            }
        }
        else if (selectedCategory.equals("Render")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Render / HUD Bileşenleri]", startX, startY, 0xFFFFFF);

            int staffColor = AxoHileClient.hudStaffList ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 15, startX + 250, startY + 38, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "📋 Staff List", startX + 10, startY + 20, staffColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudStaffList ? "AÇIK" : "KAPALI", startX + 200, startY + 20, staffColor);

            int keyColor = AxoHileClient.hudKeybinds ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 42, startX + 250, startY + 65, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "⌨️ Keybinds", startX + 10, startY + 47, keyColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudKeybinds ? "AÇIK" : "KAPALI", startX + 200, startY + 47, keyColor);

            int potColor = AxoHileClient.hudPotions ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 69, startX + 250, startY + 92, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🧪 Potions", startX + 10, startY + 74, potColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudPotions ? "AÇIK" : "KAPALI", startX + 200, startY + 74, potColor);

            int mainColor = AxoHileClient.hudMainMenu ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 96, startX + 250, startY + 119, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🖥️ Ana Menü HUD", startX + 10, startY + 101, mainColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudMainMenu ? "AÇIK" : "KAPALI", startX + 200, startY + 101, mainColor);

            int targetColor = AxoHileClient.hudTargetHud ? AxoHileClient.themeColor : 0xFFFFFFFF;
            context.fill(startX, startY + 123, startX + 250, startY + 146, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "🎯 TargetHUD", startX + 10, startY + 128, targetColor);
            context.drawTextWithShadow(this.textRenderer, AxoHileClient.hudTargetHud ? "AÇIK" : "KAPALI", startX + 200, startY + 128, targetColor);
        }
        else if (selectedCategory.equals("Accounts")) {
            context.drawTextWithShadow(this.textRenderer, "§e[Çevrimdışı Hesap Yöneticisi]", startX, startY, 0xFFFFFF);

            context.fill(startX, startY + 18, startX + 250, startY + 42, 0x70202020);
            context.drawTextWithShadow(this.textRenderer, "Kullanıcı adı: " + AxoHileClient.typedAccountInput + "_", startX + 10, startY + 26, 0xFFFFFF);

            context.fill(startX, startY + 48, startX + 120, startY + 72, 0xB0333333);
            context.drawCenteredTextWithShadow(this.textRenderer, "+ Offline Ekle", startX + 60, startY + 56, 0x55FF55);

            context.fill(startX + 130, startY + 48, startX + 250, startY + 72, 0xB0333333);
            context.drawCenteredTextWithShadow(this.textRenderer, "🗑️ Seçileni Sil", startX + 190, startY + 56, 0xFF5555);

            context.drawTextWithShadow(this.textRenderer, "Kayıtlı çevrimdışı profiller:", startX, startY + 82, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer, "§7Not: Bu liste Microsoft oturumunu değiştirmez.", startX, startY + 224, 0xAAAAAA);
            int accY = startY + 98;
            for (String acc : AxoHileClient.accountList) {
                boolean isSel = acc.equals(AxoHileClient.selectedAccount);
                int bg = isSel ? 0xC0224488 : 0x70252525;
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
            context.fill(this.width / 2 - 100, this.height / 2 - 30, this.width / 2 + 100, this.height / 2 + 30, 0xE0000000);
            context.drawCenteredTextWithShadow(this.textRenderer, "§e" + moduleToBind + " için bir tuşa basın...", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void cycleHitSpeed() {
        double next = Math.round((AxoHileClient.hitSpeed + 0.2) * 10.0) / 10.0;
        if (next > 10.0) {
            next = 0.0;
        }
        AxoHileClient.hitSpeed = next;
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
                    triggerbotSubMenu = false;
                    elytraTargetSubMenu = false;
                    autoTotemSubMenu = false;
                    elytraHelperSubMenu = false;
                }
                catY += 22;
            }
        }

        int startX = centerX - 85;
        int startY = centerY - 105;

        if (selectedCategory.equals("Combat")) {
            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= startY + 14 && mouseY <= startY + 38) {
                if (button == 0) AxoHileClient.killauraEnabled = !AxoHileClient.killauraEnabled;
                else if (button == 1) { attackAuraSubMenu = !attackAuraSubMenu; triggerbotSubMenu = false; elytraTargetSubMenu = false; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "AttackAura"; }
            }

            int currentY = startY + 42;
            if (attackAuraSubMenu) {
                if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= currentY && mouseY <= currentY + 105) {
                    if (mouseY >= currentY + 4 && mouseY <= currentY + 20) cycleHitSpeed();
                    else if (mouseY >= currentY + 20 && mouseY <= currentY + 36) AxoHileClient.criticalHitEnabled = !AxoHileClient.criticalHitEnabled;
                    else if (mouseY >= currentY + 36 && mouseY <= currentY + 52) AxoHileClient.fullHitEnabled = !AxoHileClient.fullHitEnabled;
                    else if (mouseY >= currentY + 52 && mouseY <= currentY + 68) AxoHileClient.targetLockEnabled = !AxoHileClient.targetLockEnabled;
                    else if (mouseY >= currentY + 68 && mouseY <= currentY + 84) AxoHileClient.freeCameraEnabled = !AxoHileClient.freeCameraEnabled;
                    else if (mouseY >= currentY + 84 && mouseY <= currentY + 102) {
                        if (AxoHileClient.attackBypassType.equals("Vulcan")) AxoHileClient.attackBypassType = "Grim";
                        else if (AxoHileClient.attackBypassType.equals("Grim")) AxoHileClient.attackBypassType = "Polar";
                        else if (AxoHileClient.attackBypassType.equals("Polar")) AxoHileClient.attackBypassType = "Fun";
                        else AxoHileClient.attackBypassType = "Vulcan";
                    }
                }
                currentY += 110;
            }

            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= currentY && mouseY <= currentY + 24) {
                if (button == 0) AxoHileClient.triggerbotEnabled = !AxoHileClient.triggerbotEnabled;
                else if (button == 1) { triggerbotSubMenu = !triggerbotSubMenu; attackAuraSubMenu = false; elytraTargetSubMenu = false; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "Triggerbot"; }
            }
            currentY += 28;

            if (triggerbotSubMenu) {
                if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= currentY && mouseY <= currentY + 52) {
                    if (mouseY >= currentY + 4 && mouseY <= currentY + 19) cycleHitSpeed();
                    else if (mouseY >= currentY + 19 && mouseY <= currentY + 34) AxoHileClient.triggerbotHitOnlyWhenJumping = !AxoHileClient.triggerbotHitOnlyWhenJumping;
                    else if (mouseY >= currentY + 34 && mouseY <= currentY + 50) AxoHileClient.shieldBreakerAxeMode = !AxoHileClient.shieldBreakerAxeMode;
                }
                currentY += 56;
            }

            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= currentY && mouseY <= currentY + 24) {
                if (button == 0) AxoHileClient.elytraFlightEnabled = !AxoHileClient.elytraFlightEnabled;
                else if (button == 1) { elytraTargetSubMenu = !elytraTargetSubMenu; attackAuraSubMenu = false; triggerbotSubMenu = false; }
                else if (button == 2) { awaitingKeyBind = true; moduleToBind = "ElytraTarget"; }
            }
            currentY += 28;

            if (elytraTargetSubMenu) {
                if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= currentY && mouseY <= currentY + 56) {
                    if (mouseY >= currentY + 4 && mouseY <= currentY + 20) {
                        if (AxoHileClient.elytraBypassType.equals("Vulcan")) AxoHileClient.elytraBypassType = "Grim";
                        else if (AxoHileClient.elytraBypassType.equals("Grim")) AxoHileClient.elytraBypassType = "Polar";
                        else if (AxoHileClient.elytraBypassType.equals("Polar")) AxoHileClient.elytraBypassType = "Fun";
                        else AxoHileClient.elytraBypassType = "Vulcan";
                    } else if (mouseY >= currentY + 20 && mouseY <= currentY + 36) {
                        AxoHileClient.autoFireworkSpeed = AxoHileClient.autoFireworkSpeed >= 10 ? 1 : AxoHileClient.autoFireworkSpeed + 1;
                    } else if (mouseY >= currentY + 36 && mouseY <= currentY + 54) {
                        AxoHileClient.elytraTargetRange = AxoHileClient.elytraTargetRange >= 150 ? 25 : AxoHileClient.elytraTargetRange + 25;
                    }
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

            int helperBoxY = autoTotemSubMenu ? startY + 95 : startY + 55;
            if (mouseX >= startX && mouseX <= startX + 250 && mouseY >= helperBoxY && mouseY <= helperBoxY + 30) {
                if (button == 0) AxoHileClient.elytraHelperEnabled = !AxoHileClient.elytraHelperEnabled;
                else if (button == 1) { elytraHelperSubMenu = !elytraHelperSubMenu; autoTotemSubMenu = false; }
            }

            if (elytraHelperSubMenu && mouseX >= startX && mouseX <= startX + 250 && mouseY >= helperBoxY + 32 && mouseY <= helperBoxY + 95) {
                if (mouseY >= helperBoxY + 35 && mouseY <= helperBoxY + 60) {
                    awaitingKeyBind = true;
                    moduleToBind = "ElytraHelperSwap";
                } else if (mouseY >= helperBoxY + 62 && mouseY <= helperBoxY + 90) {
                    awaitingKeyBind = true;
                    moduleToBind = "ElytraHelperFirework";
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
            else if (moduleToBind.equals("Triggerbot")) AxoHileClient.triggerbotKey = keyCode;
            else if (moduleToBind.equals("ElytraTarget")) AxoHileClient.elytraKey = keyCode;
            else if (moduleToBind.equals("ElytraHelperSwap")) AxoHileClient.elytraHelperSwapKey = keyCode;
            else if (moduleToBind.equals("ElytraHelperFirework")) AxoHileClient.elytraHelperFireworkKey = keyCode;
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
