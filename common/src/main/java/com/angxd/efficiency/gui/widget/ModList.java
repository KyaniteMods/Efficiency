package com.angxd.efficiency.gui.widget;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.ModBrowserScreen;
import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.rinthify.ModrinthApi;
import com.angxd.rinthify.data.projects.SearchHit;
import com.angxd.rinthify.util.query.SearchProjectsQuery;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModList extends ObjectSelectionList<ModListEntry>  {
    public final ModBrowserScreen screen;
    public final ModrinthApi api;
    private ArrayList<SearchHit> currentlyDisplayedMods;
    private String filter;
    private String facets;
    public List<ResourceLocation> TEXTURES = new ArrayList<>();
    public Minecraft minecraft;
    public static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");

    public ModList(ModBrowserScreen screen, Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.screen = screen;
        this.api = screen.api;
        this.minecraft = minecraft;
        refreshProjects();
        this.x0 = 150;
    }

    @Override
    public void setSelected(@Nullable ModListEntry entry) {
        super.setSelected(entry);
        this.screen.installButton.setMessage(Component.translatable(PlatformHelper.isModLoaded(entry.modrinthProject.slug) ? "efficiency.uninstall" : "efficiency.install"));
        if(entry.modrinthProject.project_id.equals("P7dR8mSH")) { // Fabric-API project id
            this.screen.setInstallButtonActive(false);
            return;
        }
        this.screen.setInstallButtonActive(entry.isValid());
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft() - this.width / 2 + 115;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0 && d < (double)this.getScrollbarPosition() && e >= (double)this.y0 && e <= (double)this.y1) {
            int j = this.getRowLeft();
            int k = this.getScrollbarPosition();
            int l = (int)Math.floor(e - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int m = l / this.itemHeight;
            if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
                ModListEntry modListEntry = this.getEntryAtPosition2(d, e);
                if(modListEntry != null) {
                    modListEntry.mouseClicked(d, e, i);
                }
            }
        }
        return false;
    }

    public ModListEntry getEntryAtPosition2(double d, double e) {
        int i = this.getRowWidth() + 180;
        int j = this.x0 + this.width / 2;
        int k = j - i;
        int l = j + i;
        int i1 = Mth.floor(e - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int j1 = i1 / this.itemHeight;
        return d < (double)this.getScrollbarPosition() && d >= (double)k && d <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null;
    }

    private int getRowBottom(int i) {
        return this.getRowTop(i) + this.itemHeight;
    }

    @Override
    protected void renderList(PoseStack poseStack, int i, int j, float f) {
        super.renderList(poseStack, i, j, f);
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    public void refreshProjects() {
        SearchProjectsQuery.Builder query = SearchProjectsQuery.create()
                .query(filter).limit(10);
        if(this.facets != null)
            query.facets(this.facets);

        this.currentlyDisplayedMods = screen.api.getEndpoints().PROJECTS.searchForProjects(query.get()).hits;
        fillMods();
    }
    public void update() {
        this.facets = this.screen.sidebar.getQuery();
        refreshProjects();
    }

    public void update(String filter) {
        this.filter = filter;
        this.facets = this.screen.sidebar.getQuery();
        refreshProjects();
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 223;
    }

    @Override
    protected void renderSelection(PoseStack poseStack, int i, int j, int k, int l, int m) {
        int n = this.x0 ;
        int o = this.x0 + (this.width + j) + 1500;
        fill(poseStack, n, i - 2, o, i + k + 2, l);
        fill(poseStack, n + 1, i - 1, o - 1, i + k + 1, m);
    }

    public void registerIcon(SearchHit searchHit, ModListEntry entry) {
        CompletableFuture.runAsync(() -> {
            if (!this.TEXTURES.contains(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug))) {
                if(!searchHit.icon_url.endsWith(".png") || searchHit.icon_url == null) {
                    entry.iconId = ModList.ICON_MISSING;
                    return;
                }
                this.TEXTURES.add(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug));
                NativeImage icon = null;
                try {
                    icon = NativeImage.read(new URL(searchHit.icon_url).openStream());
                    entry.nativeImage = icon;
                } catch (IOException e) {
                    entry.iconId = ModList.ICON_MISSING;
                    return;
                } catch (NullPointerException e) {
                    entry.iconId = ModList.ICON_MISSING;
                    return;
                }
                DynamicTexture dynamicTexture = new DynamicTexture(icon);
                if (dynamicTexture != null && icon != null) {
                    minecraft.getTextureManager().register(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug), dynamicTexture);
                    entry.iconId = new ResourceLocation(Efficiency.MOD_ID, searchHit.slug);
                }else{
                    entry.iconId = ModList.ICON_MISSING;
                }
            }
        });
    }
    private void fillMods() {
        this.clearEntries();
        if (currentlyDisplayedMods == null) return;

        for (SearchHit searchHit : currentlyDisplayedMods) {
            ModListEntry entry = new ModListEntry(this, searchHit);
            this.addEntry(entry);

            if(!this.TEXTURES.contains(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug)))
                entry.iconId = ModList.ICON_MISSING;
            else entry.iconId = new ResourceLocation(Efficiency.MOD_ID, searchHit.slug);

            registerIcon(searchHit, entry);
        }
    }
}
