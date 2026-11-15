package com.axohile;

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
    public static boolean killauraEnabled = true;
    public static boolean elytraFlightEnabled = true;
    public static double reach = 4.0; // Polar/Vulcan bypass için optimal mesafe

    @Override
    public void onInitializeClient() {
        // Sağ Shift tuşu ile mod durumunu kontrol etme
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.axohile.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.axohile.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§b[AxoHile] §aBypass & Combat Aktif! (Sağ Shift)"), true);
                }
            }

            if (client.player != null && client.world != null) {
                if (killauraEnabled) {
                    runAdvancedCombat(client);
                }
                if (elytraFlightEnabled) {
                    runElytraFlight(client);
                }
            }
        });
    }

    // --- POLAR / VULCAN BYPASS DESTEKLİ GELİŞMİŞ COMBAT (KILLAURA) ---
    private static void runAdvancedCombat(MinecraftClient client) {
        LivingEntity target = null;
        double closestDistance = reach;

        // Çevredeki en avantajlı hedefi seçme
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
            // 1. Polar Bypass için Akıllı/Yumuşak Rotasyon (Anında kilitlenmek yerine anti-cheat takibini atlatan delta dönüş)
            aimAtTargetBypass(client.player, target);

            // 2. W Tuşuna basıldığında hedefin arkasını/hareket yönünü takip etme (Movement Lock)
            boolean isWPressed = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;
            if (isWPressed) {
                trackTargetMovement(client.player, target);
            }

            // 3. Auto-Axe (Hedef kalkan açtığı an envanterden baltayı seçip kalkanı kırma)
            handleShieldBreaker(client, target);

            // 4. Auto-Crit (Düşüş/zıplama döngüsü ile sürekli kritik vuruş)
            if (client.player.isOnGround() && !client.player.isFallFlying() && !client.player.isSubmergedInWater() && client.player.hurtTime == 0) {
                client.player.jump();
            }

            // 5. Cooldown Kontrollü Meşru Vuruş (Anti-Cheat attack rate korumalarını aşmak için)
            if (client.player.getAttackCooldownProgress(0.5f) >= 0.92f) {
                client.interactionManager.attackEntity(client.player, target);
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    // Anti-cheat rotation check'lerine (Polar Aim/Angle) takılmamak için optimize edilmiş rotasyon
    private static void aimAtTargetBypass(PlayerEntity player, LivingEntity target) {
        Vec3d eyes = player.getEyePos();
        // Hedefin tam merkezine veya gövdesine odaklanma
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.5, 0);

        double diffX = targetPos.x - eyes.x;
        double diffY = targetPos.y - eyes.y;
        double diffZ = targetPos.z - eyes.z;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0F;
        float targetPitch = (float) -(Math.atan2(diffY, dist) * (180 / Math.PI));

        // Ani snap yasaklayan anti-cheatler için yumuşatma (Interpolation) katsayısı
        float yawDelta = wrapDegrees(targetYaw - player.getYaw());
        float pitchDelta = targetPitch - player.getPitch();

        player.setYaw(player.getYaw() + yawDelta * 0.75f);
        player.setPitch(player.getPitch() + pitchDelta * 0.75f);
    }

    // W tuşuna basıldığında hedefi takip etme ve arkasında konumlanma mantığı
    private static void trackTargetMovement(PlayerEntity player, LivingEntity target) {
        Vec3d targetPos = target.getPos();
        Vec3d playerPos = player.getPos();
        
        // Hedefin hareket yönüne göre rotayı kitle
        double diffX = targetPos.x - playerPos.x;
        double diffZ = targetPos.z - playerPos.z;
        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0F;
        
        player.setYaw(targetYaw);
    }

    // Kalkan kıran mekanizma
    private static void handleShieldBreaker(MinecraftClient client, LivingEntity target) {
        if (target.isUsingItem() && target.getActiveItem().isOf(Items.SHIELD)) {
            for (int i = 0; i < 9; i++) {
                if (client.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                    client.player.getInventory().selectedSlot = i;
                    break;
                }
            }
        }
    }

    private static float wrapDegrees(float value) {
        value = value % 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    // --- ELYTRA HEDEF TAKİBİ VE OTOMATİK FİŞEK ---
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

        if (target != null && (client.player.isFallFlying() || client.player.fallDistance > 2.0f)) {
            aimAtTargetBypass(client.player, (LivingEntity) target);
        }

        if (client.player.isFallFlying() && client.player.age % 20 == 0) {
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
}

