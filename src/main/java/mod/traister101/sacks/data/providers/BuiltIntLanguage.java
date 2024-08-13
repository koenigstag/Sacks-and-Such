package mod.traister101.sacks.data.providers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.items.SNSItems;
import mod.traister101.sacks.data.SmartLanguageProvider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BuiltIntLanguage extends SmartLanguageProvider {

	public BuiltIntLanguage(final PackOutput output) {
		super(output, SacksNSuch.MODID, "en_us");
	}

	/**
	 * Takes a string like dark_oak and converts it to Dark Oak.
	 */
	public static String langify(final String serializedName) {
		return Arrays.stream(serializedName.split("_"))
				.map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
				.collect(Collectors.joining(" "));
	}

	@Override
	protected void addTranslations() {
		addItemTranslations();

		// Keybinds
		add("sns.key.pickup", "Toggle Sack Pickup");
		add("sns.key.void", "Toggle Sack Voiding");
		// Creative Tab
		add("sns.creative_tab.sacks", "Sacks 'N Such");
	}

	@Override
	protected Iterable<Item> getKnownItems() {
		return SNSItems.ITEMS.getEntries().stream().map(RegistryObject::get)::iterator;
	}

	private void addItemTranslations() {
		add(SNSItems.UNFINISHED_LEATHER_SACK.get(), "Unfinished Leather Sack");
		add(SNSItems.REINFORCED_FIBER.get(), "Reinforced Fiber");
		add(SNSItems.REINFORCED_FABRIC.get(), "Reinforced Fabric");
		add(SNSItems.PACK_FRAME.get(), "Pack Frame");

		SNSItems.ITEM_CONTAINERS.forEach(
				(defaultContainer, registryObject) -> add(registryObject.get(), langify(defaultContainer.getSerializedName())));
	}
}