package com.sandevistanmod.client;

import com.sandevistanmod.data.SandevistanData;
import com.sandevistanmod.data.Tx;
import com.sandevistanmod.logic.SandevistanLogic;
import com.sandevistanmod.net.DoctorActionPayload;
import com.sandevistanmod.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

/** Tela do Ripperdoc: menu (Loja / Implantes), encaixe do Sandevistan na silhueta e loja de remedios. */
public class DoctorScreen extends Screen {
    private static final int MENU = 0;
    private static final int IMPLANTS = 1;
    private static final int SHOP = 2;

    private int mode = MENU;
    /** O Sandevistan foi encaixado no slot (ainda nao implantado). */
    private boolean placed;
    private Button implantBtn;
    private Button removeBtn;
    private Button buy1;
    private Button buy2;

    public DoctorScreen() {
        super(Tx.t("doc.name"));
    }

    private static int count(net.minecraft.world.item.Item item) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        int n = 0;
        for (ItemStack s : mc.player.getInventory().items) if (s.is(item)) n += s.getCount();
        for (ItemStack s : mc.player.getInventory().offhand) if (s.is(item)) n += s.getCount();
        return n;
    }

    private static boolean creative() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getAbilities().instabuild;
    }

    private static boolean hasImplantItem() {
        return creative() || count(ModRegistries.SANDEVISTAN_ITEM.get()) > 0;
    }

    private int px() {
        return this.width / 2 - 130;
    }

    private int py() {
        return this.height / 2 - 100;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int top = py();
        implantBtn = null;
        removeBtn = null;
        buy1 = null;
        buy2 = null;
        if (mode == MENU) {
            addRenderableWidget(Button.builder(Tx.t("doc.menu.shop"), b -> setMode(SHOP))
                    .bounds(cx - 80, top + 70, 160, 22).build());
            addRenderableWidget(Button.builder(Tx.t("doc.menu.implants"), b -> setMode(IMPLANTS))
                    .bounds(cx - 80, top + 98, 160, 22).build());
            addRenderableWidget(Button.builder(Tx.t("btn.close"), b -> onClose())
                    .bounds(cx - 80, top + 140, 160, 20).build());
        } else if (mode == IMPLANTS) {
            implantBtn = addRenderableWidget(Button.builder(Tx.t("doc.btn.implant"), b -> {
                        PacketDistributor.sendToServer(new DoctorActionPayload(1));
                        placed = false;
                    })
                    .bounds(cx - 60, top + 166, 90, 20).build());
            removeBtn = addRenderableWidget(Button.builder(Tx.t("doc.btn.remove"), b ->
                            PacketDistributor.sendToServer(new DoctorActionPayload(2)))
                    .bounds(cx - 60, top + 166, 120, 20).build());
            addRenderableWidget(Button.builder(Tx.t("doc.btn.back"), b -> setMode(MENU))
                    .bounds(px() + 6, top + 6, 50, 16).build());
        } else {
            buy1 = addRenderableWidget(Button.builder(Tx.t("doc.btn.buy"), b ->
                            PacketDistributor.sendToServer(new DoctorActionPayload(3)))
                    .bounds(cx + 50, top + 62, 60, 20).build());
            buy2 = addRenderableWidget(Button.builder(Tx.t("doc.btn.buy"), b ->
                            PacketDistributor.sendToServer(new DoctorActionPayload(4)))
                    .bounds(cx + 50, top + 98, 60, 20).build());
            addRenderableWidget(Button.builder(Tx.t("doc.btn.back"), b -> setMode(MENU))
                    .bounds(px() + 6, top + 6, 50, 16).build());
        }
        refresh();
    }

    private void setMode(int m) {
        mode = m;
        rebuildWidgets();
    }

    private void refresh() {
        SandevistanData d = ClientData.DATA;
        if (implantBtn != null) {
            implantBtn.visible = !d.implanted && placed;
            implantBtn.active = creative() || count(Items.EMERALD) >= SandevistanLogic.CLINIC_EMERALDS;
        }
        if (removeBtn != null) {
            removeBtn.visible = d.implanted;
            removeBtn.active = !d.active;
        }
        if (buy1 != null) buy1.active = creative() || count(Items.EMERALD) >= SandevistanLogic.STABILIZER_PRICE;
        if (buy2 != null) buy2.active = creative() || count(Items.EMERALD) >= SandevistanLogic.IMMUNO_PRICE;
    }

    @Override
    public void tick() {
        if (placed && !hasImplantItem()) placed = false;
        refresh();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // slot do implante (a esquerda da silhueta)
    private int slotX() {
        return this.width / 2 - 100;
    }

    private int slotY() {
        return py() + 90;
    }

    private boolean inSlot(double mx, double my) {
        return mx >= slotX() && mx < slotX() + 18 && my >= slotY() && my < slotY() + 18;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (mode == IMPLANTS && button == 0 && inSlot(mx, my) && !ClientData.DATA.implanted && hasImplantItem()) {
            placed = !placed;
            refresh();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void renderBackground(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(gg, mouseX, mouseY, partialTick);
        int cx = this.width / 2;
        int x0 = px();
        int top = py();
        gg.fill(x0, top, x0 + 260, top + 200, 0xD0101214);
        gg.fill(x0, top, x0 + 260, top + 2, 0xFFFF2040);
        gg.drawCenteredString(this.font, Tx.t("doc.name"), cx, top + 8, 0xFF5566);

        if (mode == MENU) {
            gg.drawCenteredString(this.font, Tx.t("doc.menu.title"), cx, top + 40, 0xDDDDDD);
        } else if (mode == IMPLANTS) {
            drawImplants(gg, cx, top);
        } else {
            drawShop(gg, cx, top);
        }
    }

    private void drawImplants(GuiGraphics gg, int cx, int top) {
        SandevistanData d = ClientData.DATA;
        int u = 4;
        int sx = cx - 8 * u + 30;
        int sy = top + 34;
        Silhouette.draw(gg, sx, sy, u);
        int slx = slotX();
        int sly = slotY();
        gg.fill(slx - 1, sly - 1, slx + 19, sly + 19, d.implanted || placed ? 0xFF39FF14 : 0xFF777777);
        gg.fill(slx, sly, slx + 18, sly + 18, 0xFF1A1E22);
        if (d.implanted || placed) {
            gg.renderItem(new ItemStack(ModRegistries.SANDEVISTAN_ITEM.get()), slx + 1, sly + 1);
            int[] b = Silhouette.back(sx, sy, u);
            Silhouette.arrow(gg, slx + 22, sx + 4 * u, b[1], 0xFF39FF14);
            if (d.implanted) Silhouette.implant(gg, sx, sy, u);
        } else {
            int[] b = Silhouette.back(sx, sy, u);
            Silhouette.arrow(gg, slx + 22, sx + 4 * u, b[1], 0xFF555555);
        }
        gg.drawCenteredString(this.font, Tx.t("doc.slot"), slx + 9, sly + 22, 0xAAAAAA);
        if (d.implanted) {
            gg.drawCenteredString(this.font, Tx.t("screen.implanted"), cx, top + 150, 0x55FF55);
        } else if (!hasImplantItem()) {
            gg.drawCenteredString(this.font, Tx.t("clinic.noitem"), cx, top + 150, 0xFF5555);
        } else if (!placed) {
            gg.drawCenteredString(this.font, Tx.t("doc.place"), cx, top + 150, 0xDDDDDD);
        }
        if (!d.implanted && placed) {
            int bx = cx - 60 + 90 + 8;
            gg.renderItem(new ItemStack(Items.EMERALD), bx, top + 167);
            int have = count(Items.EMERALD);
            boolean ok = creative() || have >= SandevistanLogic.CLINIC_EMERALDS;
            gg.drawString(this.font, "x" + SandevistanLogic.CLINIC_EMERALDS, bx + 19, top + 172, ok ? 0x55FF55 : 0xFF5555);
        }
    }

    private void drawShop(GuiGraphics gg, int cx, int top) {
        int have = count(Items.EMERALD);
        gg.renderItem(new ItemStack(Items.EMERALD), cx - 120, top + 28);
        gg.drawString(this.font, Tx.t("doc.shop.have", have), cx - 98, top + 33, 0x55FF55);
        shopRow(gg, cx, top + 60, new ItemStack(ModRegistries.ESTABILIZADOR.get()), SandevistanLogic.STABILIZER_PRICE);
        shopRow(gg, cx, top + 96, new ItemStack(ModRegistries.IMUNOSSUPRESSOR.get()), SandevistanLogic.IMMUNO_PRICE);
    }

    private void shopRow(GuiGraphics gg, int cx, int y, ItemStack stack, int price) {
        gg.fill(cx - 124, y - 4, cx + 124, y + 28, 0x60000000);
        gg.renderItem(stack, cx - 118, y + 2);
        gg.drawString(this.font, stack.getHoverName(), cx - 96, y + 2, 0xFFFFFF);
        gg.renderItem(new ItemStack(Items.EMERALD), cx - 96, y + 12);
        gg.drawString(this.font, "x" + price, cx - 77, y + 17, count(Items.EMERALD) >= price || creative() ? 0x55FF55 : 0xFF5555);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
        if (mode == IMPLANTS && inSlot(mouseX, mouseY)) {
            gg.renderComponentTooltip(this.font, SandevistanScreen.implantTooltip(), mouseX, mouseY);
        }
    }
}
