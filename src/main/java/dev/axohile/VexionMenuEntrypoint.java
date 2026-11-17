package dev.axohile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.TitleScreen;

public final class VexionMenuEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VexionAccountStore.load();
        VexionSounds.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null && client.currentScreen instanceof TitleScreen) {
                client.setScreen(new VexionMainMenuScreen());
            }
        });
    }
}
