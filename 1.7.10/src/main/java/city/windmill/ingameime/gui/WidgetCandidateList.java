package city.windmill.ingameime.gui;

import net.minecraft.client.Minecraft;

import java.util.List;

public class WidgetCandidateList extends Widget {
    private List<String> Candidates = null;
    private int Selected = -1;

    WidgetCandidateList() {
        Padding = 3;
    }

    public void setContent(List<String> candidates, int selected) {
        Candidates = candidates;
        Selected = selected;
        isDirty = true;
        layout();
    }

    @Override
    public boolean isActive() {
        return Candidates != null;
    }

    @Override
    public void layout() {
        if (!isDirty) return;
        Height = Width = 0;
        if (!isActive()) return;

        Height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

        int i = 1;
        for (String candidate : Candidates) {
            Width += Padding * 2;
            String formatted = String.format("%d. %s", i++, candidate);
            Width += Minecraft.getMinecraft().fontRenderer.getStringWidth(formatted);
        }
        super.layout();
    }

    @Override
    public void draw() {
        if (!isActive()) return;
        super.draw();

        int x = X + Padding;
        int i = 1;
        for (String candidate : Candidates) {
            x += Padding;
            String formatted = String.format("%d. %s", i, candidate);
            if (Selected != i++ - 1)
                Minecraft.getMinecraft().fontRenderer.drawString(
                        formatted,
                        x,
                        Y + Padding,
                        TextColor
                );
            else {
                // Different background for selected one
                int xLen = Minecraft.getMinecraft().fontRenderer.getStringWidth(formatted);
                int fontH = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
                drawRect(x - 1, Y + Padding - 1, x + xLen, Y + Padding + fontH, 0xEB_B2DAE0);
                Minecraft.getMinecraft().fontRenderer.drawString(
                        formatted,
                        x,
                        Y + Padding,
                        TextColor
                );
            }
            x += Minecraft.getMinecraft().fontRenderer.getStringWidth(formatted);
            x += Padding;
        }
    }
}
