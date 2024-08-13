package mod.traister101.sacks.data.providers.tags;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;

import net.minecraftforge.common.data.*;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class BuiltInBlockTags extends BlockTagsProvider {

	public BuiltInBlockTags(final PackOutput output, final CompletableFuture<Provider> lookupProvider,
			@Nullable final ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(final Provider provider) {

	}
}