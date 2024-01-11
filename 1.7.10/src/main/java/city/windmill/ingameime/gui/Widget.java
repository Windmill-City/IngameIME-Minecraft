package city.windmill.ingameime.gui;

import net.minecraft.client.gui.Gui;

public class Widget extends Gui {
    public int offsetX, offsetY;
    public int TextColor = 0xFF_00_00_00;
    public int Background = 0xEB_EB_EB_EB;
    public int Padding = 1;
    public int X, Y;
    public int Width, Height;
    protected boolean isDirty = true;

    public boolean isActive() {
        return false;
    }

    public void layout() {
        // Update Width & Height before positioning
        Width += 2 * Padding;
        Height += 2 * Padding;

        X = offsetX;
        Y = offsetY;

        isDirty = false;
    }

    public void draw() {
        drawRect(X, Y, X + Width, Y + Height, Background);
    }

    public void setPos(int x, int y) {
        offsetX = x;
        offsetY = y;
        isDirty = true;
        layout();
    }
}
