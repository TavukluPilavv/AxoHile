package dev.axohile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Ana menü açıldığında karşılama sesini bir kez oynatır.
 */
public final class VexionWelcomeSound {
    private static final Identifier ID = Identifier.of("axohile", "welcome");
    private static SoundEvent sound;
    private static boolean played;

    private VexionWelcomeSound() {}

    public static void register() {
        if (sound == null) {
            sound = Registry.register(Registries.SOUND_EVENT, ID, SoundEvent.of(ID));
        }
    }

    public static void playOnce() {
        if (played || sound == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getSoundManager() == null) {
            return;
        }

        played = true;
        client.getSoundManager().play(PositionedSoundInstance.master(sound, 1.0F));
    }
}
