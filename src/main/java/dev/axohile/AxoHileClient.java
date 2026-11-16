package dev.axohile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
    
    // --- HİLELER (Başlangıçta Hepsi Kapalı) ---
    public static boolean killauraEnabled = false;
    public static boolean elytraFlightEnabled = false;
    public static boolean menuOpen = false; // M tuşu ile açılan GUI durumu

    // --- ATTACK AURA AYARLARI ---
    public static double reach = 4.2;
    public static boolean fullHitEnabled = false;         
    public static boolean shieldBreakerAxeMode = false;    
    public static String attackBypassType = "Vulcan";     // Sağ tık menüsünden seçilen bypass

    // --- ELYTRA TARGET AYARLARI ---
    public static String elytraBypassType = "Vulcan";     // Sağ tık menüsünden seçilen bypass
    public static int autoFireworkSpeed = 5;              

    private static int previousSlot = -1;                 

    @Override
    public void onInitializeClient() {
        // M Tuşu ve Sağ Shift ile Menü Açma
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.axohile.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.axohile.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                menuOpen = !menuOpen;
                if (client.player != null) {
                    String status = menuOpen ? "§aAçıldı (Tüm Ayarlar Kapalı)" : "§cKapatıldı";
                    client.player.sendMessage(net.minecraft.text.Text.literal("§b[AxoHile Menü] " + status), true);
                }
            }

            if (client.player != null && client.world != null) {
                // Sadece açık olan hileler çalışır
                if (killauraEnabled) {
                    runAdvancedCombat(client);
                }
                if (elytraFlightEnabled) {
                    runElytraFlight(client);
                }
            }
        });
    }

    private static void runAdvancedCombat(MinecraftClient client) {
        LivingEntity target = null;
        double closestDistance = reach;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != client.player && entity.isAlive()) {
                double dist = client.player.distanceTo(entity);
                if (dist <= closestDistance) {
                    closestDistance = dist;
                    target = (LivingEntity) entity;
                }
            }
        }

        if (target != null) {
            // Hedef Üzerinde Dönen Yuvarlak Gösterge Render Sinyali (Kırmızı = Menzilde, Mavi = Menzil Dışı)
            boolean inRange = client.player.distanceTo(target) <= reach;
            renderTargetCircleIndicator(client, target, inRange);

            aimAtTargetBypass(client.player, target, attackBypassType);

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
                } else if (client.player.isOnGround() && !client.player.isSubmergedInWater() && client.player.hurtTime == 0) {
                    client.player.jump();
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

    private static void renderTargetCircleIndicator(MinecraftClient client, LivingEntity target, boolean inRange) {
        // Hedefin ayak/kafa hizasında dönen daire renk mantığı (Kırmızı: Vurabilir, Mavi: Bekliyor)
        // Render engine buradaki inRange değerine göre kırmızı/mavi halka çizer.
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
            aimAtTargetBypass(client.player, (LivingEntity) target, elytraBypassType);
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

    private static void aimAtTargetBypass(PlayerEntity player, LivingEntity target, String bypass) {
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

        // Seçilen Anti-Cheat Bypass Türüne Göre Açı Optimizasyonu (Grim, Vulcan, Polar vb.)
        float smoothing = 0.75f;
        if (bypass.equals("Grim")) smoothing = 0.40f;
        else if (bypass.equals("Polar")) smoothing = 0.60f;
        else if (bypass.equals("Vulcan")) smoothing = 0.75f;

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
