package thaumcraft.common.config;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blockentities.crafting.BlockEntityArcaneWorkbench;
import thaumcraft.common.blockentities.crafting.BlockEntityCrucible;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.blockentities.misc.BlockEntityNitor;
import thaumcraft.common.blocks.basic.BlockStoneTC;
import thaumcraft.common.blocks.basic.BlockTable;
import thaumcraft.common.blocks.crafting.BlockArcaneWorkbench;
import thaumcraft.common.blocks.crafting.BlockCrucible;
import thaumcraft.common.blocks.crafting.BlockResearchTable;
import thaumcraft.common.blocks.misc.BlockNitor;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.blocks.world.ore.ShardType;

public class ConfigBlocks {
    public static void initBlocks(RegisterEvent.RegisterHelper<Block> event) {
        BlocksTC.crystalAir = registerBlock("crystal_aer", new BlockCrystal(Aspect.AIR));
        BlocksTC.crystalFire = registerBlock("crystal_ignis", new BlockCrystal(Aspect.FIRE));
        BlocksTC.crystalWater = registerBlock("crystal_aqua", new BlockCrystal(Aspect.WATER));
        BlocksTC.crystalEarth = registerBlock("crystal_terra", new BlockCrystal(Aspect.EARTH));
        BlocksTC.crystalOrder = registerBlock("crystal_ordo", new BlockCrystal(Aspect.ORDER));
        BlocksTC.crystalEntropy = registerBlock("crystal_perditio", new BlockCrystal(Aspect.ENTROPY));
        BlocksTC.crystalTaint = registerBlock("crystal_vitium", new BlockCrystal(Aspect.FLUX));

        ShardType.AIR.setOre(BlocksTC.crystalAir);
        ShardType.FIRE.setOre(BlocksTC.crystalFire);
        ShardType.WATER.setOre(BlocksTC.crystalWater);
        ShardType.EARTH.setOre(BlocksTC.crystalEarth);
        ShardType.ORDER.setOre(BlocksTC.crystalOrder);
        ShardType.ENTROPY.setOre(BlocksTC.crystalEntropy);
        ShardType.FLUX.setOre(BlocksTC.crystalTaint);

        BlocksTC.stoneArcane = registerBlock("stone_arcane", new BlockStoneTC());
        BlocksTC.stoneArcaneBrick = registerBlock("stone_arcane_brick", new BlockStoneTC());
        BlocksTC.tableWood = registerBlock("table_wood", new BlockTable(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).destroyTime(2.0f)));
        for (DyeColor dye : DyeColor.values()) {
            BlocksTC.nitor.put(dye, registerBlock("nitor_" + dye.getName().toLowerCase(), new BlockNitor(BlockBehaviour.Properties.of(Material.DECORATION).strength(0.1f).sound(SoundType.WOOL).lightLevel((pBlockState) -> 15).noCollission().color(dye.getMaterialColor()), dye)));
        }
        BlocksTC.arcaneWorkbench = registerBlock("arcane_workbench", new BlockArcaneWorkbench());
        BlocksTC.researchTable = registerBlock("research_table", new BlockResearchTable(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).noOcclusion()));
        BlocksTC.crucible = registerBlock("crucible", new BlockCrucible(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion()));
    }

    public static void initBlockEntities() {
        ForgeRegistries.BLOCK_ENTITY_TYPES.register("arcane_workbench", BlockEntityType.Builder.of(BlockEntityArcaneWorkbench::new, BlocksTC.arcaneWorkbench).build(null));
        ForgeRegistries.BLOCK_ENTITY_TYPES.register("crucible", BlockEntityType.Builder.of(BlockEntityCrucible::new, BlocksTC.crucible).build(null));
        ForgeRegistries.BLOCK_ENTITY_TYPES.register("nitor", BlockEntityType.Builder.of(BlockEntityNitor::new, BlocksTC.nitor.values().toArray(new Block[0])).build(null));
        ForgeRegistries.BLOCK_ENTITY_TYPES.register("research_table", BlockEntityType.Builder.of(BlockEntityResearchTable::new, BlocksTC.researchTable).build(null));
    }

    private static Block registerBlock(String name, Block block) {
        return registerBlock(name, block, new BlockItem(block, new Item.Properties().tab(ConfigItems.TABTC)));
    }

    private static Block registerBlock(String name, Block block, BlockItem itemBlock) {
        ForgeRegistries.BLOCKS.register(name, block);
        ForgeRegistries.ITEMS.register(name, itemBlock);
        return block;
    }
}