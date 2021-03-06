/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.gui.GuiContainerTFC;
import net.dries007.tfc.client.gui.GuiLiquidTransfer;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.container.ContainerFirePit;
import net.dries007.tfc.objects.container.ContainerLiquidTransfer;
import net.dries007.tfc.objects.container.ContainerLogPile;
import net.dries007.tfc.objects.container.ContainerSmallVessel;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.ceramics.ItemSmallVessel;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class TFCGuiHandler implements IGuiHandler
{
    private static final ResourceLocation SMALL_INVENTORY_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/small_inventory.png");
    private static final ResourceLocation FIRE_PIT_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit.png");

    // use this instead of player.openGui() -> avoids magic numbers
    public static void openGui(World world, BlockPos pos, EntityPlayer player, Type type)
    {
        player.openGui(TerraFirmaCraft.getInstance(), type.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void openGui(World world, EntityPlayer player, Type type)
    {
        player.openGui(TerraFirmaCraft.getInstance(), type.ordinal(), world, 0, 0, 0);
    }

    @Override
    @Nullable
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack stack = player.getHeldItemMainhand();
        Type type = Type.valueOf(ID);
        switch (type)
        {
            case LOG_PILE:
                TELogPile teLogPile = Helpers.getTE(world, pos, TELogPile.class);
                return teLogPile == null ? null : new ContainerLogPile(player.inventory, teLogPile);
            case SMALL_VESSEL:
                return new ContainerSmallVessel(player.inventory, stack.getItem() instanceof ItemSmallVessel ? stack : player.getHeldItemOffhand());
            case SMALL_VESSEL_LIQUID:
                return new ContainerLiquidTransfer(player.inventory, stack.getItem() instanceof ItemSmallVessel ? stack : player.getHeldItemOffhand());
            case MOLD:
                return new ContainerLiquidTransfer(player.inventory, stack.getItem() instanceof ItemMold ? stack : player.getHeldItemOffhand());
            case FIRE_PIT:
                TEFirePit teFirePit = Helpers.getTE(world, pos, TEFirePit.class);
                return new ContainerFirePit(player.inventory, teFirePit);
            default:
                return null;
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Container container = getServerGuiElement(ID, player, world, x, y, z);
        Type type = Type.valueOf(ID);
        switch (type)
        {
            case LOG_PILE:
                return new GuiContainerTFC(container, player.inventory, SMALL_INVENTORY_BACKGROUND, BlocksTFC.LOG_PILE.getTranslationKey());
            case SMALL_VESSEL:
                return new GuiContainerTFC(container, player.inventory, SMALL_INVENTORY_BACKGROUND, ItemsTFC.CERAMICS_FIRED_VESSEL.getTranslationKey());
            case SMALL_VESSEL_LIQUID:
                return new GuiLiquidTransfer(container, player, "", player.getHeldItemMainhand().getItem() instanceof ItemSmallVessel);
            case MOLD:
                return new GuiLiquidTransfer(container, player, "", player.getHeldItemMainhand().getItem() instanceof ItemMold);
            case FIRE_PIT:
                return new GuiContainerTFC(container, player.inventory, FIRE_PIT_BACKGROUND, BlocksTFC.FIREPIT.getTranslationKey());
            default:
                return null;
        }
    }

    public enum Type
    {
        LOG_PILE,
        SMALL_VESSEL,
        SMALL_VESSEL_LIQUID,
        MOLD,
        FIRE_PIT,
        NULL;

        private static Type[] values = values();

        @Nonnull
        private static Type valueOf(int id)
        {
            return id < 0 || id >= values.length ? NULL : values[id];
        }
    }
}
