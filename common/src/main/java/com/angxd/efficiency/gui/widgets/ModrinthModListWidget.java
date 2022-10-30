package com.angxd.efficiency.gui.widgets;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.ModrinthSearchScreen;
import com.angxd.efficiency.gui.old.ModBrowserScreen;
import com.angxd.efficiency.gui.old.widget.ModList;
import com.angxd.efficiency.gui.old.widget.ModListEntry;
import com.angxd.rinthify.ModrinthApi;
import com.angxd.rinthify.data.projects.SearchHit;
import com.angxd.rinthify.util.query.SearchProjectsQuery;
import com.kyanite.crossui.Position;
import com.kyanite.crossui.widget.container.SpruceEntryListWidget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModrinthModListWidget extends SpruceEntryListWidget<ModrinthModListEntryWidget> {
    public final ModrinthSearchScreen screen;
    public final ModrinthApi api;

    public List<ResourceLocation> TEXTURES = new ArrayList<>();

    private ArrayList<SearchHit> currentlyDisplayedMods;
    private String filter;
    private String facets = "";

    public ModrinthModListWidget(Position position, int width, int height, int anchorYOffset, Class<ModrinthModListEntryWidget> entryClass, ModrinthSearchScreen screen, ModrinthApi api) {
        super(position, width, height, anchorYOffset, entryClass);
        this.screen = screen;
        this.api = api;

        refreshProjects();
    }

    public void refreshProjects() {
        CompletableFuture.runAsync(() -> {
            SearchProjectsQuery.Builder query = SearchProjectsQuery.create()
                    .query(filter).limit(25);
            if(this.facets != null)
                query.facets(this.facets);

            this.currentlyDisplayedMods = api.getEndpoints().PROJECTS.searchForProjects(query.get()).hits;
            fillMods();
        });
    }

    private void fillMods() {
        this.client.doRunTask(() -> {
            this.clearEntries();
            if (currentlyDisplayedMods == null) return;

            for (SearchHit searchHit : currentlyDisplayedMods) {
                ModrinthModListEntryWidget entry = new ModrinthModListEntryWidget(searchHit, client, this);
                this.addEntry(entry);

                if(!this.TEXTURES.contains(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug)))
                    entry.iconId = ModList.ICON_MISSING;
                else entry.iconId = new ResourceLocation(Efficiency.MOD_ID, searchHit.slug);

                registerIcon(searchHit, entry);
            }
        });
    }

    public void registerIcon(SearchHit searchHit, ModrinthModListEntryWidget entry) {
        CompletableFuture.runAsync(() -> {
            if (!this.TEXTURES.contains(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug))) {
                if(!searchHit.icon_url.endsWith(".png")) {
                    entry.iconId = ModList.ICON_MISSING;
                    return;
                }
                this.TEXTURES.add(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug));
                NativeImage icon = null;
                try {
                    icon = NativeImage.read(new URL(searchHit.icon_url).openStream());
                    entry.nativeImage = icon;
                } catch (Exception e) {
                    entry.iconId = ModList.ICON_MISSING;
                    return;
                }

                // Icon is never null if it gets to this point, unwrap if statement checks.
                DynamicTexture dynamicTexture = new DynamicTexture(icon);
                client.getTextureManager().register(new ResourceLocation(Efficiency.MOD_ID, searchHit.slug), dynamicTexture);
                entry.iconId = new ResourceLocation(Efficiency.MOD_ID, searchHit.slug);
            }
        });
    }

    public void update() {
//        this.facets = this.screen.sidebar.getQuery();
        refreshProjects();
    }

    public void update(String filter) {
        this.filter = filter.replaceAll("[\\\\/:*?\"<>#|]", "");
//        this.facets = this.screen.sidebar.getQuery();
        refreshProjects();
    }
}
