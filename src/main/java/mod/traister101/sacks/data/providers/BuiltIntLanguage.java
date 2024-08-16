package mod.traister101.sacks.data.providers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.items.*;
import mod.traister101.sacks.data.SmartLanguageProvider;
import mod.traister101.sacks.util.SNSUtils;
import mod.traister101.sacks.util.SNSUtils.ToggleType;

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
		add("sns.key.pickup", "Toggle Container Item Pickup");
		add("sns.key.void", "Toggle Container Item Voiding");
		// Creative Tab
		add("sns.creative_tab.sacks", "Sacks 'N Such");
		// Tooltips
		add(ContainerItem.TYPE_NO_VOID, "This container type has voiding disabled");
		add(ContainerItem.TYPE_NO_PICKUP, "This container type has pickup disabled");
		add(ContainerItem.HOLD_SHIFT_TOOLTIP, "Hold [Shift]");
		add(ContainerItem.STATUS_TOOLTIP, "Status:");
		add(ContainerItem.PICKUP_TOOLTIP, "Item Pickup %s");
		add(ContainerItem.VOID_TOOLTIP, "Item Voiding %s");
		add(ToggleType.PICKUP.langKey, "Auto Pickup %s");
		add(ToggleType.VOID.langKey, "Voiding %s");
		add(SNSUtils.ENABLED, "Enabled");
		add(SNSUtils.DISABLED, "Disabled");
	}

	@Override
	protected Iterable<Item> getKnownItems() {
		return SNSItems.ITEMS.getEntries().stream().map(RegistryObject::get)::iterator;
	}

	private void addItemTranslations() {
		SNSItems.ITEMS.getEntries().forEach(this::addSimpleItem);
	}

	private void addSimpleItem(final RegistryObject<Item> item) {
		add(item.get(), langify(item.getId().getPath()));
	}
}