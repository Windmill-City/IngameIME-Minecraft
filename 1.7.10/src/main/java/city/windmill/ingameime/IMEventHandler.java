package city.windmill.ingameime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IMEventHandler {
    IMStates onScreenClose();

    IMStates onControlFocus(@Nonnull Object control, boolean focused);

    IMStates onScreenOpen(@Nullable Object screen);

    IMStates onToggleKey();

    IMStates onMouseMove();
}
