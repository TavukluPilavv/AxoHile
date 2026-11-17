package dev.axohile;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public final class VexionBackgroundRenderer {
    private static final Identifier BACKGROUND =
            Identifier.of("axohile", "textures/gui/vexion_menu_background.jpg");

    private VexionBackgroundRenderer() {}

    public static void render(DrawContext context, int width, int height) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND,
                0, 0, 0.0f, 0.0f, width, height, 1536, 846);
        context.fillGradient(0, 0, width, height, 0x10040A16, 0x30020710);
    }
}
