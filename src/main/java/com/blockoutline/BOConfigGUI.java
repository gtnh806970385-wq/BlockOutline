package com.blockoutline;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class BOConfigGUI extends GuiScreen {

    GuiScreen parent;

    public BOConfigGUI(GuiScreen p) {
        this.parent = p;
    }

    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();

        // 左侧 - 线框颜色
        this.buttonList.add(new BOSlider(1, 4, this.height / 2 + -62, BlockOutline.getRed()));
        this.buttonList.add(new BOSlider(2, 4, this.height / 2 + -38, BlockOutline.getGreen()));
        this.buttonList.add(new BOSlider(3, 4, this.height / 2 + -14, BlockOutline.getBlue()));
        this.buttonList.add(new BOSlider(4, 4, this.height / 2 + 10, BlockOutline.getAlpha()));
        this.buttonList.add(new BOSlider(5, 4, this.height / 2 + 34, BlockOutline.getThickness() / 7.0F));

        // 右侧 - 外观设置
        this.buttonList.add(
            new BOButton(
                9,
                this.width - 154,
                this.height / 2 - 52,
                150,
                20,
                "\u7981\u7528\u6df1\u5ea6\u6d4b\u8bd5: "
                    + (BlockOutline.disableDepthBuffer ? "\u00a7a\u5f00" : "\u00a7c\u5173")));
        this.buttonList.add(
            new BOButton(
                10,
                this.width - 154,
                this.height / 2 - 30,
                150,
                20,
                "\u7834\u574f\u52a8\u753b: " + getBreakAnimationName()));
        this.buttonList.add(
            new BOButton(
                11,
                this.width - 154,
                this.height / 2 - 8,
                150,
                20,
                "GT \u673a\u5668\u6a21\u5f0f: "
                    + (BlockOutline.gtMachineMode ? "\u00a7a\u5f00" : "\u00a7c\u5173")));
        this.buttonList.add(new BOSlider(7, this.width - 154, this.height / 2 + 14, BlockOutline.getBlinkAlpha()));
        this.buttonList.add(new BOSlider(8, this.width - 154, this.height / 2 + 38, BlockOutline.getBlinkSpeed()));

        // 底部
        this.buttonList.add(new BOButton(20, this.width / 2 - 100, this.height - 48, "\u00a7e\u5b8c\u6210"));
        this.buttonList
            .add(new BOButton(21, this.width / 2 - 100, this.height - 24, 95, 20, "\u00a7bBO \u9ed8\u8ba4"));
        this.buttonList.add(new BOButton(22, this.width / 2 + 5, this.height - 24, 95, 20, "\u00a77MC \u9ed8\u8ba4"));
    }

    private String getBreakAnimationName() {
        if (BlockOutline.breakAnimation == 0) return "\u00a77\u65e0";
        if (BlockOutline.breakAnimation == 1) return "\u00a7e\u6536\u7f29";
        if (BlockOutline.breakAnimation == 2) return "\u00a7b\u4e0b\u843d";
        if (BlockOutline.breakAnimation == 3) return "\u00a7d\u6de1\u51fa";
        return "";
    }

    public void actionPerformed(GuiButton button) {
        if (button.id == 20) {
            BlockOutline.save();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (button.id == 21) {
            BlockOutline.reset(false);
            initGui();
        } else if (button.id == 22) {
            BlockOutline.reset(true);
            initGui();
        } else if (button.id == 9) {
            BlockOutline.disableDepthBuffer = !BlockOutline.disableDepthBuffer;
            button.displayString = "\u7981\u7528\u6df1\u5ea6\u6d4b\u8bd5: "
                + (BlockOutline.disableDepthBuffer ? "\u00a7a\u5f00" : "\u00a7c\u5173");
        } else if (button.id == 10) {
            BlockOutline.setBreakAnimation(
                BlockOutline.breakAnimation == BlockOutline.LASTANIMATION_INDEX ? 0
                    : BlockOutline.breakAnimation + 1);
            button.displayString = "\u7834\u574f\u52a8\u753b: " + getBreakAnimationName();
        } else if (button.id == 11) {
            BlockOutline.gtMachineMode = !BlockOutline.gtMachineMode;
            button.displayString = "GT \u673a\u5668\u6a21\u5f0f: "
                + (BlockOutline.gtMachineMode ? "\u00a7a\u5f00" : "\u00a7c\u5173");
        }
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == 1) {
            BlockOutline.save();
            this.mc.displayGuiScreen((GuiScreen) null);
        }
    }

    public void drawScreen(int par1, int par2, float par3) {
        drawGradientRect(0, 0, this.width, 48 - 4, -1072689136, -804253680); // 顶部
        drawGradientRect(0, this.height / 2 - 67, 158, this.height / 2 + 59, -1072689136, -804253680); // 左侧面板
        drawGradientRect(
            this.width - 158,
            this.height / 2 - 56,
            this.width,
            this.height / 2 + 63,
            -1072689136,
            -804253680); // 右侧面板
        drawGradientRect(0, this.height - 48 - 4, this.width, this.height, -1072689136, -804253680); // 底部

        drawCenteredString(
            this.fontRendererObj,
            "\u00a76BlockOutline \u65b9\u5757\u8f6e\u5ed3\u8bbe\u7f6e",
            this.width / 2,
            (this.height - (this.height + 4 - 48)) / 2 - 4,
            16777215);

        super.drawScreen(par1, par2, par3);
    }
}
