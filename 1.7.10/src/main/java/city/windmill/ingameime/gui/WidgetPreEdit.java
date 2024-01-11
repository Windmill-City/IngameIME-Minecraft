package city.windmill.ingameime.gui;

import city.windmill.ingameime.IngameIME_Forge;
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

        WidgetCandidateList candidateList = IngameIME_Forge.Screen.CandidateList;
        candidateList.setPos(X, Y + Height);
        // Check if exceed screen
        int displayWidth = Minecraft.getMinecraft().displayWidth;
        if (candidateList.X + Width > displayWidth)
            candidateList.setPos(displayWidth - candidateList.Width, Y + Height);
        int displayHeight = Minecraft.getMinecraft().displayHeight;
        if (candidateList.Y + Height > displayHeight)
            candidateList.setPos(candidateList.X, Y - candidateList.Height);


        if (!IngameIME_Forge.LIBRARY_LOADED || IngameIME_Forge.InputCtx == null) return;
        PreEditRect rect = new PreEditRect();
        rect.setX(X);
        rect.setY(Y);
        rect.setHeight(Height);
        rect.setWidth(Width);
        IngameIME_Forge.InputCtx.setPreEditRect(rect);
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
        int xLen = Minecraft.getMinecraft().fontRenderer.drawString(beforeCursor, X + Padding, Y + Padding, TextColor);
        // Cursor
        drawRect(X + Padding + xLen + 1, Y + Padding, X + Padding + xLen + 2, Y + Padding + Height, TextColor);
        Minecraft.getMinecraft().fontRenderer.drawString(afterCursor, X + Padding + xLen + CursorWidth, Y + Padding, TextColor);
    }
}
