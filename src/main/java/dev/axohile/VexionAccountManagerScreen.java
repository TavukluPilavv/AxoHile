package dev.axohile;

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
        int panelWidth = Math.min(420, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, this.height / 5);

        accountNameField = new TextFieldWidget(this.textRenderer, left + 18, top + 43,
                panelWidth - 145, 22, Text.literal("Hesap adı"));
        accountNameField.setMaxLength(16);
        accountNameField.setPlaceholder(Text.literal("Minecraft kullanıcı adı"));
        addDrawableChild(accountNameField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Sil"), button -> {
            VexionAccountStore.deleteSelected();
            status = "Seçili hesap silindi.";
        }).dimensions(left + panelWidth - 116, top + 42, 98, 24).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Hesap Ekle"), button -> {
            if (VexionAccountStore.add(accountNameField.getText())) {
                accountNameField.setText("");
                status = "Hesap listeye eklendi.";
            } else {
                status = "Geçersiz veya zaten kayıtlı isim.";
            }
        }).dimensions(left + 18, top + 76, panelWidth - 36, 25).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Geri"), button -> {
            if (client != null) client.setScreen(parent);
        }).dimensions(left + 18, this.height - 42, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        VexionBackgroundRenderer.render(context, this.width, this.height);
        int panelWidth = Math.min(420, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, this.height / 5);

        context.fill(left, top, left + panelWidth, this.height - 30, 0xE6101830);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF438CFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "VC  ACCOUNT MANAGER",
                this.width / 2, top + 14, 0xFFE8F3FF);
        context.drawTextWithShadow(this.textRenderer, "Hesap adı", left + 18, top + 31, 0xFFB8C9E8);

        super.render(context, mouseX, mouseY, delta);

        // İstenen yeşil ekle ve kırmızı sil buton renkleri
        context.fill(left + panelWidth - 116, top + 42, left + panelWidth - 18, top + 66, 0xFFB83248);
        context.drawCenteredTextWithShadow(this.textRenderer, "Sil", left + panelWidth - 67, top + 50, 0xFFFFFFFF);
        context.fill(left + 18, top + 76, left + panelWidth - 18, top + 101, 0xFF16864A);
        context.drawCenteredTextWithShadow(this.textRenderer, "Hesap Ekle", this.width / 2, top + 84, 0xFFFFFFFF);

        context.drawTextWithShadow(this.textRenderer, "Kayıtlı hesaplar", left + 18, top + 111, 0xFFB8C9E8);
        int rowY = top + 128;
        for (String account : VexionAccountStore.getAccounts()) {
            boolean selected = account.equals(VexionAccountStore.getSelected());
            int rowColor = selected ? 0xFF245A9B : 0x88313D58;
            context.fill(left + 18, rowY, left + panelWidth - 18, rowY + 21, rowColor);
            context.drawTextWithShadow(this.textRenderer, (selected ? "▶  " : "   ") + account,
                    left + 27, rowY + 6, selected ? 0xFFBDEBFF : 0xFFFFFFFF);
            rowY += 25;
            if (rowY > this.height - 65) break;
        }

        if (!status.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, status, this.width / 2,
                    this.height - 59, 0xFFFFD479);
        }
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Bu liste çevrimdışı isimleri saklar; Microsoft oturumu açmaz.",
                this.width / 2, this.height - 19, 0xFF9BAAC6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelWidth = Math.min(420, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, this.height / 5);
        int rowY = top + 128;

        for (String account : VexionAccountStore.getAccounts()) {
            if (mouseX >= left + 18 && mouseX <= left + panelWidth - 18
                    && mouseY >= rowY && mouseY <= rowY + 21) {
                VexionAccountStore.select(account);
                return true;
            }
            rowY += 25;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
