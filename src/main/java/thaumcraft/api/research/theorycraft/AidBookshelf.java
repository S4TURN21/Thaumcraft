package thaumcraft.api.research.theorycraft;

import net.minecraft.world.level.block.Blocks;

public class AidBookshelf implements ITheorycraftAid {
    @Override
    public Object getAidObject() {
        return Blocks.BOOKSHELF;
    }

    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[]{CardBalance.class, CardNotation.class, CardNotation.class, CardStudy.class, CardStudy.class, CardStudy.class};
    }
}
