package city.windmill.ingameime.gui;

import city.windmill.ingameime.IngameIME_Forge;
import ingameime.InputContext;

public class OverlayScreen extends Widget {
    public WidgetPreEdit PreEdit = new WidgetPreEdit(this);

    @Override
    public boolean isActive() {
        InputContext inputCtx = IngameIME_Forge.InputCtx;
        return inputCtx != null && inputCtx.getActivated();
    }

    @Override
    public void layout() {
    }

    @Override
    public void draw() {
        if (!isActive()) return;
        PreEdit.draw();
    }
}
