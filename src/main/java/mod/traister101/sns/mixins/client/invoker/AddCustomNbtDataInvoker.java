package mod.traister101.sns.mixins.client.invoker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(Minecraft.class)
public interface AddCustomNbtDataInvoker {

	@Invoker("addCustomNbtData")
	void invokeAddCustomNbtData(ItemStack pStack, BlockEntity pBe);
}