package dev.axohile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.TitleScreen;

public final class VexionMenuEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VexionWelcomeSound.register();
        VexionAccountStore.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen instanceof TitleScreen) {
                client.setScreen(new VexionMainMenuScreen());
            }
        });
    }
}
