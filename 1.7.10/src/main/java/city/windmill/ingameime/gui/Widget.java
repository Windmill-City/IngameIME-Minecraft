package city.windmill.ingameime.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Widget extends Gui {
    public int offsetX, offsetY;
    public int TextColor = 0xFF_00_00_00;
    public int Background = 0xEB_EB_EB_EB;
    public int Padding = 1;
    public int X, Y;
    public int Width, Height;
    public boolean DrawInline = true;
    protected boolean isDirty = true;

    public boolean isActive() {
        return false;
    }

    public void layout() {
        // Update Width & Height before positioning
        Width += 2 * Padding;
        Height += 2 * Padding;

        X = offsetX;
        Y = offsetY + (DrawInline ? 0 : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);

        // Check if exceed screen
        ScaledResolution scaledresolution = new ScaledResolution(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        int displayHeight = scaledresolution.getScaledHeight();
        int displayWidth = scaledresolution.getScaledWidth();
        if (X + Width > displayWidth) X = Math.max(0, displayWidth - Width);
        if (Y + Height > displayHeight) Y = (DrawInline ? displayHeight : offsetY) - Height;

        isDirty = false;
    }

    public void draw() {
        drawRect(X, Y, X + Width, Y + Height, Background);
    }

    public void setPos(int x, int y) {
        if (offsetX == x && offsetY == y) return;
        offsetX = x;
        offsetY = y;
        isDirty = true;
        layout();
    }
}
