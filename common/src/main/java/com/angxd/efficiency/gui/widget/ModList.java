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
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.Platform;

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
    public List<ResourceLocation> TEXTURES = new ArrayList<>();
    public Minecraft minecraft;
    public static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");

    public ModList(ModBrowserScreen screen, Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.screen = screen;
        this.api = screen.api;
        this.minecraft = minecraft;
        refreshProjects();
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
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    public void refreshProjects() {
        this.currentlyDisplayedMods = screen.api.getEndpoints().PROJECTS.searchForProjects(SearchProjectsQuery.create().query(filter).limit(10).get()).hits;
        fillMods();
    }
    public void updateFilter(String string) {
        this.filter = string;
        refreshProjects();
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
