package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;

/**
 * Vexion ana menüsünün arka plan görselini çizer.
 */
public final class VexionBackgroundRenderer {
    private static final Identifier BACKGROUND =
            Identifier.of("axohile", "textures/gui/vexion_background.jpg");

    private VexionBackgroundRenderer() {}

    public static void render(DrawContext context, int width, int height) {
        context.drawTexture(
                RenderLayer::getGuiTextured,
                BACKGROUND,
                0, 0,
                0.0F, 0.0F,
                width, height,
                width, height
        );
    }
}
