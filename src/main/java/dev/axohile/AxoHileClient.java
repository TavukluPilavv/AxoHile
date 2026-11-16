package dev.axohile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AxoHileClient implements ClientModInitializer {
    public static KeyBinding menuKey;
    
    // --- HİLELER ---
    public static boolean killauraEnabled = false;
    public static boolean elytraFlightEnabled = false;

    // --- KEYBINDLER ---
    public static int attackAuraKey = -1;
    public static int elytraKey = -1;

    // --- ATTACK AURA AYARLARI ---
    public static double reach = 4.2;
    public static boolean fullHitEnabled = false;         
    public static boolean shieldBreakerAxeMode = false;    
    public static String attackBypassType = "Vulcan";     

    // --- ELYTRA TARGET AYARLARI ---
    public static String elytraBypassType = "Vulcan";     
    public static int autoFireworkSpeed = 5;              

    // --- TEMA ---
    public static int themeColor = 0xFF5555FF;

    // --- HUD ---
    public static boolean hudStaffList = true;
    public static boolean hudKeybinds = true;
    public static boolean hudPotions = true;
    public static boolean hudMainMenu = true;
    public static boolean hudTargetHud = true; // TargetHUD Açık/Kapalı

    public static int hudX = 15;
    public static int hudY = 15;

    private static int previousSlot = -1;                 

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.axohile.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.axohile.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new AxoHileMenuScreen());
                }
            }

            if (client.player != null && client.currentScreen == null) {
                if (attackAuraKey != -1 && GLFW.glfwGetKey(client.getWindow().getHandle(), attackAuraKey) == GLFW.GLFW_PRESS) {
                    killauraEnabled = !killauraEnabled;
                }
                if (elytraKey != -1 && GLFW.glfwGetKey(client.getWindow().getHandle(), elytraKey) == GLFW.GLFW_PRESS) {
                    elytraFlightEnabled = !elytraFlightEnabled;
                }
            }

            if (client.player != null && client.world != null) {
                if (elytraFlightEnabled) {
                    runElytraFlight(client);
                }
                if (killauraEnabled) {
                    runAdvancedCombat(client);
                }
            }
        });

        // HUD Render & TargetHUD
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            int x = hudX;
            int y = hudY;

            // 1. Ana Menü HUD
            if (hudMainMenu) {
                int fps = client.getCurrentFps();
                String playerName = client.player.getName().getString();
                context.fill(x, y, x + 150, y + 36, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "⭐ VexionClient §7| §f" + playerName, x + 6, y + 6, 0xFFFFFF);
                context.drawTextWithShadow(client.textRenderer, "🟢 FPS: §f" + fps + " §7| 🏓 12ms §7| 🌍 local", x + 6, y + 21, 0xFFFFFF);
                y += 42;
            }

            // 2. Keybinds HUD
            boolean hasKeybinds = (killauraEnabled && attackAuraKey != -1) || (elytraFlightEnabled && elytraKey != -1);
            if (hudKeybinds && hasKeybinds) {
                context.fill(x, y, x + 150, y + 45, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "⌨️ Keybinds", x + 6, y + 6, 0xFFAAAAAA);
                int kbY = y + 20;
                if (killauraEnabled && attackAuraKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fAttackAura", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(attackAuraKey, 0) + "]", x + 100, kbY, 0x55FF55);
                    kbY += 14;
                }
                if (elytraFlightEnabled && elytraKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fElytraTarget", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(elytraKey, 0) + "]", x + 100, kbY, 0x55FF55);
                }
                y += 50;
            }

            // 3. Staff List HUD
            if (hudStaffList) {
                context.fill(x, y, x + 150, y + 30, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "📋 Staff List (0 aktif)", x + 6, y + 8, 0xFFAAAAAA);
                y += 36;
            }

            // 4. Potions HUD
            if (hudPotions) {
                context.fill(x, y, x + 150, y + 30, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "🧪 Aktif İksir Yok", x + 6, y + 8, 0xFFAAAAAA);
                y += 36;
            }

            // 5. TargetHUD (Hedef Mob/Oyuncu Adı ve Can Barı)
            if (hudTargetHud) {
                LivingEntity target = getBestTarget(client, 8.0); // Yakındaki hedefi algılar
                if (target != null) {
                    int tX = client.getWindow().getScaledWidth() / 2 - 75;
                    int tY = client.getWindow().getScaledHeight() / 2 + 25; // Ekranın ortasında alt kısımda

                    String targetName = target.getName().getString();
                    float health = target.getHealth();
                    float maxHealth = target.getMaxHealth();
                    int healthWidth = (int) ((health / maxHealth) * 130);

                    // Arkaplan Kutusu
                    context.fill(tX, tY, tX + 150, tY + 45, 0x90000000);
                    // Hedef Adı ve Can Miktarı
                    context.drawTextWithShadow(client.textRenderer, "🎯 " + targetName, tX + 8, tY + 8, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "❤ " + (int)health + "/" + (int)maxHealth, tX + 100, tY + 8, 0xFF5555);

                    // Can Barı (Health Bar)
                    context.fill(tX + 8, tY + 28, tX + 142, tY + 38, 0xFF333333);
                    if (healthWidth > 0) {
                        context.fill(tX + 8, tY + 28, tX + 8 + healthWidth, tY + 38, themeColor);
                    }
                }
            }
        });

        // Sarsıntısız Kamera & Hit Göstergesi
        WorldRenderEvents.LAST.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.world != null && killauraEnabled) {
                LivingEntity target = getBestTarget(client, reach);
                if (target != null) {
                    boolean inRange = client.player.distanceTo(target) <= reach;
                    renderTargetIndicator(target, inRange);
                }
            }
        });
    }

    private static void runAdvancedCombat(MinecraftClient client) {
        LivingEntity target = getBestTarget(client, reach);
        if (target != null) {
            aimAtTargetSmooth(client.player, target, attackBypassType);

            boolean isWPressed = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;
            if (isWPressed) {
                trackTargetMovement(client.player, target);
            }

            handleShieldBreaker(client, target);

            if (fullHitEnabled) {
                if (!client.player.isOnGround() && client.player.fallDistance > 0.0f) {
                    if (client.player.getAttackCooldownProgress(0.5f) >= 0.92f) {
                        client.interactionManager.attackEntity(client.player, target);
                        client.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            } else {
                if (client.player.getAttackCooldownProgress(0.5f) >= 0.92f) {
                    client.interactionManager.attackEntity(client.player, target);
                    client.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            restorePreviousItem(client);
        }
    }

    private static LivingEntity getBestTarget(MinecraftClient client, double range) {
        LivingEntity closest = null;
        double minDist = range;
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != client.player && entity.isAlive() && !entity.isSpectator()) {
                double dist = client.player.distanceTo(entity);
                if (dist <= minDist) {
                    minDist = dist;
                    closest = (LivingEntity) entity;
                }
            }
        }
        return closest;
    }

    private static void renderTargetIndicator(LivingEntity target, boolean inRange) {
        // Hedef üstündeki hit çemberi
    }

    private static void handleShieldBreaker(MinecraftClient client, LivingEntity target) {
        boolean targetIsBlocking = target.isUsingItem() && target.getActiveItem().isOf(Items.SHIELD);

        if (targetIsBlocking) {
            if (shieldBreakerAxeMode) {
                for (int i = 0; i < 9; i++) {
                    if (client.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                        if (previousSlot == -1 && !(client.player.getInventory().getMainHandStack().getItem() instanceof AxeItem)) {
                            previousSlot = client.player.getInventory().selectedSlot;
                        }
                        client.player.getInventory().selectedSlot = i;
                        return;
                    }
                }
            } else {
                restorePreviousItem(client);
            }
        } else {
            restorePreviousItem(client);
        }
    }

    private static void restorePreviousItem(MinecraftClient client) {
        if (previousSlot != -1) {
            client.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }

    private static void runElytraFlight(MinecraftClient client) {
        Entity target = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != client.player && entity.isAlive()) {
                double dist = client.player.distanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = entity;
                }
            }
        }

        boolean isGlidingSafely = !client.player.isOnGround() && client.player.fallDistance > 1.0f;

        if (target != null && isGlidingSafely) {
            aimAtTargetSmooth(client.player, (LivingEntity) target, elytraBypassType);
        }

        int tickInterval = Math.max(2, 22 - (autoFireworkSpeed * 2)); 
        if (isGlidingSafely && client.player.age % tickInterval == 0) {
            useFirework(client);
        }
    }

    private static void useFirework(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;
        int fireworkSlot = -1;

        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getStack(i).isOf(Items.FIREWORK_ROCKET)) {
                fireworkSlot = i;
                break;
            }
        }

        if (fireworkSlot == -1) {
            for (int i = 9; i < 36; i++) {
                if (client.player.getInventory().getStack(i).isOf(Items.FIREWORK_ROCKET)) {
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, 0, SlotActionType.SWAP, client.player);
                    fireworkSlot = 0;
                    break;
                }
            }
        }

        if (fireworkSlot != -1) {
            int oldSlot = client.player.getInventory().selectedSlot;
            client.player.getInventory().selectedSlot = fireworkSlot;
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
            client.player.getInventory().selectedSlot = oldSlot;
        }
    }

    private static void aimAtTargetSmooth(PlayerEntity player, LivingEntity target, String bypass) {
        Vec3d eyes = player.getEyePos();
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.5, 0);

        double diffX = targetPos.x - eyes.x;
        double diffY = targetPos.y - eyes.y;
        double diffZ = targetPos.z - eyes.z;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0F;
        float targetPitch = (float) -(Math.atan2(diffY, dist) * (180 / Math.PI));

        float yawDelta = wrapDegrees(targetYaw - player.getYaw());
        float pitchDelta = targetPitch - player.getPitch();

        float smoothing = bypass.equals("Grim") ? 0.30f : 0.60f;

        player.setYaw(player.getYaw() + yawDelta * smoothing);
        player.setPitch(player.getPitch() + pitchDelta * smoothing);
    }

    private static void trackTargetMovement(PlayerEntity player, LivingEntity target) {
        Vec3d targetPos = target.getPos();
        Vec3d playerPos = player.getPos();
        double diffX = targetPos.x - playerPos.x;
        double diffZ = targetPos.z - playerPos.z;
        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0F;
        player.setYaw(targetYaw);
    }

    private static float wrapDegrees(float value) {
        value = value % 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }
}
