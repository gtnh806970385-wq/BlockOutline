package com.blockoutline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class BOSlider extends GuiButton {

    private float sliderValue;
    public boolean dragging;

    public BOSlider(int i, int x, int y, float f) {
        super(i, x, y, 150, 20, "");
        this.sliderValue = f;
        this.displayString = getDisplayString(i);
    }

    @Override
    public int getHoverState(boolean flag) {
        return 0;
    }

    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float) (par2 - (this.xPosition + 4)) / (float) (this.width - 8);
                if (this.sliderValue < 0.0F) {
                    this.sliderValue = 0.0F;
                }
                if (this.sliderValue > 1.0F) {
                    this.sliderValue = 1.0F;
                }
                updateValue(this.id);
                this.displayString = getDisplayString(this.id);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(
                this.xPosition + (int) (this.sliderValue * (this.width - 8)),
                this.yPosition,
                0,
                66,
                4,
                20);
            drawTexturedModalRect(
                this.xPosition + (int) (this.sliderValue * (this.width - 8)) + 4,
                this.yPosition,
                196,
                66,
                4,
                20);
        }
    }

    public boolean mousePressed(Minecraft mc, int par2, int par3) {
        if (super.mousePressed(mc, par2, par3)) {
            this.sliderValue = (float) (par2 - (this.xPosition + 4)) / (float) (this.width - 8);

            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }
            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }
            this.displayString = getDisplayString(this.id);
            this.dragging = true;
            return true;
        }
        return false;
    }

    private void updateValue(int id) {
        switch (id) {
            case 1:
                BlockOutline.setRed(this.sliderValue);
                break;
            case 2:
                BlockOutline.setGreen(this.sliderValue);
                break;
            case 3:
                BlockOutline.setBlue(this.sliderValue);
                break;
            case 4:
                BlockOutline.setAlpha(this.sliderValue);
                break;
            case 5:
                BlockOutline.setThickness(this.sliderValue * 7.0F);
                break;
            case 7:
                BlockOutline.setBlinkAlpha(this.sliderValue);
                break;
            case 8:
                BlockOutline.setBlinkSpeed(this.sliderValue);
        }
    }

    private String getDisplayString(int id) {
        switch (id) {
            case 1:
                return "\u00a7c\u7ea2\u8272: " + Math.round(this.sliderValue * 255.0F);
            case 2:
                return "\u00a7a\u7eff\u8272: " + Math.round(this.sliderValue * 255.0F);
            case 3:
                return "\u00a79\u84dd\u8272: " + Math.round(this.sliderValue * 255.0F);
            case 4:
                return "\u00a7f\u900f\u660e\u5ea6: " + Math.round(this.sliderValue * 255.0F);
            case 5:
                return "\u00a7f\u7ebf\u5bbd: " + Math.round(this.sliderValue * 7.0F);
            case 7:
                return "\u00a7f\u95ea\u70c1\u900f\u660e\u5ea6: " + Math.round(this.sliderValue * 255.0F);
            case 8:
                return "\u00a7f\u95ea\u70c1\u901f\u5ea6: " + Math.round(this.sliderValue * 100.0F);
        }
        return "\u9009\u9879\u9519\u8bef?! (" + id + ")";
    }

    public void mouseReleased(int p_146118_1_, int p_146118_2_) {
        this.dragging = false;
    }
}
