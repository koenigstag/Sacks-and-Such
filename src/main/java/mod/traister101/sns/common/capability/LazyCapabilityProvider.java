package mod.traister101.sns.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.IItemHandler;

import org.jetbrains.annotations.Nullable;
import javax.annotation.Nonnull;

/**
 * A class to lazily initalize a handler like {@link IItemHandler} on first request.
 *
 * @see LazySerializedCapabilityProvider if your handler needs serializing
 */
public sealed class LazyCapabilityProvider<Handler> implements ICapabilityProvider {

	private final HandlerFactory<Handler> handlerFactory;
	private final Capability<? super Handler>[] capabilities;
	@Nullable
	protected Handler handler;
	@Nullable
	private LazyOptional<Handler> holder;

	/**
	 * @param handlerFactory A factory for the handler
	 * @param capabilities The capability to lazily evaluate
	 */
	@SafeVarargs
	@SuppressWarnings("varargs")
	public LazyCapabilityProvider(final HandlerFactory<Handler> handlerFactory, final Capability<? super Handler>... capabilities) {
		this.capabilities = capabilities;
		this.handlerFactory = handlerFactory;
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
		for (final var capability : capabilities) {
			if (capability == cap) {
				return getHolder().cast();
			}
		}

		return LazyOptional.empty();
	}

	protected final LazyOptional<Handler> getHolder() {
		if (holder == null) {
			holder = LazyOptional.of(this::getHandler);
		}
		return holder;
	}

	protected final Handler getHandler() {
		if (handler == null) {
			handler = handlerFactory.create();
		}
		return handler;
	}

	@FunctionalInterface
	public interface HandlerFactory<Handler> {

		@Nonnull
		Handler create();
	}

	public static final class LazySerializedCapabilityProvider<Handler extends INBTSerializable<CompoundTag>> extends
			LazyCapabilityProvider<Handler> implements INBTSerializable<CompoundTag> {

		@SafeVarargs
		@SuppressWarnings("varargs")
		public LazySerializedCapabilityProvider(final HandlerFactory<Handler> handlerFactory, final Capability<? super Handler>... capabilities) {
			super(handlerFactory, capabilities);
		}

		@Override
		public CompoundTag serializeNBT() {
			return getHandler().serializeNBT();
		}

		@Override
		public void deserializeNBT(final CompoundTag compoundTag) {
			getHandler().deserializeNBT(compoundTag);
		}
	}
}