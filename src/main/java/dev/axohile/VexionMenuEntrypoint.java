package dev.axohile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.PositionedSoundInstance;

public final class VexionMenuEntrypoint implements ClientModInitializer {
    private boolean welcomePlayed;

    @Override
    public void onInitializeClient() {
        VexionAccountStore.load();
        VexionSounds.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen instanceof TitleScreen) {
                client.setScreen(new VexionMainMenuScreen());
                if (!welcomePlayed) {
                    client.getSoundManager().play(PositionedSoundInstance.master(VexionSounds.WELCOME, 1.0f));
                    welcomePlayed = true;
                }
            }
        });
    }
}
