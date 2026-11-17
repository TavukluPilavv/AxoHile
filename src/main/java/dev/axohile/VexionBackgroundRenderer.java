package dev.axohile;

import net.minecraft.client.gui.DrawContext;

/**
 * Vexion ana menüsü için kodla çizilen, piksel-art tarzı arka plan.
 * Harici dosya gerektirmediğinden kaynak görseli eksik kalmaz.
 */
public final class VexionBackgroundRenderer {
    private VexionBackgroundRenderer() {}

    public static void render(DrawContext context, int width, int height) {
        context.fillGradient(0, 0, width, height, 0xFF080D20, 0xFF17133A);
        context.fillGradient(0, height / 3, width, height, 0x00121C3D, 0xD9081028);

        // Uzak dağ silüetleri
        int horizon = height * 2 / 3;
        context.fill(0, horizon - 35, width, height, 0xFF101A35);
        for (int i = 0; i < width; i += 92) {
            int peak = 35 + ((i * 37) % 75);
            context.fill(i, horizon - peak, i + 48, horizon + 12, 0xFF18264A);
            context.fill(i + 14, horizon - peak - 18, i + 35, horizon - peak + 8, 0xFF22345D);
            context.fill(i + 5, horizon - peak + 8, i + 43, horizon + 12, 0xFF18264A);
        }

        // Uzak ışıklar / yıldızlar
        for (int i = 0; i < 46; i++) {
            int x = (i * 137 + 29) % Math.max(1, width);
            int y = (i * 71 + 17) % Math.max(1, horizon - 25);
            int color = (i % 3 == 0) ? 0xAA8BD8FF : 0x667C91D8;
            context.fill(x, y, x + 2, y + 2, color);
        }

        // Sağ kenarda neon blok sütunları
        for (int i = 0; i < 7; i++) {
            int x = width - 34 - i * 19;
            int top = height / 4 + (i % 3) * 28;
            context.fill(x, top, x + 10, height - 22, 0x263D7DCE);
            context.fill(x, top, x + 2, height - 22, 0x6678BFFF);
        }
        context.fill(0, 0, width, height, 0x18000000);
    }
}
