package thaumcraft.common.items.consumables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;

public class ItemPhial extends ItemTCEssentiaContainer {
    public ItemPhial() {
        super(10);
    }

    public static ItemStack makePhial(Aspect aspect, int amt) {
        ItemStack i = new ItemStack(ItemsTC.phial, 1);
        ((IEssentiaContainerItem) i.getItem()).setAspects(i, new AspectList().add(aspect, amt));
        return i;
    }

    public static ItemStack makeFilledPhial(Aspect aspect) {
        return makePhial(aspect, 10);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeModeTab.TAB_SEARCH) {
            items.add(new ItemStack(this, 1));
            for (Aspect tag : Aspect.aspects.values()) {
                ItemStack i = new ItemStack(this, 1);
                setAspects(i, new AspectList().add(tag, base));
                items.add(i);
            }
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return (getAspects(pStack) != null && !getAspects(pStack).aspects.isEmpty()) ? Component.translatable(getDescriptionId(pStack), getAspects(pStack).getAspects()[0].getName()) : super.getName(pStack);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack pStack) {
        return (getAspects(pStack) != null && !getAspects(pStack).aspects.isEmpty()) ? super.getDescriptionId(pStack) + ".filled" : super.getDescriptionId(pStack);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return super.getDescriptionId();
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {

    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {

    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }
}
