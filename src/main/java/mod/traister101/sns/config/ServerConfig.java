package mod.traister101.sns.config;

import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public final class ServerConfig {

	public final ContainerConfig strawBasket;
	public final ContainerConfig leatherSack;
	public final ContainerConfig burlapSack;
	public final ContainerConfig oreSack;
	public final ContainerConfig seedPouch;
	public final ContainerConfig framePack;
	public final ContainerConfig lunchBox;
	public final BooleanValue doPickup;
	public final BooleanValue doVoiding;
	public final BooleanValue allPickup;
	public final BooleanValue allPickBlock;
	public final BooleanValue allAllowOre;
	public final BooleanValue allAllowFood;
	public final BooleanValue enableContainerInventoryInteraction;
	public final DoubleValue traitLunchboxModifier;

	ServerConfig(final ForgeConfigSpec.Builder builder) {

		builder.push("Container Item Config");

		strawBasket = buildContainerConfig(builder, "Straw Basket", true, true, true, 4, 32, Size.SMALL);
		leatherSack = buildContainerConfig(builder, "Leather Sack", false, false, true, 4, 64, Size.NORMAL);
		burlapSack = buildContainerConfig(builder, "Burlap Sack", true, true, false, 8, 48, Size.SMALL);
		oreSack = buildContainerConfig(builder, "Ore Sack", true, false, true, 1, 512, Size.SMALL);
		seedPouch = buildContainerConfig(builder, "Seed Pouch", true, false, true, 27, 64, Size.SMALL);
		framePack = buildContainerConfig(builder, "Frame Pack", false, false, false, 18, 64, Size.LARGE);
		lunchBox = buildContainerConfig(builder, "Lunch Box", false, false, true, 8, 4, Size.NORMAL);

		builder.pop();

		builder.push("Global config");
		doPickup = builder.comment("Global control for automatic pickup, this will not force enable for every type")
				.worldRestart()
				.define("doPickup", true);
		doVoiding = builder.comment("A global toggle for item voiding, this will not force enable for every type").define("doVoiding", true);
		allPickup = builder.comment("Enable auto pickup for other container like items such as the TFC vessel.",
				"This may not always work as expected enable at your own discretion").define("allPickup", false);
		allPickBlock = builder.comment("This allows other containers such as vessels to support the pick block search").define("allPickBlock", false);
		allAllowOre = builder.comment("This makes all container types capable of holding ore").define("allAllowOre", false);
		allAllowFood = builder.comment("This makes all container types capable of holding food although they won't preserve it!")
				.define("allAllowFood", false);
		enableContainerInventoryInteraction = builder.comment(
						"This allows containers to have items inserted and extracted from them via the inventory like vanilla Bundles")
				.define("enableContainerInventoryInteraction", true);

		traitLunchboxModifier = builder.comment(
						"The modifer for the 'Lunchbox' food trait. Values less than 1 extend food lifetime, values greater than one decrease it. A value of zero stops decay.")
				.defineInRange("traitLunchboxModifier", 0.6, 0.0, Double.MAX_VALUE);
	}

	private ContainerConfig buildContainerConfig(final Builder builder, final String containerName, final boolean doPickup, final boolean doVoiding,
			final boolean doInventoryTransfer, final int slotCount, final int slotCap, final Size allowedSize) {
		builder.push(containerName);
		final ContainerConfig containerConfig = new ContainerConfig(builder, doPickup, doVoiding, doInventoryTransfer, slotCount, slotCap,
				allowedSize);
		builder.pop();
		return containerConfig;
	}

	public static final class ContainerConfig {

		public final BooleanValue doPickup;
		public final BooleanValue doVoiding;
		public final BooleanValue doInventoryTransfer;
		public final IntValue slotCount;
		public final IntValue slotCap;
		public final EnumValue<Size> allowedSize;

		public ContainerConfig(final ForgeConfigSpec.Builder builder, final boolean doPickup, final boolean doVoiding,
				final boolean doInventoryTransfer, final int slotCount, final int slotCap, final Size allowedSize) {
			this.doPickup = builder.comment("Determines if this container will automatically pickup items").define("doPickup", doPickup);
			this.doVoiding = builder.comment("Determines if this container can void items on pickup").define("doVoiding", doVoiding);
			this.doInventoryTransfer = builder.comment("Determines if this container can transfer items in an inventory")
					.define("doInventoryTransfer", doInventoryTransfer);
			this.slotCount = builder.comment("This config has a realistic cap of 27 as any higher the slots are added on top of the player slots")
					.defineInRange("slotCount", slotCount, 1, 27);
			this.slotCap = builder.comment("Item stack max for the type of container").defineInRange("slotCap", slotCap, 1, 512);
			this.allowedSize = builder.comment("The maximum item size allowed in the container").defineEnum("allowedSize", allowedSize);
		}
	}
}