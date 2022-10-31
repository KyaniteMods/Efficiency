package com.angxd.efficiency.gui.widget;

import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.rinthify.data.misc.Category;
import com.angxd.rinthify.data.projects.SearchHit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

public class ModFilterEntry extends ObjectSelectionList.Entry<ModFilterEntry> implements AutoCloseable{
    private final Minecraft minecraft;
    public final Category category;
    public final ModFilterSidebar list;
    public final Checkbox checkbox;
    public boolean wasSelected = false;
    public ModFilterEntry(ModFilterSidebar list, Category category) {
        this.category = category;
        this.minecraft = list.minecraft;
        this.list = list;
        this.checkbox = new Checkbox(25, 0, 20, 20, Component.literal(StringUtils.capitalize(this.category.name)), false);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        list.setSelected(this);
        this.checkbox.mouseClicked(d, e, i);
        return true;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Component getNarration() {
        return Component.literal("Category");
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
        if(this.wasSelected != this.checkbox.selected()) {
            this.wasSelected = this.checkbox.selected();
            this.list.screen.list.update();
        }

        this.checkbox.y = j;
        this.checkbox.render(poseStack, i, j, f);
    }
}
