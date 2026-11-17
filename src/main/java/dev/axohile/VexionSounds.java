package dev.axohile;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class VexionSounds {
    public static final Identifier MAIN_MENU_MAIN_MENU_WELCOME_ID = Identifier.of("axohile", "main_menu_welcome");
    public static final SoundEvent MAIN_MENU_WELCOME = SoundEvent.of(MAIN_MENU_MAIN_MENU_WELCOME_ID);

    private VexionSounds() {}

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, MAIN_MENU_MAIN_MENU_WELCOME_ID, MAIN_MENU_WELCOME);
    }
}
