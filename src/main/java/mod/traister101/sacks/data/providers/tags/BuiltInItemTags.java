package mod.traister101.sacks.data.providers.tags;

import top.theillusivec4.curios.api.CuriosApi;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.SNSTags;
import mod.traister101.sacks.common.items.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class BuiltInItemTags extends ItemTagsProvider {

	public BuiltInItemTags(final PackOutput packOutput, final CompletableFuture<Provider> lookupProvider,
			final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, blockTags, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(final Provider provider) {
		tag(SNSTags.Items.PREVENTED_IN_ITEM_CONTAINERS).add(SNSItems.ITEM_CONTAINERS.values().stream().map(RegistryObject::get).toArray(Item[]::new));
		tag(SNSTags.Items.ALLOWED_IN_SEED_POUCH).addTag(SNSTags.Items.TFC_SEEDS);

		// Curios
		tag(TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, "belt"))).add(SNSItems.ITEM_CONTAINERS.values()
				.stream()
				.map(RegistryObject::get)
				.filter(containerItem -> containerItem.getType() != DefaultContainers.FRAME_PACK)
				.toArray(Item[]::new));
		tag(TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, "back"))).add(
				SNSItems.ITEM_CONTAINERS.get(DefaultContainers.FRAME_PACK).get());
	}
}