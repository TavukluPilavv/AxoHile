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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AxoHileClient implements ClientModInitializer {
    public static KeyBinding menuKey;
    
    // --- HİLELER (BAŞLANGIÇTA KAPALI) ---
    public static boolean killauraEnabled = false;
    public static boolean elytraFlightEnabled = false;
    public static boolean autoTotemEnabled = false;

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
    public static int elytraTargetRange = 100;            

    // --- AUTOTOTEM AYARLARI (3 Kalp = 6 Can) ---
    public static int autoTotemHealth = 3; 
    private static ItemStack savedOffhandStack = ItemStack.EMPTY;
    private static boolean restoredOffhand = true;

    // --- TEMA ---
    public static int themeColor = 0xFF5555FF;

    // --- HUD (BAŞLANGIÇTA KAPALI) ---
    public static boolean hudStaffList = false;
    public static boolean hudKeybinds = false;
    public static boolean hudPotions = false;
    public static boolean hudMainMenu = false;
    public static boolean hudTargetHud = false;

    public static int hudX = 15;
    public static int hudY = 15;
    private static boolean isDraggingHud = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;

    // --- ACCOUNTS ---
    public static List<String> accountList = new ArrayList<>();
    public static String selectedAccount = "";
    public static String typedAccountInput = "";

    private static int previousSlot = -1;                 
    private static int welcomeTimer = 0;

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
                if (welcomeTimer < 100) welcomeTimer++;

                if (autoTotemEnabled) {
                    runAutoTotem(client);
                }
                if (elytraFlightEnabled) {
                    runElytraFlight(client);
                }
                if (killauraEnabled) {
                    runAdvancedCombat(client);
                }
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            if (welcomeTimer < 80) {
                int sw = client.getWindow().getScaledWidth();
                int sh = client.getWindow().getScaledHeight();
                context.drawCenteredTextWithShadow(client.textRenderer, "§b§lVexionClient", sw / 2, sh / 2 - 30, 0xFFFFFF);
            }

            boolean isShiftPressed = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
            boolean isLeftMousePressed = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            double mouseX = client.mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();

            if (isShiftPressed && isLeftMousePressed && client.currentScreen == null) {
                if (!isDraggingHud) {
                    if (mouseX >= hudX && mouseX <= hudX + 160 && mouseY >= hudY && mouseY <= hudY + 120) {
                        isDraggingHud = true;
                        dragOffsetX = (int)mouseX - hudX;
                        dragOffsetY = (int)mouseY - hudY;
                    }
                } else {
                    hudX = (int)mouseX - dragOffsetX;
                    hudY = (int)mouseY - dragOffsetY;
                }
            } else {
                isDraggingHud = false;
            }

            int x = hudX;
            int y = hudY;

            if (hudMainMenu) {
                int fps = client.getCurrentFps();
                String playerName = client.player.getName().getString();
                context.fill(x, y, x + 160, y + 38, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "👤 §bVexionClient §7| §f" + playerName, x + 6, y + 6, 0xFFFFFF);
                context.drawTextWithShadow(client.textRenderer, "🖥️ §e" + fps + " FPS §7| 📶 12ms §7| 🌍 local", x + 6, y + 22, 0xFFFFFF);
                y += 44;
            }

            boolean hasKeybinds = (killauraEnabled && attackAuraKey != -1) || (elytraFlightEnabled && elytraKey != -1);
            if (hudKeybinds && hasKeybinds) {
                context.fill(x, y, x + 160, y + 45, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "⌨️ Keybinds", x + 6, y + 6, 0xFFAAAAAA);
                int kbY = y + 20;
                if (killauraEnabled && attackAuraKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fAttackAura", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(attackAuraKey, 0) + "]", x + 110, kbY, 0x55FF55);
                    kbY += 14;
                }
                if (elytraFlightEnabled && elytraKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fElytraTarget", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(elytraKey, 0) + "]", x + 110, kbY, 0x55FF55);
                }
                y += 50;
            }

            if (hudStaffList) {
                context.fill(x, y, x + 160, y + 30, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "📋 Staff List (0 aktif)", x + 6, y + 8, 0xFFAAAAAA);
                y += 36;
            }

            if (hudPotions && !client.player.getStatusEffects().isEmpty()) {
                int potHeight = 25 + (client.player.getStatusEffects().size() * 15);
                context.fill(x, y, x + 160, y + potHeight, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "🧪 Aktif İksirler", x + 6, y + 6, 0xFFAAAAAA);
                int pY = y + 20;
                for (net.minecraft.entity.effect.StatusEffectInstance effect : client.player.getStatusEffects()) {
                    String name = effect.getEffectType().value().getName().getString();
                    int totalSeconds = effect.getDuration() / 20;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    String timeFormatted = String.format("%d:%02d", minutes, seconds);
                    context.drawTextWithShadow(client.textRenderer, "§f" + name + " §7(" + timeFormatted + ")", x + 10, pY, 0xFFFFFF);
                    pY += 15;
                }
                y += potHeight + 6;
            }

            if (hudTargetHud) {
                LivingEntity target = getBestTarget(client, 8.0);
                if (target != null) {
                    int tX = client.getWindow().getScaledWidth() / 2 - 75;
                    int tY = client.getWindow().getScaledHeight() / 2 + 25;

                    String targetName = target.getName().getString();
                    float health = target.getHealth();
                    float maxHealth = target.getMaxHealth();
                    int healthWidth = (int) ((health / maxHealth) * 130);

                    context.fill(tX, tY, tX + 150, tY + 45, 0x90000000);
                    context.drawTextWithShadow(client.textRenderer, "🎯 " + targetName, tX + 8, tY + 8, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "❤ " + (int)health + "/" + (int)maxHealth, tX + 100, tY + 8, 0xFF5555);

                    context.fill(tX + 8, tY + 28, tX + 142, tY + 38, 0xFF333333);
                    if (healthWidth > 0) {
                        context.fill(tX + 8, tY + 28, tX + 8 + healthWidth, tY + 38, themeColor);
                    }
                }
            }
        });

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

    private static void runAutoTotem(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;
        
        float thresholdHealth = autoTotemHealth * 2.0f;
        if (client.player.getHealth() <= thresholdHealth) {
            if (!client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                savedOffhandStack = client.player.getOffHandStack().copy();
                restoredOffhand = false;
                for (int i = 0; i < 36; i++) {
                    if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i < 9 ? i + 36 : i, 0, SlotActionType.PICKUP, client.player);
                        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, client.player);
                        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i < 9 ? i + 36 : i, 0, SlotActionType.PICKUP, client.player);
                        break;
                    }
                }
            }
        } else {
            if (!restoredOffhand && !savedOffhandStack.isEmpty()) {
                for (int i = 0; i < 36; i++) {
                    if (client.player.getInventory().getStack(i).isEmpty()) {
                        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, client.player);
                        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i < 9 ? i + 36 : i, 0, SlotActionType.PICKUP, client.player);
                        restoredOffhand = true;
                        savedOffhandStack = ItemStack.EMPTY;
                        break;
                    }
                }
            }
        }
    }

    // --- KESİN VE GARANTİLİ KRİTİK VURUŞ (CHATGPT ONAYLI DÜZELTME) ---
    private static void runAdvancedCombat(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        LivingEntity target = getBestTarget(client, reach);
        if (target != null) {
            aimAtTargetSmooth(client.player, target, attackBypassType);

            boolean isWPressed = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;
            if (isWPressed) {
                trackTargetMovement(client.player, target);
            }

            // Kalkan Kırma & Balta Yönetimi
            boolean targetIsBlocking = target.isUsingItem() && target.getActiveItem().isOf(Items.SHIELD);
            if (targetIsBlocking && shieldBreakerAxeMode) {
                for (int i = 0; i < 9; i++) {
                    if (client.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                        if (previousSlot == -1 && !(client.player.getInventory().getMainHandStack().getItem() instanceof AxeItem)) {
                            previousSlot = client.player.getInventory().selectedSlot;
                        }
                        client.player.getInventory().selectedSlot = i;
                        break;
                    }
                }
            } else {
                restorePreviousItem(client);
            }

            // Full Hit ve Cooldown Mantığı
            if (fullHitEnabled) {
                // Yerdeyse zıpla
                if (client.player.isOnGround()) {
                    client.player.jump();
                } else {
                    // Havadayken: ChatGPT'nin haklı tespiti olan aşağı düşüş hızı (velocity.y < 0) ve cooldown kontrolü
                    boolean isFallingDown = client.player.getVelocity().y < -0.05 && client.player.fallDistance > 0.0f;
                    if (isFallingDown && client.player.getAttackCooldownProgress(0.5f) >= 0.85f) {
                        client.player.setSprinting(false);
                        client.interactionManager.attackEntity(client.player, target);
                        client.player.swingHand(Hand.MAIN_HAND);
                        restorePreviousItem(client);
                    }
                }
            } else {
                // Full Hit Kapalıyken: Normal yerde cooldown dolduğunda düz vurur, asla spam yapmaz
                if (client.player.getAttackCooldownProgress(0.5f) >= 0.98f) {
                    client.interactionManager.attackEntity(client.player, target);
                    client.player.swingHand(Hand.MAIN_HAND);
                    restorePreviousItem(client);
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

    private static void renderTargetIndicator(LivingEntity target, boolean inRange) {}

    private static void restorePreviousItem(MinecraftClient client) {
        if (previousSlot != -1) {
            client.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }

    private static void runElytraFlight(MinecraftClient client) {
        LivingEntity target = null;
        double closestDist = (double) elytraTargetRange;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != client.player && entity.isAlive() && !entity.isSpectator()) {
                double dist = client.player.distanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = (LivingEntity) entity;
                }
            }
        }

        boolean isGlidingSafely = !client.player.isOnGround() && client.player.fallDistance > 0.5f;

        if (target != null) {
            aimAtTargetSmooth(client.player, target, elytraBypassType);
        }

        int tickInterval = Math.max(2, 22 - (autoFireworkSpeed * 2)); 
        if (isGlidingSafely && client.player.age % tickInterval == 0) {
            useFireworkAnywhere(client);
        }
    }

    private static void useFireworkAnywhere(MinecraftClient client) {
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
                    int currentSlot = client.player.getInventory().selectedSlot;
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, currentSlot, SlotActionType.SWAP, client.player);
                    fireworkSlot = currentSlot;
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

        float smoothing = bypass.equals("Grim") ? 0.20f : 0.40f;

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
