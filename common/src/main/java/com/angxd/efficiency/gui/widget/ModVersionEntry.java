package com.angxd.efficiency.gui.widget;

import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.efficiency.utils.RenderingUtils;
import com.angxd.rinthify.data.misc.Version;
import com.angxd.rinthify.data.projects.SearchHit;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ModVersionEntry extends ObjectSelectionList.Entry<ModVersionEntry> implements AutoCloseable {
    private final Minecraft minecraft;
    private final ModVersionsList list;
    public final Version version;

    public ModVersionEntry(ModVersionsList list, Version version) {
        this.minecraft = list.minecraft;
        this.list = list;
        this.version = version;
    }

    public boolean isValid() {
        return this.version.game_versions.contains(SharedConstants.getCurrentVersion().getName()) && this.version.loaders.contains(PlatformHelper.getLoader());
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        list.setSelected(this);
        return true;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Component getNarration() {
        return Component.literal("Mod Version");
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
        this.minecraft.font.draw(poseStack, this.version.name, k, (float) (j + 1), 16777215);
        this.minecraft.font.draw(poseStack, Component.translatable("efficiency.published_date",  StringUtils.left(String.valueOf(this.version.date_published), 10)), k, (float) (j + 10), 0xD6D5CB);
        this.minecraft.font.draw(poseStack, Component.translatable("efficiency.minecraft_versions", this.version.game_versions.toString()), k, (float) (j + 20), 0xD6D5CB);

        if(!this.version.loaders.isEmpty()) {
            int startX = k + minecraft.font.width(this.version.name) + 5;
            for (int i2 = 0; i2 < this.version.loaders.stream().count(); i2++) {
                if(i2 > 4) return;
                String category = this.version.loaders.get(i2).toString();
                RenderingUtils.drawSimpleBadge(
                        poseStack, minecraft,
                        startX, j - 1,
                        minecraft.font.width(category + 15), FormattedCharSequence.forward(StringUtils.capitalize(category), Style.EMPTY),
                        RenderingUtils.getBadgeColor(category), 0xCACACA);

                startX = startX + 20 + minecraft.font.width(category);
            }
        }
    }
}
