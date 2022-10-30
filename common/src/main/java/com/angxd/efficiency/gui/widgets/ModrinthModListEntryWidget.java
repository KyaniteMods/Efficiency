package com.angxd.efficiency.gui.widgets;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.efficiency.utils.ClientUtils;
import com.angxd.efficiency.utils.RenderingUtils;
import com.angxd.rinthify.data.projects.SearchHit;
import com.kyanite.crossui.Tooltip;
import com.kyanite.crossui.widget.container.SpruceEntryListWidget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModrinthModListEntryWidget extends SpruceEntryListWidget.Entry {
    public final SearchHit modrinthProject;
    public final Minecraft client;
    public final ModrinthModListWidget list;
    private final Tooltip tooltip;

    public ResourceLocation iconId = null;
    public boolean available = false;

    @Nullable public NativeImage nativeImage = null;

    public ModrinthModListEntryWidget(SearchHit modrinthProject, Minecraft client, ModrinthModListWidget list) {
        this.modrinthProject = modrinthProject;
        this.client = client;
        this.list = list;

        FormattedCharSequence sequence = Component.translatable("efficiency.slug_tooltip", this.modrinthProject.slug).getVisualOrderText();
        this.tooltip = Tooltip.create(this.getX(), this.getY(), List.of(sequence));

        CompletableFuture.runAsync(() -> {
            if(this.list.api.getEndpoints().PROJECTS.getVersions(this.modrinthProject.slug)
                    .stream().filter((version -> version.loaders.contains(PlatformHelper.getLoader()) && version.game_versions.contains(SharedConstants.getCurrentVersion().getName())))
                    .count() > 0) {
                available = true;
            }
        });
    }

    public boolean isValid() {
        return this.available && !Efficiency.REQUIRES_RESTART.contains(this.modrinthProject.slug);
    }

    @Override
    protected void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.client.font.draw(poseStack, this.modrinthProject.title, (float) (getX() + 32 + 3), (float) (getY() + 1), 16777215);
        this.client.font.draw(poseStack, this.modrinthProject.description.substring(0, Math.min(this.modrinthProject.description.length(), 75)) + "...", (float) (getX() + 32 + 3), (float) (getY() + 10), 0xD6D5CB);

        if(this.isMouseHovered()) {
            tooltip.render(this.list.screen, poseStack);
        }

        try {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.iconId);
            RenderSystem.enableBlend();
            GuiComponent.blit(poseStack, this.getX(), this.getY(), 0.0F, 0.0F, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        } catch (NullPointerException e) {

        }

        if(!this.modrinthProject.categories.isEmpty()) {
            int startX = this.getX() + client.font.width(this.modrinthProject.title) + 40;
            for (int i2 = 0; i2 < this.modrinthProject.categories.stream().count(); i2++) {
                if(i2 > 4) return;
                String category = this.modrinthProject.categories.get(i2);
                RenderingUtils.drawSimpleBadge(
                        poseStack, client,
                        startX, this.getY(),
                        client.font.width(category + 15), FormattedCharSequence.forward(StringUtils.capitalize(category), Style.EMPTY),
                        RenderingUtils.getBadgeColor(category), 0xCACACA);

                startX = startX + 20 + client.font.width(category);
            }
        }

        if(Efficiency.REQUIRES_RESTART.contains(this.modrinthProject.slug)) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Efficiency.MOD_ID, "textures/gui/restart.png"));
            GuiComponent.blit(poseStack, getX() - 5, getY(), 0.0F, 0.0F, 8, 8, 8, 8);
            return;
        }

        if(isValid()) {
            RenderSystem.setShaderTexture(0, new ResourceLocation("realms", "textures/gui/realms/checkmark.png"));
            GuiComponent.blit(poseStack, getX() - 5, getY(), 0.0F, 0.0F, 8, 8, 8, 8);

//            if(ClientUtils.isMouseWithin(getX() -5, j, 8, 8,n, o)) {
//                FormattedCharSequence sequence = Component.translatable("efficiency.mod_supports", SharedConstants.getCurrentVersion().getName()).getVisualOrderText();
//                this.list.screen.setToolTip(Collections.singletonList(sequence));
//            }
        }
    }
}
