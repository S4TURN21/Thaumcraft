package thaumcraft.common.blockentities.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.InventoryFake;
import thaumcraft.common.blockentities.BlockEntityThaumcraft;
import thaumcraft.common.blockentities.Tickable;
import thaumcraft.common.container.CrucibleContainer;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public class BlockEntityCrucible extends BlockEntityThaumcraft implements Tickable, IAspectContainer {
    private long counter;
    private int delay;
    int prevcolor;
    int prevx;
    int prevy;
    public short heat;
    public AspectList aspects;
    public FluidTank tank;

    public BlockEntityCrucible(BlockPos pPos, BlockState pBlockState) {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation("thaumcraft:crucible")), pPos, pBlockState);
        this.aspects = new AspectList();
        this.counter = -100L;
        this.delay = 0;
        this.prevcolor = 0;
        this.prevx = 0;
        this.prevy = 0;
        this.tank = new FluidTank(1000);
        this.tank.setFluid(new FluidStack(Fluids.WATER, 0));
        this.heat = 0;
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        this.heat = nbt.getShort("Heat");
        this.tank.readFromNBT(nbt);
        if (nbt.contains("Empty")) {
            this.tank.setFluid(FluidStack.EMPTY);
        }
        this.aspects.readFromNBT(nbt);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putShort("Heat", this.heat);
        this.tank.writeToNBT(nbt);
        this.aspects.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void tick() {
        ++this.counter;
        int prevheat = heat;
        if (!this.level.isClientSide) {
            if (tank.getFluidAmount() > 0) {
                BlockState block = level.getBlockState(getBlockPos().below());
                if (block.getMaterial() == Material.LAVA || block.getMaterial() == Material.FIRE || BlocksTC.nitor.containsValue(block.getBlock()) || block.getBlock() == Blocks.MAGMA_BLOCK) {
                    if (heat < 200) {
                        ++heat;
                        if (prevheat < 151 && heat >= 151) {
                            setChanged();
                            syncTile(false);
                        }
                    }
                } else if (heat > 0) {
                    --heat;
                    if (heat == 149) {
                        setChanged();
                    }
                }
            } else if (heat > 0) {
                --heat;
            }
            if (this.aspects.visSize() > 500) {
                spillRandom();
            }
            if (counter >= 100L) {
                spillRandom();
                counter = 0L;
            }
        } else if (tank.getFluidAmount() > 0) {
            drawEffects();
        }
        if (level.isClientSide && prevheat < 151 && heat >= 151) {
            ++heat;
        }
    }

    private void drawEffects() {
        if (heat > 150) {
            FXDispatcher.INSTANCE.crucibleFroth(worldPosition.getX() + 0.2f + level.random.nextFloat() * 0.6f, worldPosition.getY() + getFluidHeight(), worldPosition.getZ() + 0.2f + level.random.nextFloat() * 0.6f);
            if (this.aspects.visSize() > 500) {
                for (int a = 0; a < 2; ++a) {
                    FXDispatcher.INSTANCE.crucibleFrothDown((float) worldPosition.getX(), (float) (worldPosition.getY() + 1), worldPosition.getZ() + level.random.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float) (worldPosition.getX() + 1), (float) (worldPosition.getY() + 1), worldPosition.getZ() + level.random.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown(worldPosition.getX() + level.random.nextFloat(), (float) (worldPosition.getY() + 1), (float) worldPosition.getZ());
                    FXDispatcher.INSTANCE.crucibleFrothDown(worldPosition.getX() + level.random.nextFloat(), (float) (worldPosition.getY() + 1), (float) (worldPosition.getZ() + 1));
                }
            }
        }
        if (level.random.nextInt(6) == 0 && this.aspects.size() > 0) {
            int color = this.aspects.getAspects()[level.random.nextInt(this.aspects.size())].getColor() - 0x1000000;
            int x = 5 + level.random.nextInt(22);
            int y = 5 + level.random.nextInt(22);
            this.delay = level.random.nextInt(10);
            this.prevcolor = color;
            this.prevx = x;
            this.prevy = y;
            Color c = new Color(color);
            float r = c.getRed() / 255.0f;
            float g = c.getGreen() / 255.0f;
            float b = c.getBlue() / 255.0f;
            FXDispatcher.INSTANCE.crucibleBubble(worldPosition.getX() + x / 32.0f + 0.015625f, worldPosition.getY() + 0.05f + getFluidHeight(), worldPosition.getZ() + y / 32.0f + 0.015625f, r, g, b);
        }
    }

    public void ejectItem(ItemStack items) {
        boolean first = true;
        do {
            ItemStack spitout = items.copy();
            if (spitout.getCount() > spitout.getMaxStackSize()) {
                spitout.setCount(spitout.getMaxStackSize());
            }
            items.shrink(spitout.getCount());
            EntitySpecialItem entityitem = new EntitySpecialItem(level, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.71f, worldPosition.getZ() + 0.5f, spitout);
            entityitem.setDeltaMovement(0.075f, (first ? 0.0 : ((level.random.nextFloat() - level.random.nextFloat()) * 0.01f)), (first ? 0.0 : ((level.random.nextFloat() - level.random.nextFloat()) * 0.01f)));
            level.addFreshEntity(entityitem);
            first = false;
        } while (items.getCount() > 0);
    }

    public ItemStack attemptSmelt(ItemStack item, UUID thrower) {
        boolean bubble = false;
        boolean craftDone = false;
        int stackSize = item.getCount();
        Player player = level.getPlayerByUUID(thrower);
        for (int a = 0; a < stackSize; ++a) {
            Optional<CrucibleRecipe> crucibleRecipe = level.getRecipeManager().getRecipeFor(CrucibleRecipe.Type.INSTANCE, new CrucibleContainer(this.aspects, item), this.level);
            if (crucibleRecipe.isPresent() && tank.getFluidAmount() > 0) {
                var cr = crucibleRecipe.get();
                ItemStack out = cr.getResultItem().copy();
                if (player != null) {
                    ForgeEventFactory.firePlayerCraftingEvent(player, out, new InventoryFake(item));
                }
                this.aspects = cr.removeMatching(this.aspects);
                tank.drain(50, IFluidHandler.FluidAction.EXECUTE);
                ejectItem(out);
                craftDone = true;
                --stackSize;
                counter = -250L;
            } else {
                AspectList ot = ThaumcraftCraftingManager.getObjectTags(item);
                if (ot != null) {
                    if (ot.size() != 0) {
                        for (Aspect aspect : ot.getAspects()) {
                            this.aspects.add(aspect, ot.getAmount(aspect));
                        }
                        bubble = true;
                        --stackSize;
                        counter = -150L;
                    }
                }
            }
        }
        if (bubble) {
            level.playSound(null, worldPosition, SoundsTC.bubble, SoundSource.BLOCKS, 0.2f, 1.0f + level.random.nextFloat() * 0.4f);
            syncTile(false);
            level.blockEvent(worldPosition, BlocksTC.crucible, 2, 1);
        }
        if (craftDone) {
            syncTile(false);
            level.blockEvent(worldPosition, BlocksTC.crucible, 99, 0);
        }
        setChanged();
        if (stackSize <= 0) {
            return null;
        }
        item.setCount(stackSize);
        return item;
    }

    public void attemptSmelt(ItemEntity entity) {
        ItemStack item = entity.getItem();
        UUID username = entity.getThrower();
        ItemStack res = attemptSmelt(item, username);
        if (res == null || res.getCount() <= 0) {
            entity.discard();
        } else {
            item.setCount(res.getCount());
            entity.setItem(item);
        }
    }

    public float getFluidHeight() {
        float base = 0.3f + 0.5f * (this.tank.getFluidAmount() / (float) this.tank.getCapacity());
        float out = base + this.aspects.visSize() / 500.0f * (1.0f - base);
        if (out > 1.0f) {
            out = 1.001f;
        }
        if (out == 1.0f) {
            out = 0.9999f;
        }
        return out;
    }

    public void spillRandom() {
        if (this.aspects.size() > 0) {
            Aspect tag = this.aspects.getAspects()[level.random.nextInt(this.aspects.getAspects().length)];
            this.aspects.remove(tag, 1);
            AuraHelper.polluteAura(level, getBlockPos(), (tag == Aspect.FLUX) ? 1.0f : 0.25f, true);
        }
        setChanged();
        syncTile(false);
    }

    public void spillRemnants() {
        int vs = this.aspects.visSize();
        if (this.tank.getFluidAmount() > 0 || vs > 0) {
            this.tank.setFluid(FluidStack.EMPTY);
            AuraHelper.polluteAura(level, getBlockPos(), vs * 0.25f, true);
            int f = this.aspects.getAmount(Aspect.FLUX);
            if (f > 0) {
                AuraHelper.polluteAura(level, getBlockPos(), f * 0.75f, false);
            }
            this.aspects = new AspectList();
            level.blockEvent(worldPosition, BlocksTC.crucible, 2, 5);
            setChanged();
            syncTile(false);
        }
    }

    @Override
    public boolean triggerEvent(int pId, int pType) {
        if (pId == 99) {
            if (level.isClientSide) {
                FXDispatcher.INSTANCE.drawBamf(worldPosition.getX() + 0.5, worldPosition.getY() + 1.25f, worldPosition.getZ() + 0.5, true, true, Direction.UP);
                level.playLocalSound(worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f, SoundsTC.spill, SoundSource.BLOCKS, 0.2f, 1.0f, false);
            }
            return true;
        }
        if (pId == 1) {
            if (level.isClientSide) {
                FXDispatcher.INSTANCE.drawBamf(worldPosition.above(), true, true, Direction.UP);
            }
            return true;
        }
        if (pId == 2) {
            level.playLocalSound(worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f, SoundsTC.spill, SoundSource.BLOCKS, 0.2f, 1.0f, false);
            if (level.isClientSide) {
                for (int q = 0; q < 10; ++q) {
                    FXDispatcher.INSTANCE.crucibleBoil(worldPosition, this, pType);
                }
            }
            return true;
        }
        return super.triggerEvent(pId, pType);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.worldPosition.getX() + 1, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 1);
    }

    @Override
    public AspectList getAspects() {
        return this.aspects;
    }
}
