package thaumcraft.common.items.resources;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;

public class ItemCrystalEssence extends ItemTCEssentiaContainer {
    public ItemCrystalEssence() {
        super(1);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeModeTab.TAB_SEARCH) {
            for (Aspect tag : Aspect.aspects.values()) {
                ItemStack i = new ItemStack(this);
                this.setAspects(i, new AspectList().add(tag, this.base));
                items.add(i);
            }
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return (this.getAspects(pStack) != null && !this.getAspects(pStack).aspects.isEmpty()) ? Component.literal(I18n.get(pStack.getDescriptionId(), this.getAspects(pStack).getAspects()[0].getName())) : Component.literal(I18n.get(pStack.getDescriptionId(), ""));
    }
}
