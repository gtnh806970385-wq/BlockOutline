package com.blockoutline;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class BOClientProxy extends BOCommonProxy {

    public void registerTickHandler() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.side != Side.CLIENT || !event.phase.equals(TickEvent.Phase.START)) return;
        if (event.type.equals(TickEvent.Type.RENDER)) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen instanceof GuiOptions) {
                GuiOptions gui = (GuiOptions) mc.currentScreen;
                List buttons = getList(gui);
                if (buttons == null) {
                    BlockOutline.logger.warn("no buttonsList?! " + gui);
                    doDebug(gui);
                    return;
                }
                for (Iterator i$ = buttons.iterator(); i$.hasNext();) {
                    Object o = i$.next();
                    if (o instanceof BOButton) {
                        return;
                    }
                }
                if (BlockOutline.diffButtonLoc)
                    buttons.add(new BOButton(404, gui.width - 150, 0, 150, 20, "\u00a76BlockOutline \u8bbe\u7f6e"));
                else buttons.add(
                    new BOButton(
                        404,
                        gui.width / 2 - 75,
                        gui.height / 6 + 24 - 6,
                        150,
                        20,
                        "\u00a76BlockOutline \u8bbe\u7f6e"));
            }
        }
    }

    public List getList(GuiOptions gui) {
        try {
            Field field;
            try {
                field = gui.getClass()
                    .getSuperclass()
                    .getDeclaredField("buttonList");
            } catch (NoSuchFieldException e) {
                field = gui.getClass()
                    .getSuperclass()
                    .getDeclaredField("field_146292_n");
            }
            field.setAccessible(true);
            return (List) field.get(gui);
        } catch (NoSuchFieldException e) {
            System.err.println("BO: NoSuchField");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doDebug(GuiScreen gui) {
        try {
            Class clazz = gui.getClass()
                .getSuperclass();
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                BlockOutline.logger.warn(
                    i + " name="
                        + fields[i].getName()
                        + "\ttype="
                        + fields[i].getType()
                        + "\tgenerictype="
                        + fields[i].getGenericType()
                        + "\taccessible="
                        + fields[i].isAccessible());
            }
            BlockOutline.logger.warn(Arrays.toString(fields));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
