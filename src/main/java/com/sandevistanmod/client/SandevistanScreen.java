package com.sandevistanmod.client;

import com.sandevistanmod.data.SandevistanData;
import com.sandevistanmod.data.Tx;
import com.sandevistanmod.registry.ModRegistries;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** Aba especial do Sandevistan: implante, Mente, o que ele faz e as acoes. */
public class SandevistanScreen extends Screen {
    private Button corner;

    public SandevistanScreen() {
        super(Tx.t("screen.title"));
    }

    /** Posicao do centro do painel principal (o painel dos implantes fica a esquerda). */
    private int mainCx() {
        int groupW = 126 + 280;
        int x0 = Math.max(2, (this.width - groupW) / 2);
        return x0 + 126 + 140;
    }

    private int leftCx() {
        return mainCx() - 140 - 6 - 60;
    }

    public static List<Component> implantTooltip() {
        return List.of(Tx.t("implant.name").withStyle(net.minecraft.ChatFormatting.GREEN),
                Tx.t("implant.tip.1").withStyle(net.minecraft.ChatFormatting.GRAY),
                Tx.t("implant.tip.2").withStyle(net.minecraft.ChatFormatting.GRAY),
                Tx.t("implant.tip.3").withStyle(net.minecraft.ChatFormatting.GRAY),
                Tx.t("implant.tip.4").withStyle(net.minecraft.ChatFormatting.RED));
    }

    @Override
    protected void init() {
        int cx = mainCx();
        int y = this.height / 2 + 36;
        corner = addRenderableWidget(Button.builder(cornerLabel(), b -> {
                    ClientLayout.nextCorner();
                    b.setMessage(cornerLabel());
                })
                .bounds(cx - 110, y, 220, 20).build());
        addRenderableWidget(Button.builder(musicLabel(), b -> {
                    ClientLayout.nextMusic();
                    b.setMessage(musicLabel());
                })
                .bounds(cx - 110, y + 22, 220, 20).build());
        addRenderableWidget(Button.builder(Tx.t("btn.close"), b -> onClose())
                .bounds(cx - 110, y + 44, 220, 20).build());
    }

    private static Component musicLabel() {
        return Tx.t("btn.music", Tx.t("music." + ClientLayout.music));
    }

    private static Component cornerLabel() {
        return Tx.t("btn.corner", Tx.t("corner." + ClientLayout.corner));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeys.SCREEN.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static String time(int seconds) {
        return (seconds / 60) + ":" + String.format("%02d", seconds % 60);
    }

    @Override
    public void renderBackground(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(gg, mouseX, mouseY, partialTick);
        SandevistanData d = ClientData.DATA;
        int cx = mainCx();
        int cy = this.height / 2;
        drawImplantPanel(gg, cy);

        gg.fill(cx - 140, cy - 120, cx + 140, cy + 130, 0xC0101410);
        gg.fill(cx - 140, cy - 120, cx + 140, cy - 118, 0xFF39FF14);
        gg.drawCenteredString(this.font, Tx.t("screen.title"), cx, cy - 113, 0x39FF14);

        int y = cy - 98;
        MutableComponent implant = d.implanted ? Tx.t("screen.implanted") : Tx.t("screen.noimplant");
        gg.drawCenteredString(this.font, implant, cx, y, d.implanted ? 0x55FF55 : 0xAAAAAA);
        y += 12;

        if (d.implanted) {
            gg.drawCenteredString(this.font, d.active ? Tx.t("screen.on") : Tx.t("screen.off"), cx, y,
                    d.active ? 0x39FF14 : 0xAAAAAA);
            y += 14;
            ClientOverlay.bar(gg, cx - 100, y, 200, 9, d.mind / SandevistanData.MAX_MIND,
                    ClientOverlay.mindColor(d.mind));
            ClientOverlay.lostMark(gg, cx - 100, y, 200, 9, SandevistanData.MAX_MIND - d.maxMind());
            y += 12;
            gg.drawCenteredString(this.font,
                    Tx.t("screen.mind", (int) d.mind, (int) d.maxMind(), time(d.secondsLeft())), cx, y,
                    d.mind < 20f ? 0xFF5555 : 0x88FFAA);
            y += 14;
            MutableComponent key = Component.empty().append(ModKeys.TOGGLE.getTranslatedKeyMessage());
            gg.drawCenteredString(this.font, Tx.t("screen.key", key), cx, y, 0xDDDDDD);
            y += 14;
        } else {
            gg.drawCenteredString(this.font, Tx.t("screen.howto"), cx, y + 4, 0xBBBBBB);
            y += 30;
        }

        gg.drawCenteredString(this.font, Tx.t("screen.fx.1"), cx, y, 0x88DDFF);
        y += 11;
        gg.drawCenteredString(this.font, Tx.t("screen.fx.2"), cx, y, 0xBBBBBB);
        y += 11;
        gg.drawCenteredString(this.font, Tx.t("screen.fx.3"), cx, y, 0xBBBBBB);
        y += 11;
        gg.drawCenteredString(this.font, Tx.t("screen.fx.4"), cx, y, 0xBBBBBB);
    }

    private int slotX() {
        return leftCx() - 52;
    }

    private int slotY() {
        return this.height / 2 - 30;
    }

    /** Painel a esquerda: silhueta com o implante nas costas (so visual; implantar/remover e no Ripperdoc). */
    private void drawImplantPanel(GuiGraphics gg, int cy) {
        SandevistanData d = ClientData.DATA;
        int lx = leftCx();
        gg.fill(lx - 60, cy - 120, lx + 60, cy + 130, 0xC0101410);
        gg.fill(lx - 60, cy - 120, lx + 60, cy - 118, 0xFF39FF14);
        gg.drawCenteredString(this.font, Tx.t("implants.title"), lx, cy - 113, 0x39FF14);
        int u = 4;
        int sx = lx - 8 * u + 14;
        int sy = cy - 90;
        Silhouette.draw(gg, sx, sy, u);
        int slx = slotX();
        int sly = slotY();
        gg.fill(slx - 1, sly - 1, slx + 19, sly + 19, d.implanted ? 0xFF39FF14 : 0xFF666666);
        gg.fill(slx, sly, slx + 18, sly + 18, 0xFF1A1E22);
        int[] b = Silhouette.back(sx, sy, u);
        if (d.implanted) {
            gg.renderItem(new ItemStack(ModRegistries.SANDEVISTAN_ITEM.get()), slx + 1, sly + 1);
            Silhouette.implant(gg, sx, sy, u);
            Silhouette.arrow(gg, slx + 21, sx + 4 * u, b[1], 0xFF39FF14);
        } else {
            Silhouette.arrow(gg, slx + 21, sx + 4 * u, b[1], 0xFF444444);
        }
        gg.drawCenteredString(this.font, d.implanted ? Tx.t("screen.implanted") : Tx.t("screen.noimplant"), lx, cy + 56,
                d.implanted ? 0x55FF55 : 0xAAAAAA);
        gg.drawCenteredString(this.font, Tx.t("implants.visual"), lx, cy + 70, 0x888888);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
        if (ClientData.DATA.implanted && mouseX >= slotX() && mouseX < slotX() + 18
                && mouseY >= slotY() && mouseY < slotY() + 18) {
            gg.renderComponentTooltip(this.font, implantTooltip(), mouseX, mouseY);
        }
    }
}
