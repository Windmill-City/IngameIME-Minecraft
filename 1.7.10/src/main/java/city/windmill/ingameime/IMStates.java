package city.windmill.ingameime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum IMStates implements IMEventHandler {
    Disabled {
        @Override
        public IMStates onControlFocus(@Nonnull Object control, boolean focused) {
            if (focused) {
                ActiveControl = control;
                IngameIME_Forge.LOG.info("Opened by control focus: {}", ActiveControl.getClass());
                Internal.setActivated(true);
                return OpenedAuto;
            } else {
                return this;
            }
        }

        @Override
        public IMStates onToggleKey() {
            IngameIME_Forge.LOG.info("Turned on by toggle key");
            Internal.setActivated(true);
            return OpenedManual;
        }

    },
    OpenedManual {
        @Override
        public IMStates onControlFocus(@Nonnull Object control, boolean focused) {
            // Ignore all focus event
            return this;
        }

        @Override
        public IMStates onMouseMove() {
            if (!Config.TurnOffOnMouseMove.getBoolean()) return this;
            IngameIME_Forge.LOG.info("Turned off by mouse move");
            Internal.setActivated(false);
            return Disabled;
        }
    },
    OpenedAuto {
        @Override
        public IMStates onControlFocus(@Nonnull Object control, boolean focused) {
            // Ignore not active focus one
            if (!focused && control != ActiveControl) return this;

            if (!focused) {
                IngameIME_Forge.LOG.info("Turned off by losing control focus: {}", ActiveControl.getClass());
                Internal.setActivated(false);
                return Disabled;
            }

            // Update active focused control
            if (ActiveControl != control) {
                ActiveControl = control;
                IngameIME_Forge.LOG.info("Opened by control focus: {}", ActiveControl.getClass());
                Internal.setActivated(true);
                ClientProxy.Screen.WInputMode.setActive(true);
            }
            return this;
        }
    };

    @Nullable
    public static Object ActiveScreen = null;
    @Nullable
    public static Object ActiveControl = null;

    @Override
    public IMStates onScreenClose() {
        if (ActiveScreen != null) IngameIME_Forge.LOG.info("Screen closed: {}", ActiveScreen.getClass());
        Internal.setActivated(false);
        ActiveScreen = null;
        return Disabled;
    }

    @Override
    public IMStates onScreenOpen(Object screen) {
        if (ActiveScreen == screen) return this;
        ActiveScreen = screen;
        if (ActiveScreen != null) IngameIME_Forge.LOG.info("Screen Opened: {}", ActiveScreen.getClass());
        return this;
    }

    @Override
    public IMStates onMouseMove() {
        return this;
    }

    @Override
    public IMStates onToggleKey() {
        IngameIME_Forge.LOG.info("Turned off by toggle key");
        Internal.setActivated(false);
        return Disabled;
    }
}
