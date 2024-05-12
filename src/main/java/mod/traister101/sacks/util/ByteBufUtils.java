package mod.traister101.sacks.util;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import lombok.experimental.UtilityClass;
import java.io.IOException;

@UtilityClass
public final class ByteBufUtils {

	private static final long LIMIT = 2097152L * 4;

	public static void writeExtendedItemStack(final FriendlyByteBuf friendlyByteBuf, final ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			friendlyByteBuf.writeBoolean(true);
			return;
		} else friendlyByteBuf.writeBoolean(false);

		friendlyByteBuf.writeInt(Item.getId(itemStack.getItem()));
		friendlyByteBuf.writeInt(itemStack.getCount());

		final CompoundTag itemStackTag;
		if (itemStack.getItem().isDamageable(itemStack) || itemStack.getItem().shouldOverrideMultiplayerNbt()) {
			itemStackTag = itemStack.getShareTag();
		} else itemStackTag = null;

		writeNBT(friendlyByteBuf, itemStackTag);
	}

	public static ItemStack readExtendedItemStack(final FriendlyByteBuf friendlyByteBuf) {
		if (friendlyByteBuf.readBoolean()) {
			return ItemStack.EMPTY;
		}

		final ItemStack itemstack = new ItemStack(Item.byId(friendlyByteBuf.readInt()), friendlyByteBuf.readInt());
		itemstack.readShareTag(readNBT(friendlyByteBuf));
		return itemstack;
	}

	public static void writeNBT(final FriendlyByteBuf friendlyByteBuf, final @Nullable CompoundTag compoundTag) {
		if (compoundTag == null) {
			friendlyByteBuf.writeBoolean(true);
			return;
		} else friendlyByteBuf.writeBoolean(false);

		try {
			NbtIo.write(compoundTag, new ByteBufOutputStream(friendlyByteBuf));
		} catch (final IOException ioexception) {
			throw new EncoderException(ioexception);
		}
	}

	@Nullable
	public static CompoundTag readNBT(final FriendlyByteBuf friendlyByteBuf) {
		if (friendlyByteBuf.readBoolean()) {
			return null;
		}

		try {
			return NbtIo.read(new ByteBufInputStream(friendlyByteBuf), new NbtAccounter(LIMIT));
		} catch (IOException ioexception) {
			throw new EncoderException(ioexception);
		}
	}
}
