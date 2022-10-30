package com.angxd.efficiency.gui.widget;

import com.angxd.efficiency.gui.ModBrowserScreen;
import com.angxd.rinthify.ModrinthApi;
import com.angxd.rinthify.data.misc.Category;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Mth;

import java.util.List;

public class ModFilterSidebar extends ObjectSelectionList<ModFilterEntry> {
    public final ModBrowserScreen screen;
    public final ModrinthApi api;
    public Minecraft minecraft;
    private List<Category> currentlyDisplayedCategories;

    public ModFilterSidebar(ModBrowserScreen screen, Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.minecraft = minecraft;
        this.api = screen.api;;
        this.screen = screen;
        this.x0 = 20;
        refresh();
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft() + 20;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        for(ModFilterEntry filterEntry : children()) {
            filterEntry.checkbox.mouseClicked(d, e, i);
        }
        return false;
    }

    public String getQuery() {
        List<ModFilterEntry> activatedCategories = children().stream().filter(modFilterEntry -> modFilterEntry.checkbox.selected()).toList();
        if(activatedCategories.stream().count() < 1) return null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for(ModFilterEntry filterEntry : activatedCategories) {
            stringBuilder.append("[\"categories:" + filterEntry.category.name + "\"],");
        }
        System.out.println(stringBuilder.substring(0, stringBuilder.toString().length()-1) + "]");
        return stringBuilder.substring(0, stringBuilder.toString().length()-1) + "]";
    }
    @Override
    protected int addEntry(ModFilterEntry entry) {
        return super.addEntry(entry);
    }

    @Override
    protected void renderSelection(PoseStack poseStack, int i, int j, int k, int l, int m) {
        int n = this.x0 + (this.getRowWidth() - j);
        int o = this.x0 + (this.getRowWidth() + j) - 330;
        fill(poseStack, n, i - 2, o, i + k + 2, l);
        fill(poseStack, n + 1, i - 1, o - 1, i + k + 1, m);
    }


    private int getRowBottom(int i) {
        return this.getRowTop(i);
    }

    @Override
    protected void renderList(PoseStack poseStack, int i, int j, float f) {
        int k = this.getRowLeft();
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

    public void refresh() {
        try {
            this.currentlyDisplayedCategories = screen.api.getEndpoints().TAGS.getCategories().stream().filter((category -> category.project_type.equals("mod"))).toList();
            fillCategories();
        }catch (JsonSyntaxException e) {
            this.minecraft.setScreen(new ModBrowserScreen(this.screen));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() - 60;
    }

    private void fillCategories() {
        this.clearEntries();
        if (this.currentlyDisplayedCategories == null) return;

        for (Category category : this.currentlyDisplayedCategories) {
            ModFilterEntry entry = new ModFilterEntry(this, category);
            this.addEntry(entry);
        }
    }
}
