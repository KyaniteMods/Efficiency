package com.angxd.efficiency.gui.widget;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.ModInfoScreen;
import com.angxd.rinthify.ModrinthApi;
import com.angxd.rinthify.data.misc.Version;
import com.angxd.rinthify.data.projects.SearchHit;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModVersionsList extends ObjectSelectionList<ModVersionEntry> {
    public final Minecraft minecraft;
    private List<Version> currentlyDisplayedVersions;
    private SearchHit searchHit;
    private final ModInfoScreen screen;
    public ModVersionsList(ModInfoScreen screen, SearchHit hit, Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.searchHit = hit;
        this.minecraft = minecraft;
        this.screen = screen;
        refreshProjects();
    }

    @Override
    public void setSelected(@Nullable ModVersionEntry entry) {
        super.setSelected(entry);
        this.screen.updateButtonStatus(entry.isValid());
    }

    private boolean renderBackground = true;
    private boolean renderTopAndBottom = true;
    private boolean renderHeader;
    private ModVersionEntry hovered;

    public void refreshProjects() {
        this.currentlyDisplayedVersions = Efficiency.CONNECTION_MANAGER.API.getEndpoints().PROJECTS.getVersions(this.searchHit.slug);
        fillMods();
    }

    private void fillMods() {
        this.clearEntries();
        if (this.currentlyDisplayedVersions == null) return;

        for (Version version : this.currentlyDisplayedVersions) {
            ModVersionEntry entry = new ModVersionEntry(this, version);
            this.addEntry(entry);
        }
    }

    private int getRowBottom(int i) {
        return this.getRowTop(i) + this.itemHeight;
    }

    @Override
    protected void renderList(PoseStack poseStack, int i, int j, float f) {
        int k = this.getRowLeft() - this.width / 2 + 120;
        int l = this.getRowWidth();
        int m = this.itemHeight - 4;
        int n = this.getItemCount();

        for(int o = 0; o < n; ++o) {
            int p = this.getRowTop(o);
            int q = this.getRowBottom(o);
            if (q >= this.y0 && p <= this.y1) {
                this.renderItem(poseStack, i, j, f, o, k, p, l, m);
            }
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 5;
    }

    @Override
    protected void renderSelection(PoseStack poseStack, int i, int j, int k, int l, int m) {
        int n = this.x0 + (this.width - j) - 1500;
        int o = this.x0 + (this.width + j) + 1500;
        fill(poseStack, n, i - 2, o, i + k + 2, l);
        fill(poseStack, n + 1, i - 1, o - 1, i + k + 1, m);
    }
}
