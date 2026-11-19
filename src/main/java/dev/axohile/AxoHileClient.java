package dev.axohile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AxoHileClient implements ClientModInitializer {
    public static KeyBinding menuKey;

    // --- HİLELER ---
    public static boolean killauraEnabled = false;
    public static boolean triggerbotEnabled = false;
    public static boolean elytraFlightEnabled = false;
    public static boolean autoTotemEnabled = false;
    public static boolean elytraHelperEnabled = false;

    // --- KEYBINDLER ---
    public static int attackAuraKey = -1;
    public static int triggerbotKey = -1;
    public static int elytraKey = -1;
    public static int elytraHelperSwapKey = -1;
    public static int elytraHelperFireworkKey = -1;

    // --- ATTACK AURA, TRIGGERBOT & COMBAT AYARLARI ---
    public static double reach = 4.2;
    public static double hitSpeed = 5.5;                  // 0.0 - 10.0 arası (0.2 adım), 5.5 = Tam Hit
    public static boolean criticalHitEnabled = false;
    public static boolean fullHitEnabled = false;
    public static boolean shieldBreakerAxeMode = false;
    public static String attackBypassType = "Vulcan";
    public static boolean triggerbotHitOnlyWhenJumping = false;

    // --- TARGET LOCK & SERBEST KAMERA AYARLARI ---
    public static boolean targetLockEnabled = true;
    public static boolean freeCameraEnabled = false;

    // --- ELYTRA TARGET & HELPER AYARLARI ---
    public static String elytraBypassType = "Vulcan";
    public static int autoFireworkSpeed = 5;
    public static int elytraTargetRange = 100;

    // --- AUTOTOTEM AYARLARI ---
    public static int autoTotemHealth = 3;
    private static ItemStack savedOffhandStack = ItemStack.EMPTY;
    private static boolean restoredOffhand = true;

    // --- TEMA & HUD ---
    public static int themeColor = 0xFF5555FF;
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

    // --- MESAJ & UYARI SİSTEMLERİ ---
    public static String inventoryMessage = "";
    private static int inventoryMessageTimer = 0;

    public static List<String> accountList = new ArrayList<>();
    public static String selectedAccount = "";
    public static String typedAccountInput = "";

    public static void saveOfflineAccounts() {}

    private static int previousSlot = -1;
    private static int welcomeTimer = 0;
    private static boolean lastSwapKeyState = false;
    private static boolean lastFireworkKeyState = false;
    private static int hitTickCooldown = 0;
    private static float orbitAngle = 0.0f;
    private static boolean fullHitJumpingPhase = false;

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
                long window = client.getWindow().getHandle();

                if (attackAuraKey != -1 && GLFW.glfwGetKey(window, attackAuraKey) == GLFW.GLFW_PRESS) {
                    killauraEnabled = !killauraEnabled;
                }
                if (triggerbotKey != -1 && GLFW.glfwGetKey(window, triggerbotKey) == GLFW.GLFW_PRESS) {
                    triggerbotEnabled = !triggerbotEnabled;
                }
                if (elytraKey != -1 && GLFW.glfwGetKey(window, elytraKey) == GLFW.GLFW_PRESS) {
                    elytraFlightEnabled = !elytraFlightEnabled;
                }

                if (elytraHelperEnabled) {
                    if (elytraHelperSwapKey != -1) {
                        boolean isPressed = GLFW.glfwGetKey(window, elytraHelperSwapKey) == GLFW.GLFW_PRESS;
                        if (isPressed && !lastSwapKeyState) {
                            toggleElytraAndChestplate(client);
                        }
                        lastSwapKeyState = isPressed;
                    }

                    if (elytraHelperFireworkKey != -1) {
                        boolean isPressed = GLFW.glfwGetKey(window, elytraHelperFireworkKey) == GLFW.GLFW_PRESS;
                        if (isPressed && !lastFireworkKeyState) {
                            useFireworkAnywhere(client);
                        }
                        lastFireworkKeyState = isPressed;
                    }
                }
            }

            if (client.player != null && client.world != null) {
                if (welcomeTimer < 100) welcomeTimer++;
                if (inventoryMessageTimer > 0) inventoryMessageTimer--;
                if (hitTickCooldown > 0) hitTickCooldown--;

                if (autoTotemEnabled) {
                    runAutoTotem(client);
                }
                if (elytraFlightEnabled) {
                    runElytraFlight(client);
                }
                if (killauraEnabled || triggerbotEnabled) {
                    runAdvancedCombat(client);
                } else {
                    fullHitJumpingPhase = false;
                }
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            if (inventoryMessageTimer > 0 && !inventoryMessage.isEmpty()) {
                int sw = client.getWindow().getScaledWidth();
                int sh = client.getWindow().getScaledHeight();
                context.drawCenteredTextWithShadow(client.textRenderer, inventoryMessage, sw / 2, sh / 2 + 40, 0xFFFF5555);
            }

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

            boolean hasKeybinds = (killauraEnabled && attackAuraKey != -1) || (triggerbotEnabled && triggerbotKey != -1) || (elytraFlightEnabled && elytraKey != -1);
            if (hudKeybinds && hasKeybinds) {
                context.fill(x, y, x + 160, y + 58, 0x90000000);
                context.drawTextWithShadow(client.textRenderer, "⌨️ Keybinds", x + 6, y + 6, 0xFFAAAAAA);
                int kbY = y + 20;
                if (killauraEnabled && attackAuraKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fAttackAura", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(attackAuraKey, 0) + "]", x + 110, kbY, 0x55FF55);
                    kbY += 12;
                }
                if (triggerbotEnabled && triggerbotKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fTriggerbot", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(triggerbotKey, 0) + "]", x + 110, kbY, 0x55FF55);
                    kbY += 12;
                }
                if (elytraFlightEnabled && elytraKey != -1) {
                    context.drawTextWithShadow(client.textRenderer, "§fElytraTarget", x + 10, kbY, 0xFFFFFF);
                    context.drawTextWithShadow(client.textRenderer, "[" + GLFW.glfwGetKeyName(elytraKey, 0) + "]", x + 110, kbY, 0x55FF55);
                }
                y += 64;
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
            if (client.player != null && client.world != null && (killauraEnabled || triggerbotEnabled)) {
                LivingEntity target = getBestTarget(client, reach);
                if (target != null) {
                    boolean inRange = client.player.distanceTo(target) <= reach;
                    renderTargetIndicator(context, target, inRange);
                }
            }
        });
    }

    private static float getRequiredCooldownThreshold() {
        // 0.0 -> 1.15 (daha uzun bekleme), 5.5 -> 0.98 (Tam Hit), 10.0 -> 0.50 (hızlı vuruş)
        if (Math.abs(hitSpeed - 5.5) < 0.02) {
            return 0.98f;
        }
        double delta = hitSpeed - 5.5;
        float threshold = (float) (0.98 - (delta * 0.095));
        return Math.max(0.45f, Math.min(1.20f, threshold));
    }

    private static int getExtraCooldownTicks() {
        // Düşük hitSpeed değerlerinde ek bekleme tick'i ekler
        if (hitSpeed >= 5.5) {
            return 0;
        }
        double slowFactor = (5.5 - hitSpeed) / 0.2; // 0.2 adımlık kademe sayısı
        return Math.max(0, (int) Math.round(slowFactor * 1.2));
    }

    private static boolean canPerformCritical(PlayerEntity player) {
        return !player.isOnGround()
                && player.getVelocity().y < -0.04
                && player.fallDistance > 0.03f
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.hasVehicle()
                && !player.isGliding();
    }

    private static void applyTargetOrbitAndChestAim(MinecraftClient client, LivingEntity target) {
        PlayerEntity player = client.player;
        if (player == null || target == null) return;

        // Hedefin göğüs hizasına odaklan ve etrafında 360° / içinden geçip çıkacak şekilde orbit uygula
        orbitAngle += 0.28f;
        if (orbitAngle > 360.0f) orbitAngle -= 360.0f;

        Vec3d targetChest = target.getPos().add(0, target.getHeight() * 0.55, 0);
        double rad = Math.toRadians(orbitAngle);
        // Hedefin içine girip çıkacak dinamik yarıçap (0.0 ile 0.85 blok arası)
        double radius = 0.42 * (1.0 + Math.sin(rad * 2.0));
        double desiredX = target.getX() + Math.cos(rad) * radius;
        double desiredZ = target.getZ() + Math.sin(rad) * radius;

        // Sadece targetLockEnabled açıkken ve yatay/dikey konum yaklaşımı isteniyorsa yumuşak kaydır
        double moveFactor = attackBypassType.equals("Grim") ? 0.14 : 0.22;
        double nextX = player.getX() + (desiredX - player.getX()) * moveFactor;
        double nextZ = player.getZ() + (desiredZ - player.getZ()) * moveFactor;
        player.setPosition(nextX, player.getY(), nextZ);

        Vec3d eyes = player.getEyePos();
        double diffX = targetChest.x - eyes.x;
        double diffY = targetChest.y - eyes.y;
        double diffZ = targetChest.z - eyes.z;
        double horizDist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180.0 / Math.PI)) - 90.0F;
        float targetPitch = (float) -(Math.atan2(diffY, horizDist) * (180.0 / Math.PI));

        // 1. şahısta serbest kamera açıkken kamerayı kilitleme; kullanıcı ekranı serbestçe çevirsin
        boolean isFirstPerson = client.options.getPerspective().isFirstPerson();
        if (freeCameraEnabled && isFirstPerson) {
            // Hedefe bakış açısını gövde/kafa yönüne yumuşak yansıt ama kamera (player yaw/pitch) kullanıcıda kalsın
            player.setBodyYaw(targetYaw);
            player.setHeadYaw(targetYaw);
            return;
        }

        float yawDelta = wrapDegrees(targetYaw - player.getYaw());
        float pitchDelta = targetPitch - player.getPitch();
        float smoothing = attackBypassType.equals("Grim") ? 0.22f : 0.35f;

        player.setYaw(player.getYaw() + yawDelta * smoothing);
        player.setPitch(Math.max(-90.0f, Math.min(90.0f, player.getPitch() + pitchDelta * smoothing)));
    }

    private static void toggleElytraAndChestplate(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        ItemStack chestStack = client.player.getInventory().getArmorStack(2);
        boolean isElytraEquipped = chestStack.isOf(Items.ELYTRA);

        if (!isElytraEquipped) {
            int elytraSlot = -1;
            for (int i = 0; i < 36; i++) {
                if (client.player.getInventory().getStack(i).isOf(Items.ELYTRA)) {
                    elytraSlot = i;
                    break;
                }
            }

            if (elytraSlot != -1) {
                int slotId = elytraSlot < 9 ? elytraSlot + 36 : elytraSlot;
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, slotId, 0, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, slotId, 0, SlotActionType.PICKUP, client.player);
            } else {
                inventoryMessage = "VexionClient: Envanterinizde Elytra yok !";
                inventoryMessageTimer = 60;
            }
        } else {
            int chestplateSlot = -1;
            for (int i = 0; i < 36; i++) {
                ItemStack stack = client.player.getInventory().getStack(i);
                if (stack.getItem() instanceof ArmorItem) {
                    String itemName = stack.getItem().toString().toLowerCase();
                    if (itemName.contains("chestplate") || itemName.contains("tunic") || itemName.contains("jacket")) {
                        chestplateSlot = i;
                        break;
                    }
                }
            }

            if (chestplateSlot != -1) {
                int slotId = chestplateSlot < 9 ? chestplateSlot + 36 : chestplateSlot;
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, slotId, 0, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, slotId, 0, SlotActionType.PICKUP, client.player);
            } else {
                inventoryMessage = "VexionClient: Envanterinizde göğüslük yok !";
                inventoryMessageTimer = 60;
            }
        }
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

    private static void runAdvancedCombat(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        LivingEntity target = getBestTarget(client, reach);
        if (target == null) {
            restorePreviousItem(client);
            fullHitJumpingPhase = false;
            return;
        }

        if (targetLockEnabled) {
            applyTargetOrbitAndChestAim(client, target);
        }

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

        float requiredCooldown = getRequiredCooldownThreshold();
        boolean isCooldownReady = client.player.getAttackCooldownProgress(0.5f) >= requiredCooldown && hitTickCooldown <= 0;
        boolean isFallingCrit = canPerformCritical(client.player);
        boolean onlyJumpMode = triggerbotHitOnlyWhenJumping || (triggerbotEnabled && !killauraEnabled);

        if (fullHitEnabled) {
            if (client.player.isOnGround() && isCooldownReady && !fullHitJumpingPhase) {
                client.player.jump();
                fullHitJumpingPhase = true;
                return;
            }
            if (isFallingCrit && isCooldownReady) {
                executeAttack(client, target);
                fullHitJumpingPhase = false;
            } else if (client.player.isOnGround() && !isCooldownReady) {
                fullHitJumpingPhase = false;
            }
            return;
        }

        fullHitJumpingPhase = false;

        if (criticalHitEnabled) {
            if (onlyJumpMode && client.player.isOnGround() && isCooldownReady) {
                client.player.jump();
                return;
            }
            if (isFallingCrit && isCooldownReady) {
                executeAttack(client, target);
            }
            return;
        }

        if (onlyJumpMode) {
            if (client.player.isOnGround()) {
                if (triggerbotEnabled && isCooldownReady) {
                    client.player.jump();
                }
                return;
            }
            if (isFallingCrit && isCooldownReady) {
                executeAttack(client, target);
            }
            return;
        }

        if (isCooldownReady) {
            executeAttack(client, target);
        }
    }

    private static void executeAttack(MinecraftClient client, LivingEntity target) {
        client.player.setSprinting(false);
        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
        hitTickCooldown = getExtraCooldownTicks();
        restorePreviousItem(client);
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

    private static void renderTargetIndicator(WorldRenderContext context, LivingEntity target, boolean inRange) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || target == null) return;

        net.minecraft.client.util.math.MatrixStack matrices = context.matrixStack();
        net.minecraft.client.render.Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        Box box = target.getBoundingBox().offset(-camPos.x, -camPos.y, -camPos.z);

        matrices.push();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer buffer = immediate.getBuffer(RenderLayer.getLines());

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        int color = inRange ? themeColor : 0xFFFF5555;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;

        float f = 0.28f;
        float dx = (maxX - minX) * f;
        float dy = (maxY - minY) * f;
        float dz = (maxZ - minZ) * f;

        drawLine(matrix, buffer, minX, minY, minZ, minX + dx, minY, minZ, r, g, b, a);
        drawLine(matrix, buffer, minX, minY, minZ, minX, minY + dy, minZ, r, g, b, a);
        drawLine(matrix, buffer, minX, minY, minZ, minX, minY, minZ + dz, r, g, b, a);

        drawLine(matrix, buffer, maxX, minY, minZ, maxX - dx, minY, minZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, minY, minZ, maxX, minY + dy, minZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, minY, minZ, maxX, minY, minZ + dz, r, g, b, a);

        drawLine(matrix, buffer, minX, minY, maxZ, minX + dx, minY, maxZ, r, g, b, a);
        drawLine(matrix, buffer, minX, minY, maxZ, minX, minY + dy, maxZ, r, g, b, a);
        drawLine(matrix, buffer, minX, minY, maxZ, minX, minY, maxZ - dz, r, g, b, a);

        drawLine(matrix, buffer, maxX, minY, maxZ, maxX - dx, minY, maxZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, minY, maxZ, maxX, minY + dy, maxZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, minY, maxZ, maxX, minY, maxZ - dz, r, g, b, a);

        drawLine(matrix, buffer, minX, maxY, minZ, minX + dx, maxY, minZ, r, g, b, a);
        drawLine(matrix, buffer, minX, maxY, minZ, minX, maxY - dy, minZ, r, g, b, a);
        drawLine(matrix, buffer, minX, maxY, minZ, minX, maxY, minZ + dz, r, g, b, a);

        drawLine(matrix, buffer, maxX, maxY, minZ, maxX - dx, maxY, minZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, maxY, minZ, maxX, maxY - dy, minZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, maxY, minZ, maxX, maxY, minZ + dz, r, g, b, a);

        drawLine(matrix, buffer, minX, maxY, maxZ, minX + dx, maxY, maxZ, r, g, b, a);
        drawLine(matrix, buffer, minX, maxY, maxZ, minX, maxY - dy, maxZ, r, g, b, a);
        drawLine(matrix, buffer, minX, maxY, maxZ, minX, maxY, maxZ - dz, r, g, b, a);

        drawLine(matrix, buffer, maxX, maxY, maxZ, maxX - dx, maxY, maxZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, maxY, maxZ, maxX, maxY - dy, maxZ, r, g, b, a);
        drawLine(matrix, buffer, maxX, maxY, maxZ, maxX, maxY, maxZ - dz, r, g, b, a);

        immediate.draw();
        matrices.pop();
    }

    private static void drawLine(Matrix4f matrix, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0, 1, 0);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0, 1, 0);
    }

    private static void restorePreviousItem(MinecraftClient client) {
        if (previousSlot != -1) {
            client.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }

    private static void runElytraFlight(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) return;

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

        if (target != null && targetLockEnabled) {
            aimAtTargetSmooth(client, client.player, target, elytraBypassType);
        }

        int tickInterval = Math.max(2, 22 - (autoFireworkSpeed * 2));
        if ((client.player.isGliding() || client.player.fallDistance > 0.4f) && client.player.age % tickInterval == 0) {
            useFireworkAnywhere(client);
        }
    }

    public static void useFireworkAnywhere(MinecraftClient client) {
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

    private static void aimAtTargetSmooth(MinecraftClient client, PlayerEntity player, LivingEntity target, String bypass) {
        Vec3d eyes = player.getEyePos();
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.55, 0);

        double diffX = targetPos.x - eyes.x;
        double diffY = targetPos.y - eyes.y;
        double diffZ = targetPos.z - eyes.z;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0F;
        float targetPitch = (float) -(Math.atan2(diffY, dist) * (180 / Math.PI));

        if (freeCameraEnabled && client.options.getPerspective().isFirstPerson()) {
            player.setBodyYaw(targetYaw);
            player.setHeadYaw(targetYaw);
            return;
        }

        float yawDelta = wrapDegrees(targetYaw - player.getYaw());
        float pitchDelta = targetPitch - player.getPitch();

        float smoothing = bypass.equals("Grim") ? 0.14f : 0.24f;

        player.setYaw(player.getYaw() + yawDelta * smoothing);
        player.setPitch(Math.max(-90.0f, Math.min(90.0f, player.getPitch() + pitchDelta * smoothing)));
    }

    private static float wrapDegrees(float value) {
        value = value % 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }
}

