package com.angxd.efficiency.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.awt.*;
import java.util.List;

public class RenderingUtils {
    public static void drawSimpleBadge(PoseStack matrices, Minecraft minecraft, int x, int y, int tagWidth, FormattedCharSequence text, int fillColor, int textColor) {
        GuiComponent.fill(matrices, x - 1, y - 1, x + tagWidth + 1, y + minecraft.font.lineHeight - 2 + 1, Color.decode(String.valueOf(fillColor)).darker().getRGB());
        GuiComponent.fill(matrices, x + 1, y, x + tagWidth, y + minecraft.font.lineHeight - 2, fillColor);
        minecraft.font.draw(matrices, text, (x + 1 + (tagWidth - minecraft.font.width(text)) / (float) 2), y, textColor);
    }

    /**
     * @author ModMenu/TerraformersMC
     * @link https://github.com/TerraformersMC/ModMenu/blob/1.19/src/main/java/com/terraformersmc/modmenu/util/DrawingUtil.java#L31
     */
    public static void drawWrappedString(PoseStack matrices, Minecraft minecraft, String string, int x, int y, int wrapWidth, int lines, int color) {
        while (string != null && string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        List<FormattedText> strings = minecraft.font.getSplitter().splitLines(Component.literal(string), wrapWidth, Style.EMPTY);
        for (int i = 0; i < strings.size(); i++) {
            if (i >= lines) {
                break;
            }
            FormattedText renderable = strings.get(i);
            if (i == lines - 1 && strings.size() > lines) {
                renderable = FormattedText.composite(strings.get(i), FormattedText.of("..."));
            }
            FormattedCharSequence line = Language.getInstance().getVisualOrder(renderable);
            int x1 = x;
            if (minecraft.font.isBidirectional()) {
                int width = minecraft.font.width(line);
                x1 += (float) (wrapWidth - width);
            }
            minecraft.font.draw(matrices, line, x1, y + i * minecraft.font.lineHeight, color);
        }
    }

    public static int getBadgeColor(String category) {
        if(category.equals("forge") || category.equals("fabric") || category.equals("quilt")) {
            return 0xff2b4b7c;
        }else if(category.equals("library")) {
            return 0xff354247;
        }else if(category.equals("utility") || category.equals("optimization")) {
            return 0xff107454;
        }else if(category.equals("adventure")) {
            return 0xff9c3030;
        }else if(category.equals("decoration")) {
            return 0xffbf5513;
        }
        else{
            return 0xff107454;
        }
    }


}
