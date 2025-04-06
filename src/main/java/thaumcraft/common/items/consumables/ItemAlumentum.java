package thaumcraft.common.items.consumables;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.items.ItemTCBase;

public class ItemAlumentum extends ItemTCBase {
    public ItemAlumentum(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (!pPlayer.isCreative()) {
            pPlayer.getItemInHand(pUsedHand).shrink(1);
        }

        pPlayer.playSound(SoundEvents.EGG_THROW, 0.3f, 0.4f / (pLevel.random.nextFloat() * 0.4f + 0.8f));

        if (!pLevel.isClientSide) {
            EntityAlumentum alumentum = new EntityAlumentum(pPlayer, pLevel);
            alumentum.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -5.0f, 0.4f, 2.0f);
            pLevel.addFreshEntity(alumentum);
        }

        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 4800;
    }
}
