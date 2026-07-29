package com.blockoutline;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = BlockOutline.MODID,
    version = BlockOutline.VERSION,
    guiFactory = "com.blockoutline.BlockOutlineGuiFactory")
public class BlockOutline {

    public static final String MODID = "BlockOutline";
    public static final String VERSION = "1.0-elite";

    @SidedProxy(
        clientSide = "com.blockoutline.BOClientProxy",
        serverSide = "com.blockoutline.BOCommonProxy")
    public static BOCommonProxy proxy;
    private static Configuration config;
    public static Logger logger;
    public static float red;
    public static float green;
    public static float blue;
    public static float alpha;
    public static float thickness;
    public static float blinkalpha;
    public static float blinkspeed;
    public static boolean diffButtonLoc;
    public static boolean disableDepthBuffer;
    public static boolean gtMachineMode;
    public static int breakAnimation;

    public static int NONE = 0;
    public static int SHRINK = 1;
    public static int DOWN = 2;
    public static int ALPHA = 3;
    public static int LASTANIMATION_INDEX = 3;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.registerTickHandler();
        MinecraftForge.EVENT_BUS.register(this);

        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        int r = config.get("general", "red", 0)
            .getInt();
        int g = config.get("general", "green", 0)
            .getInt();
        int b = config.get("general", "blue", 0)
            .getInt();
        int a = config.get("general", "alpha", 255)
            .getInt();
        int t = config.get("general", "thickness", 4)
            .getInt();
        int ba = config.get("general", "blink_alpha", 100)
            .getInt();
        int bs = config.get("general", "blink_speed", 30)
            .getInt();
        diffButtonLoc = config.get("general", "different config button location", false)
            .getBoolean(false);
        disableDepthBuffer = config.get("general", "disable_depth", false)
            .getBoolean(false);
        gtMachineMode = config.get("general", "gt_machine_mode", true)
            .getBoolean(true);
        int bra = config.get("general", "break_animation", 0)
            .getInt();
        config.save();

        setRed(r / 255.0F);
        setGreen(g / 255.0F);
        setBlue(b / 255.0F);
        setAlpha(a / 255.0F);
        setThickness(t);
        setBlinkAlpha(ba / 255.0F);
        setBlinkSpeed(bs / 100.0F);
        setBreakAnimation(bra);

