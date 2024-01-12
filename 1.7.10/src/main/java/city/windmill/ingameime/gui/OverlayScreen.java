package city.windmill.ingameime.gui;

import city.windmill.ingameime.ClientProxy;
import ingameime.InputContext;
import org.lwjgl.opengl.GL11;

public class OverlayScreen extends Widget {
    public WidgetPreEdit PreEdit = new WidgetPreEdit();
    public WidgetCandidateList CandidateList = new WidgetCandidateList();
    public WidgetInputMode WInputMode = new WidgetInputMode();

    @Override
    public boolean isActive() {
        InputContext inputCtx = ClientProxy.InputCtx;
        return inputCtx != null && inputCtx.getActivated();
    }

    @Override
    public void layout() {
    }

    @Override
    public void draw() {
        if (!isActive()) return;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        PreEdit.draw();
        CandidateList.draw();
        WInputMode.draw();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void setCaretPos(int x, int y) {
        PreEdit.setPos(x, y);
        WInputMode.setPos(x, y);
    }
}
