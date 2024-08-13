package mod.traister101.sacks.common.capability;

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
public sealed class LazyCapabilityProvider<Cap extends Capability<? super Handler>, Handler> implements ICapabilityProvider {

	private final HandlerFactory<Handler> handlerFactory;
	private final Cap capability;
	@Nullable
	protected Handler handler;
	@Nullable
	private LazyOptional<Handler> holder;

	/**
	 * @param capability The capability to lazily evaluate
	 * @param handlerFactory A factory for the handler
	 */
	public LazyCapabilityProvider(final Cap capability, final HandlerFactory<Handler> handlerFactory) {
		this.capability = capability;
		this.handlerFactory = handlerFactory;
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
		if (capability == cap) {
			return getHolder().cast();
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

	public static final class LazySerializedCapabilityProvider<Cap extends Capability<? super Handler>, Handler extends INBTSerializable<CompoundTag>> extends
			LazyCapabilityProvider<Cap, Handler> implements INBTSerializable<CompoundTag> {

		public LazySerializedCapabilityProvider(final Cap capability, final HandlerFactory<Handler> handlerFactory) {
			super(capability, handlerFactory);
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