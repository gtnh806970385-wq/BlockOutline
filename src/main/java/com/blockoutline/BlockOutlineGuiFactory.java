package com.blockoutline;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.client.IModGuiFactory;

public class BlockOutlineGuiFactory implements IModGuiFactory {

    public void initialize(Minecraft mi) {}

    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return BOConfigGUI.class;
    }

    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement e) {
        return null;
    }
}
