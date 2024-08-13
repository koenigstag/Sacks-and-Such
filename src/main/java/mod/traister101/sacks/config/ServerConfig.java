package mod.traister101.sacks.config;

import net.dries007.tfc.common.capabilities.size.Size;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public final class ServerConfig {

	public final ContainerConfig thatchBasket;
	public final ContainerConfig leatherSack;
	public final ContainerConfig burlapSack;
	public final ContainerConfig oreSack;
	public final ContainerConfig seedPouch;
	public final ContainerConfig framePack;
	public final BooleanValue doPickup;
	public final BooleanValue doVoiding;
	public final BooleanValue allPickup;
	public final BooleanValue allPickBlock;
	public final BooleanValue allAllowOre;
	public final BooleanValue allAllowFood;
	public final BooleanValue enableContainerInventoryInteraction;

	ServerConfig(final ForgeConfigSpec.Builder builder) {

		builder.push("Sack Config");

		thatchBasket = buildContainerConfig(builder, "Thatch Basket", true, true, 4, 32, Size.SMALL);
		leatherSack = buildContainerConfig(builder, "Leather Sack", true, true, 4, 64, Size.NORMAL);
		burlapSack = buildContainerConfig(builder, "Burlap Sack", true, false, 8, 48, Size.SMALL);
		oreSack = buildContainerConfig(builder, "Ore Sack", true, false, 1, 512, Size.SMALL);
		seedPouch = buildContainerConfig(builder, "Seed Pouch", true, false, 27, 64, Size.SMALL);
		framePack = buildContainerConfig(builder, "Frame Pack", false, false, 18, 64, Size.LARGE);

		builder.pop();

		builder.push("Global config");
		doPickup = builder.comment("Global control for automatic pickup, this will not force enable for every type")
				.worldRestart()
				.define("doPickup", true);
		doVoiding = builder.comment("A global toggle for item voiding, this will not force enable for every type").define("doVoiding", true);
		allPickup = builder.comment("Enable auto pickup for other container like items such as the TFC vessel.",
				"This may not always work as expected enable at your own discretion").define("allPickup", false);
		allPickBlock = builder.comment("This allows other containers such as vessels to support the pick block search").define("allPickBlock", false);
		allAllowOre = builder.comment("This makes all sack types capable of holding ore").define("allAllowOre", false);
		allAllowFood = builder.comment("This makes all sacks capable of holding food although they won't preserve it!").define("allAllowFood", false);
		enableContainerInventoryInteraction = builder.comment(
						"This allows containers to have items inserted and extracted from them from the inventory like vanilla Bundles")
				.define("enableContainerInventoryInteraction", true);
	}

	private ContainerConfig buildContainerConfig(final Builder builder, final String containerName, final boolean doPickup, final boolean doVoiding,
			final int slotCount, final int slotCap, final Size allowedSize) {
		builder.push(containerName);
		final ContainerConfig containerConfig = new ContainerConfig(builder, doPickup, doVoiding, slotCount, slotCap, allowedSize);
		builder.pop();
		return containerConfig;
	}

	public static final class ContainerConfig {

		public final BooleanValue doPickup;
		public final BooleanValue doVoiding;
		public final IntValue slotCount;
		public final IntValue slotCap;
		public final EnumValue<Size> allowedSize;

		public ContainerConfig(final ForgeConfigSpec.Builder builder, final boolean doPickup, final boolean doVoiding, final int slotCount,
				final int slotCap, final Size allowedSize) {
			this.doPickup = builder.comment("Determines if this container will automatically pickup items").define("doPickup", doPickup);
			this.doVoiding = builder.comment("Determines if this container can void items on pickup").define("doVoiding", doVoiding);
			this.slotCount = builder.comment("This config has a realistic cap of 27 as any higher the slots are added on top of the player slots")
					.defineInRange("slotCount", slotCount, 1, 27);
			this.slotCap = builder.comment("Item stack max for the type of container").defineInRange("slotCap", slotCap, 1, 512);
			this.allowedSize = builder.comment("The maximum item size allowed in the container").defineEnum("allowedSize", allowedSize);
		}
	}
}