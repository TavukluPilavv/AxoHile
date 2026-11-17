package dev.axohile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class VexionAccountManagerScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget accountNameField;
    private String status = "";

    public VexionAccountManagerScreen(Screen parent) {
        super(Text.literal("Vexion Account Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(480, this.width - 28);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(24, this.height / 7);
        accountNameField = new TextFieldWidget(textRenderer, left + 18, top + 48,
                panelWidth - 150, 25, Text.literal("Hesap adı"));
        accountNameField.setMaxLength(16);
        accountNameField.setPlaceholder(Text.literal("Minecraft kullanıcı adı"));
        addDrawableChild(accountNameField);

        int actionY = height - 57, gap = 8;
        int buttonWidth = Math.min(138, (panelWidth - 52) / 3);
        int totalWidth = buttonWidth * 3 + gap * 2;
        int actionX = (width - totalWidth) / 2;
        addDrawableChild(actionButton("Hesap Ekle", actionX, actionY, buttonWidth, 27, 0xFF16864A, b -> {
            if (VexionAccountStore.add(accountNameField.getText())) {
                accountNameField.setText("");
                status = "Hesap listeye eklendi.";
            } else status = "Geçersiz veya zaten kayıtlı isim.";
        }));
        addDrawableChild(actionButton("Sil", actionX + buttonWidth + gap, actionY, buttonWidth, 27, 0xFFB83248, b -> {
            VexionAccountStore.deleteSelected();
            status = "Seçili hesap silindi.";
        }));
        addDrawableChild(actionButton("Geri", actionX + (buttonWidth + gap) * 2, actionY, buttonWidth, 27, 0xFF344F78,
                b -> { if (client != null) client.setScreen(parent); }));
    }

    private ButtonWidget actionButton(String label, int x, int y, int width, int height, int color,
                                      ButtonWidget.PressAction action) {
        return new ButtonWidget(x, y, width, height, Text.literal(label), action,
                ButtonWidget.DEFAULT_NARRATION_SUPPLIER) {
            @Override
            protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                int fill = isHovered() ? brighten(color) : color;
                context.fill(getX() + 2, getY() + 3, getX() + width + 2, getY() + height + 3, 0x66000000);
                context.fill(getX(), getY(), getX() + width, getY() + height, fill);
                context.fill(getX(), getY(), getX() + width, getY() + 2, 0xFFFFFFFF);
                context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, getMessage(),
                        getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFFFF);
            }
        };
    }

    private int brighten(int color) {
        int r = Math.min(255, ((color >> 16) & 255) + 28);
        int g = Math.min(255, ((color >> 8) & 255) + 28);
        int b = Math.min(255, (color & 255) + 28);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        VexionBackgroundRenderer.render(context, width, height);
        int panelWidth = Math.min(480, width - 28);
        int left = (width - panelWidth) / 2;
        int top = Math.max(24, height / 7);
        context.fill(left + 4, top + 5, left + panelWidth + 4, height - 42, 0x77000000);
        context.fill(left, top, left + panelWidth, height - 45, 0xE6101830);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF438CFF);
        context.drawCenteredTextWithShadow(textRenderer, "ACCOUNT MANAGER", width / 2, top + 15, 0xFFE8F3FF);
        context.drawTextWithShadow(textRenderer, "Hesap adı", left + 18, top + 34, 0xFFB8C9E8);
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(textRenderer, "Kayıtlı hesaplar", left + 18, top + 83, 0xFFB8C9E8);
        String loggedInName = MinecraftClient.getInstance().getSession().getUsername();
        int rowY = top + 101;
        for (String account : VexionAccountStore.getAccounts()) {
            boolean activeSession = account.equalsIgnoreCase(loggedInName);
            boolean selected = account.equals(VexionAccountStore.getSelected());
            int rowLeft = left + 18, rowRight = left + panelWidth - 18;
            context.fill(rowLeft - 2, rowY - 2, rowRight + 2, rowY + 22,
                    activeSession ? 0xFF269BFF : 0xFFE9EDF5);
            context.fill(rowLeft, rowY, rowRight, rowY + 20, selected ? 0xFF203D60 : 0xE51A2230);
            context.drawTextWithShadow(textRenderer, account, rowLeft + 9, rowY + 6, 0xFF58B8FF);
            context.drawTextWithShadow(textRenderer, activeSession ? "OYUNDA" : (selected ? "SEÇİLİ" : ""),
                    rowRight - 52, rowY + 6, activeSession ? 0xFF8AD5FF : 0xFFE9EDF5);
            rowY += 26;
            if (rowY > height - 95) break;
        }
        if (VexionAccountStore.getAccounts().isEmpty())
            context.drawCenteredTextWithShadow(textRenderer, "Henüz hesap eklenmedi", width / 2, top + 125, 0xFFCCD3DF);
        if (!status.isEmpty())
            context.drawCenteredTextWithShadow(textRenderer, status, width / 2, height - 77, 0xFFFFD479);
        context.drawCenteredTextWithShadow(textRenderer,
                "Mavi çerçeve: şu an oyunda açık hesap  |  Beyaz çerçeve: diğer hesaplar",
                width / 2, height - 66, 0xFFD5DCE7);
        context.drawCenteredTextWithShadow(textRenderer,
                "Çevrimdışı isim listesi Microsoft oturumunu değiştirmez.",
                width / 2, height - 32, 0xFF9BAAC6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelWidth = Math.min(480, width - 28);
        int left = (width - panelWidth) / 2;
        int rowY = Math.max(24, height / 7) + 101;
        for (String account : VexionAccountStore.getAccounts()) {
            if (mouseX >= left + 16 && mouseX <= left + panelWidth - 16
                    && mouseY >= rowY - 2 && mouseY <= rowY + 22) {
                VexionAccountStore.select(account);
                status = account.equalsIgnoreCase(MinecraftClient.getInstance().getSession().getUsername())
                        ? account + " şu anki oyun hesabı." : account + " seçildi. Oturum değişmedi.";
                return true;
            }
            rowY += 26;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public void close() { if (client != null) client.setScreen(parent); }
    @Override public boolean shouldPause() { return false; }
}