        logger.info("red=" + getRed() + " green=" + getGreen() + " blue=" + getBlue() + " alpha=" + getAlpha());
        logger.info("thickness=" + getThickness() + " diffbuttonloc=" + diffButtonLoc);
        logger.info("blinkalpha=" + getBlinkAlpha() + " blinkspeed=" + getBlinkSpeed());
    }

    public static void save() {
        config.load();
        config.get("general", "red", 0)
            .set(getRedInt());
        config.get("general", "green", 0)
            .set(getGreenInt());
        config.get("general", "blue", 0)
            .set(getBlueInt());
        config.get("general", "alpha", 255)
            .set(getAlphaInt());
        config.get("general", "thickness", 4)
            .set(getThicknessInt());
        config.get("general", "blink_alpha", 100)
            .set(getBlinkAlphaInt());
        config.get("general", "blink_speed", 30)
            .set(getBlinkSpeedInt());
        config.get("general", "disable_depth", false)
            .set(disableDepthBuffer);
        config.get("general", "gt_machine_mode", true)
            .set(gtMachineMode);
        config.get("general", "break_animation", 0)
            .set(breakAnimation);
        config.save();
        logger.info("SAVED: red=" + getRed() + " green=" + getGreen() + " blue=" + getBlue() + " alpha=" + getAlpha());
        logger.info(
            "SAVED: thickness=" + getThickness() + " blinkalpha=" + getBlinkAlpha() + " blinkspeed=" + getBlinkSpeed());
    }

    public static void reset(boolean mc) {
        setRed(0.0F);
        setGreen(0.0F);
        setBlue(0.0F);
        setAlpha(mc ? 0.4F : 1.0F);
        setThickness(mc ? 2.0F : 4.0F);
        setBlinkAlpha(mc ? 0.0F : 0.390625F);
        setBlinkSpeed(0.2F);
        disableDepthBuffer = false;
        gtMachineMode = true;
        setBreakAnimation(0);
        save();
    }

    public static void openSettingsGUI() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.saveOptions();
        mc.displayGuiScreen(new BOConfigGUI(mc.currentScreen));
    }

    @SubscribeEvent
    public void onDrawBlockSelectionBox(DrawBlockHighlightEvent e) {
        boolean holdingWand = isHoldingWandItem(e.player);
        boolean isGTMachine = gtMachineMode && shouldSkipForGTMachine(e.player, e.target);

        drawSelectionBox(e.player, e.target, e.subID, e.currentItem, e.partialTicks);

        if (!holdingWand && !isGTMachine) {
            e.setCanceled(true); // 普通情况：取消原版轮廓
        }
        // 手持 wand：不取消 → BetterBuildersWands 画额外轮廓（TODO：后续用自己的渲染替代）
        // GT 机器：不取消 → GT 原生九宫格渲染（我们画彩色轮廓，GT 画九宫格）
    }

    /** Draw GT machine wireframe grid overlay */
    private static void drawGTMachineGrid(EntityPlayer player, MovingObjectPosition mops, float partialTicks) {
        net.minecraft.world.World world = player.worldObj;
        if (mops == null || mops.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        int bx = mops.blockX, by = mops.blockY, bz = mops.blockZ;
        Block block = world.getBlock(bx, by, bz);
        if (block.getMaterial() == Material.air) return;

        block.setBlockBoundsBasedOnState(world, bx, by, bz);
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        AxisAlignedBB bb = block.getSelectedBoundingBoxFromPool(world, bx, by, bz)
            .expand(0.002D, 0.002D, 0.002D).offset(-d0, -d1, -d2);

        GL11.glLineWidth(1.0F);
        GL11.glColor4f(getRed(), getGreen(), getBlue(), getAlpha() * 0.7F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        Tessellator t = Tessellator.instance;
        double minX = bb.minX, minY = bb.minY, minZ = bb.minZ;
        double maxX = bb.maxX, maxY = bb.maxY, maxZ = bb.maxZ;
        double stepX = (maxX - minX) / 3.0;
        double stepY = (maxY - minY) / 3.0;
        double stepZ = (maxZ - minZ) / 3.0;

        // Draw 3x3 grid on each visible face (simplified: all 6 faces)
        for (int facing = 0; facing < 6; facing++) {
            t.startDrawing(GL11.GL_LINES);
            for (int i = 1; i < 3; i++) {
                for (int j = 1; j < 3; j++) {
                    double gx1 = minX + stepX * i, gz1 = minZ + stepZ * j;
                    switch (facing) {
                        case 0: // South (Z max)
                            t.addVertex(gx1 - stepX/2, minY, maxZ); t.addVertex(gx1 - stepX/2, maxY, maxZ);
                            t.addVertex(minX, minY + stepY * j, maxZ); t.addVertex(maxX, minY + stepY * j, maxZ);
                            break;
                        case 1: // North (Z min)
                            t.addVertex(gx1 - stepX/2, minY, minZ); t.addVertex(gx1 - stepX/2, maxY, minZ);
                            t.addVertex(minX, minY + stepY * j, minZ); t.addVertex(maxX, minY + stepY * j, minZ);
                            break;
                        case 2: // East (X max)
                            t.addVertex(maxX, minY, gz1 - stepZ/2); t.addVertex(maxX, maxY, gz1 - stepZ/2);
                            t.addVertex(maxX, minY + stepY * j, minZ); t.addVertex(maxX, minY + stepY * j, maxZ);
                            break;
                        case 3: // West (X min)
                            t.addVertex(minX, minY, gz1 - stepZ/2); t.addVertex(minX, maxY, gz1 - stepZ/2);
                            t.addVertex(minX, minY + stepY * j, minZ); t.addVertex(minX, minY + stepY * j, maxZ);
                            break;
                        case 4: // Top (Y max)
                            t.addVertex(gx1 - stepX/2, maxY, minZ); t.addVertex(gx1 - stepX/2, maxY, maxZ);
                            t.addVertex(minX, maxY, gz1 - stepZ/2); t.addVertex(maxX, maxY, gz1 - stepZ/2);
                            break;
                        case 5: // Bottom (Y min)
                            t.addVertex(gx1 - stepX/2, minY, minZ); t.addVertex(gx1 - stepX/2, minY, maxZ);
                            t.addVertex(minX, minY, gz1 - stepZ/2); t.addVertex(maxX, minY, gz1 - stepZ/2);
                            break;
                    }
                }
            }
            t.draw();
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /** 检测是否应该跳过渲染，让 GT 原生九宫格显示 */
    private static boolean shouldSkipForGTMachine(EntityPlayer player, MovingObjectPosition mops) {
        if (player == null || mops == null || mops.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return false;
        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() == null)
            return false;
        // 检测手持物品是否为 GT 扳手类工具
        if (!isGTWrench(held))
            return false;
        // 检测目标方块是否为 GT 机器
        net.minecraft.world.World world = player.worldObj;
        if (world == null)
            return false;
        return isGTMachine(world, mops.blockX, mops.blockY, mops.blockZ);
    }

    /** 检测玩家是否手持建筑之杖等可渲染额外轮廓的物品 */
    private static boolean isHoldingWandItem(EntityPlayer player) {
        if (player == null) return false;
        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() == null) return false;
        String clazz = held.getItem().getClass().getName();
        // BetterBuildersWands
        if (clazz.contains("portablejim.bbw")) return true;
        // Also check interface
        for (Class<?> iface : held.getItem().getClass().getInterfaces()) {
            if (iface.getName().contains("IWandItem")) return true;
        }
        return false;
    }

    /** 检测物品是否为 GT 扳手/螺丝刀等工具 */
    private static boolean isGTWrench(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        String name = stack.getItem().getUnlocalizedName();
        String clazz = stack.getItem().getClass().getName();
        // GT 工具类名特征
        if (clazz.contains("gregtech") || clazz.contains("GT_")) return true;
        // 物品名称特征
        if (name != null) {
            String lower = name.toLowerCase();
            if (lower.contains("wrench") || lower.contains("screwdriver")
                || lower.contains("soft hammer") || lower.contains("crowbar")
                || lower.contains("wire cutter") || lower.contains("plunger"))
                return true;
        }
        return false;
    }

    /** 检测方块是否为 GT 机器 */
    private static boolean isGTMachine(net.minecraft.world.World world, int x, int y, int z) {
        net.minecraft.tileentity.TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) return false;
        String clazz = te.getClass().getName();
        return clazz.contains("gregtech") || clazz.contains("GT_TileEntity");
    }

    // see RenderGlobal.drawSelectionBox
    public static void drawSelectionBox(EntityPlayer player, MovingObjectPosition mops, int subID,
        ItemStack par4ItemStack, float partialTicks) {
        net.minecraft.world.World world = player.worldObj;
        if (subID == 0 && mops.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            float breakProgress = getBlockDamage(player, mops);

            if (disableDepthBuffer) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(getThickness());
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);

            int bx = mops.blockX;
            int by = mops.blockY;
            int bz = mops.blockZ;
            Block block = world.getBlock(bx, by, bz);

            if (block.getMaterial() != Material.air) {
                block.setBlockBoundsBasedOnState(world, bx, by, bz);
                double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
                double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
                double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

                AxisAlignedBB bb = block.getSelectedBoundingBoxFromPool(world, bx, by, bz)
                    .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                    .offset(-d0, -d1, -d2);

                if (breakAnimation == DOWN) bb = bb.expand(0f, -breakProgress / 2, 0f)
                    .offset(0f, -breakProgress / 2, 0f);
                if (breakAnimation == SHRINK)
                    bb = bb.expand(-breakProgress / 2, -breakProgress / 2, -breakProgress / 2);

                drawBlinkingBlock(bb, (breakAnimation == ALPHA) ? breakProgress : getBlinkAlpha());

                GL11.glColor4f(getRed(), getGreen(), getBlue(), getAlpha());
                drawOutlinedBoundingBox(bb, -1);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);

            if (disableDepthBuffer) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    private static float getBlockDamage(EntityPlayer player, MovingObjectPosition block) {
        try {
            Field f;
            try {
                f = RenderGlobal.class.getDeclaredField("damagedBlocks");
            } catch (NoSuchFieldException e) {
                f = RenderGlobal.class.getDeclaredField("field_72738_E");
            }
            f.setAccessible(true);
            HashMap<Integer, DestroyBlockProgress> map = (HashMap<Integer, DestroyBlockProgress>) f
                .get(Minecraft.getMinecraft().renderGlobal);
            if (map != null) {
                // Use entity ID as key to find the player's breaking progress
                DestroyBlockProgress prg = map.get(player.getEntityId());
                if (prg != null) {
                    int bx = getDestroyBlockField(prg, "blockX");
                    int by = getDestroyBlockField(prg, "blockY");
                    int bz = getDestroyBlockField(prg, "blockZ");
                    if (bx == block.blockX && by == block.blockY && bz == block.blockZ) {
                        if (prg.getPartialBlockDamage() >= 0 && prg.getPartialBlockDamage() <= 10)
                            return prg.getPartialBlockDamage() / 10f;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0f;
    }

    private static int getDestroyBlockField(DestroyBlockProgress prg, String name) {
        try {
            Field f = prg.getClass()
                .getDeclaredField(name);
            f.setAccessible(true);
            return f.getInt(prg);
        } catch (Exception e) {
            try {
                // Try obfuscated name
                Field[] fields = prg.getClass()
                    .getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getType() == Integer.TYPE && !field.getName()
                        .startsWith("field_")) {
                        // Return the appropriate field based on order: entityId, x, y, z, partial, tick
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
        }
        return 0;
    }

    private static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox, int color) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3);

        if (color != -1) {
            tessellator.setColorOpaque_I(color);
        }

        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tessellator.draw();
        tessellator.startDrawing(3);

        if (color != -1) {
            tessellator.setColorOpaque_I(color);
        }

        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tessellator.draw();
        tessellator.startDrawing(1);

        if (color != -1) {
            tessellator.setColorOpaque_I(color);
        }

        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.draw();
    }

    private static void drawBlinkingBlock(AxisAlignedBB par1AxisAlignedBB, float alpha) {
        if (alpha > 0.0F) {
            if (getBlinkSpeed() > 0 && breakAnimation != ALPHA)
                alpha *= (float) Math.abs(Math.sin(Minecraft.getSystemTime() / 100.0D * getBlinkSpeed()));

            GL11.glColor4f(getRed(), getGreen(), getBlue(), alpha);
            renderDown(par1AxisAlignedBB);
            renderUp(par1AxisAlignedBB);
            renderNorth(par1AxisAlignedBB);
            renderSouth(par1AxisAlignedBB);
            renderWest(par1AxisAlignedBB);
            renderEast(par1AxisAlignedBB);
        }
    }

    public static void renderUp(AxisAlignedBB bb) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertex(bb.minX, bb.minY, bb.minZ);
        t.addVertex(bb.maxX, bb.minY, bb.minZ);
        t.addVertex(bb.maxX, bb.minY, bb.maxZ);
        t.addVertex(bb.minX, bb.minY, bb.maxZ);
        t.addVertex(bb.minX, bb.minY, bb.minZ);
        t.draw();
    }

    public static void renderDown(AxisAlignedBB bb) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertex(bb.minX, bb.maxY, bb.minZ);
        t.addVertex(bb.minX, bb.maxY, bb.maxZ);
        t.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        t.addVertex(bb.maxX, bb.maxY, bb.minZ);
        t.addVertex(bb.minX, bb.maxY, bb.minZ);
        t.draw();
    }

    public static void renderNorth(AxisAlignedBB bb) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertex(bb.minX, bb.minY, bb.minZ);
        t.addVertex(bb.minX, bb.maxY, bb.minZ);
        t.addVertex(bb.maxX, bb.maxY, bb.minZ);
        t.addVertex(bb.maxX, bb.minY, bb.minZ);
        t.addVertex(bb.minX, bb.minY, bb.minZ);
        t.draw();
    }

    public static void renderSouth(AxisAlignedBB bb) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertex(bb.minX, bb.minY, bb.maxZ);
        t.addVertex(bb.maxX, bb.minY, bb.maxZ);
        t.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        t.addVertex(bb.minX, bb.maxY, bb.maxZ);
        t.addVertex(bb.minX, bb.minY, bb.maxZ);
        t.draw();
    }

    public static void renderWest(AxisAlignedBB bb) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertex(bb.minX, bb.minY, bb.minZ);
        t.addVertex(bb.minX, bb.minY, bb.maxZ);
        t.addVertex(bb.minX, bb.maxY, bb.maxZ);
        t.addVertex(bb.minX, bb.maxY, bb.minZ);
        t.addVertex(bb.minX, bb.minY, bb.minZ);
        t.draw();
    }

    public static void renderEast(AxisAlignedBB bb) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertex(bb.maxX, bb.minY, bb.minZ);
        t.addVertex(bb.maxX, bb.maxY, bb.minZ);
        t.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        t.addVertex(bb.maxX, bb.minY, bb.maxZ);
        t.addVertex(bb.maxX, bb.minY, bb.minZ);
        t.draw();
    }

    public static float getRed() {
        return between(red, 0.0F, 1.0F);
    }

    public static float getGreen() {
        return between(green, 0.0F, 1.0F);
    }

    public static float getBlue() {
        return between(blue, 0.0F, 1.0F);
    }

    public static float getAlpha() {
        return between(alpha, 0.0F, 1.0F);
    }

    public static float getThickness() {
        return between(thickness, 0.1F, 7.0F);
    }

    public static float getBlinkAlpha() {
        return between(blinkalpha, 0.0F, 1.0F);
    }

    public static float getBlinkSpeed() {
        return between(blinkspeed, 0.0F, 1.0F);
    }

    public static void setRed(float r) {
        red = between(r, 0.0F, 1.0F);
    }

    public static void setGreen(float g) {
        green = between(g, 0.0F, 1.0F);
    }

    public static void setBlue(float b) {
        blue = between(b, 0.0F, 1.0F);
    }

    public static void setAlpha(float a) {
        alpha = between(a, 0.0F, 1.0F);
    }

    public static void setThickness(float t) {
        thickness = between(t, 0.1F, 7.0F);
    }

    public static void setBlinkAlpha(float ba) {
        blinkalpha = between(ba, 0.0F, 1.0F);
    }

    public static void setBlinkSpeed(float s) {
        blinkspeed = between(s, 0.0F, 1.0F);
    }

    public static void setBreakAnimation(int index) {
        breakAnimation = between(index, 0, LASTANIMATION_INDEX);
    }

    public static int getRedInt() {
        return Math.round(getRed() * 256.0F);
    }

    public static int getGreenInt() {
        return Math.round(getGreen() * 256.0F);
    }

    public static int getBlueInt() {
        return Math.round(getBlue() * 256.0F);
    }

    public static int getAlphaInt() {
        return Math.round(getAlpha() * 256.0F);
    }

    public static int getThicknessInt() {
        return Math.round(getThickness());
    }

    public static int getBlinkAlphaInt() {
        return Math.round(getBlinkAlpha() * 256.0F);
    }

    public static int getBlinkSpeedInt() {
        return Math.round(getBlinkSpeed() * 100.0F);
    }

    private static float between(float i, float x, float y) {
        if (i < x) i = x;
        if (i > y) i = y;
        return i;
    }

    private static int between(int i, int x, int y) {
        if (i < x) i = x;
        if (i > y) i = y;
        return i;
    }
}
