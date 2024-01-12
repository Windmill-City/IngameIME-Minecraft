package city.windmill.ingameime.gui;

import city.windmill.ingameime.ClientProxy;
import ingameime.PreEditRect;
import net.minecraft.client.Minecraft;

public class WidgetPreEdit extends Widget {
    private final int CursorWidth = 3;
    private String Content = null;
    private int Cursor = -1;

    public void setContent(String content, int cursor) {
        Cursor = cursor;
        Content = content;
        isDirty = true;
        layout();
    }

    @Override
    public void layout() {
        if (!isDirty) return;
        if (isActive()) {
            Width = Minecraft.getMinecraft().fontRenderer.getStringWidth(Content) + CursorWidth;
            Height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        } else {
            Width = Height = 0;
        }
        super.layout();

        WidgetCandidateList list = ClientProxy.Screen.CandidateList;
        list.setPos(X, Y + Height);
        // Check if overlap
        if (list.Y < Y + Height) {
            list.setPos(X, Y - list.Height);
        }

        // Update Rect
        if (!ClientProxy.LIBRARY_LOADED || ClientProxy.InputCtx == null) return;
        PreEditRect rect = new PreEditRect();
        rect.setX(X);
        rect.setY(Y);
        rect.setHeight(Height);
        rect.setWidth(Width);
        ClientProxy.InputCtx.setPreEditRect(rect);
    }

    @Override
    public boolean isActive() {
        return Content != null && !Content.isEmpty();
    }

    @Override
    public void draw() {
        if (!isActive()) return;
        super.draw();
        String beforeCursor = Content.substring(0, Cursor);
        String afterCursor = Content.substring(Cursor);
        int x = Minecraft.getMinecraft().fontRenderer.drawString(beforeCursor, X + Padding, Y + Padding, TextColor);
        // Cursor
        drawRect(x + 1, Y + Padding, x + 2, Y + Padding + Height, TextColor);
        Minecraft.getMinecraft().fontRenderer.drawString(afterCursor, x + CursorWidth, Y + Padding, TextColor);
    }
}
