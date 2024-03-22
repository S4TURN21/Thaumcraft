package thaumcraft.api.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ItemGenericEssentiaContainer extends Item implements IEssentiaContainerItem {
    protected int base;

    public ItemGenericEssentiaContainer(Item.Properties pProperties, int base) {
        super(pProperties);
        this.base = base;
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTag()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTag());
            return (aspects.size() > 0) ? aspects : null;
        }
        return null;
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (!itemstack.hasTag()) {
            itemstack.setTag(new CompoundTag());
        }
        aspects.writeToNBT(itemstack.getTag());
    }

    @Override
    public boolean ignoreContainedAspects() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && !pStack.hasTag()) {
            Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
            setAspects(pStack, new AspectList().add(displayAspects[pLevel.random.nextInt(displayAspects.length)], base));
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        if (!pLevel.isClientSide && !pStack.hasTag()) {
            Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
            setAspects(pStack, new AspectList().add(displayAspects[pLevel.random.nextInt(displayAspects.length)], base));
        }
    }
}
