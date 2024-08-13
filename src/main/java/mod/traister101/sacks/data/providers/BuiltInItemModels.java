package mod.traister101.sacks.data.providers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.items.SNSItems;

import net.minecraft.data.PackOutput;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BuiltInItemModels extends ItemModelProvider {

	public BuiltInItemModels(final PackOutput output, final ExistingFileHelper existingFileHelper) {
		super(output, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		SNSItems.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(this::basicItem);
	}
}