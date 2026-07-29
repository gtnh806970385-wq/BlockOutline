package com.blockoutline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class BOButton extends GuiButton {

    public BOButton(int i, int x, int y, String s) {
        this(i, x, y, 200, 20, s);
    }

    public BOButton(int i, int x, int y, int w, int h, String s) {
        super(i, x, y, w, h, s);
    }

    public boolean mousePressed(Minecraft mc, int i, int j) {
        boolean flag = super.mousePressed(mc, i, j);
        if (flag && this.id == 404) {
            BlockOutline.openSettingsGUI();
            return false;
        }
        return flag;
    }
}
