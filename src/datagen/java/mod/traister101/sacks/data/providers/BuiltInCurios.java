package mod.traister101.sacks.data.providers;

import top.theillusivec4.curios.api.CuriosDataProvider;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;

import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BuiltInCurios extends CuriosDataProvider {

	public BuiltInCurios(final PackOutput output, final ExistingFileHelper fileHelper, final CompletableFuture<Provider> registries) {
		super(SacksNSuch.MODID, output, fileHelper, registries);
	}

	@Override
	public void generate(final Provider registries, final ExistingFileHelper fileHelper) {
		createEntities("held").addEntities(EntityType.PLAYER).addSlots("belt");
		createEntities("worn").addEntities(EntityType.PLAYER).addSlots("back");
	}
}