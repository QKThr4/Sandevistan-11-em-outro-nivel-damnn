package com.sandevistanmod.client;

import net.minecraft.client.gui.GuiGraphics;

/** Silhueta simples do jogador vista de costas, desenhada com retangulos (16 x 32 unidades). */
public final class Silhouette {
    private Silhouette() {}

    private static final int BODY = 0xFF55646E;
    private static final int EDGE = 0xFF2A333A;

    private static void part(GuiGraphics gg, int x, int y, int w, int h, int u) {
        gg.fill(x, y, x + w * u, y + h * u, EDGE);
        gg.fill(x + 1, y + 1, x + w * u - 1, y + h * u - 1, BODY);
    }

    /** Desenha com o topo esquerdo em (x, y); cada unidade tem u pixels. */
    public static void draw(GuiGraphics gg, int x, int y, int u) {
        part(gg, x + 4 * u, y, 8, 8, u);          // cabeca
        part(gg, x + 4 * u, y + 8 * u, 8, 12, u); // tronco
        part(gg, x, y + 8 * u, 4, 12, u);         // braco
        part(gg, x + 12 * u, y + 8 * u, 4, 12, u);
        part(gg, x + 4 * u, y + 20 * u, 4, 12, u); // pernas
        part(gg, x + 8 * u, y + 20 * u, 4, 12, u);
    }

    /** Centro das costas (onde o implante fica): retorna {x, y}. */
    public static int[] back(int x, int y, int u) {
        return new int[] {x + 8 * u, y + 12 * u};
    }

    /** O implante nas costas: uma placa fina e verde ao longo da coluna. */
    public static void implant(GuiGraphics gg, int x, int y, int u) {
        int[] b = back(x, y, u);
        gg.fill(b[0] - u, b[1] - 3 * u, b[0] + u, b[1] + 3 * u, 0xFF1B6B0A);
        gg.fill(b[0] - u + 1, b[1] - 3 * u + 1, b[0] + u - 1, b[1] + 3 * u - 1, 0xFF39FF14);
    }

    /** Seta horizontal de (x1, y) ate (x2, y) com a ponta em x2 (x2 maior que x1). */
    public static void arrow(GuiGraphics gg, int x1, int x2, int y, int color) {
        gg.fill(x1, y - 1, x2 - 4, y + 1, color);
        for (int i = 0; i < 5; i++) {
            gg.fill(x2 - 5 + i, y - 1 - (4 - i), x2 - 4 + i, y + 1 + (4 - i), color);
        }
    }
}
