package com.angxd.efficiency.gui.widget;

import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.efficiency.utils.ClientUtils;
import com.angxd.efficiency.utils.RenderingUtils;
import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.ModInfoScreen;
import com.angxd.rinthify.data.projects.SearchHit;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ModListEntry extends ObjectSelectionList.Entry<ModListEntry> implements AutoCloseable {
    private final Minecraft minecraft;
    public final SearchHit modrinthProject;
    public ResourceLocation iconId = null;
    @Nullable
    public NativeImage nativeImage = null;
    public final ModList list;
    public boolean available = false;

    public ModListEntry(ModList list, SearchHit modrinthProject) {
        this.modrinthProject = modrinthProject;
        this.minecraft = list.minecraft;
        this.list = list;

        CompletableFuture.runAsync(() -> {
            if(this.list.api.getEndpoints().PROJECTS.getVersions(this.modrinthProject.slug)
                    .stream().filter((version -> version.loaders.contains(PlatformHelper.getLoader()) && version.game_versions.contains(SharedConstants.getCurrentVersion().getName())))
                    .count() > 0) {
                available = true;
            }
        });
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        list.setSelected(this);
        return true;
    }

    public boolean isValid() {
        return this.available && !Efficiency.REQUIRES_RESTART.contains(this.modrinthProject.slug);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
        this.minecraft.font.draw(poseStack, this.modrinthProject.title, (float) (k + 32 + 3), (float) (j + 1), 16777215);
        if(ClientUtils.isMouseWithin(k + 32 + 3, j + 1, this.minecraft.font.width(this.modrinthProject.title) + 5, 5,n, o)) {
            FormattedCharSequence sequence = Component.translatable("efficiency.slug_tooltip", this.modrinthProject.slug).getVisualOrderText();
            this.list.screen.setToolTip(Collections.singletonList(sequence));
        }
        this.minecraft.font.draw(poseStack, this.modrinthProject.description.substring(0, Math.min(this.modrinthProject.description.length(), 75)) + "...", (float) (k + 32 + 3), (float) (j + 10), 0xD6D5CB);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.iconId);
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, k, j, 0.0F, 0.0F, 32, 32, 32, 32);
        RenderSystem.disableBlend();

        if(!this.modrinthProject.categories.isEmpty()) {
            int startX = k + minecraft.font.width(this.modrinthProject.title) + 40;
            for (int i2 = 0; i2 < this.modrinthProject.categories.stream().count(); i2++) {
                if(i2 > 4) return;
                String category = this.modrinthProject.categories.get(i2);
                RenderingUtils.drawSimpleBadge(
                        poseStack, minecraft,
                        startX, j,
                        minecraft.font.width(category + 15), FormattedCharSequence.forward(StringUtils.capitalize(category), Style.EMPTY),
                        RenderingUtils.getBadgeColor(category), 0xCACACA);

                startX = startX + 20 + minecraft.font.width(category);
            }
        }

        if(Efficiency.REQUIRES_RESTART.contains(this.modrinthProject.slug)) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Efficiency.MOD_ID, "textures/gui/restart.png"));
            GuiComponent.blit(poseStack, k - 5, j, 0.0F, 0.0F, 8, 8, 8, 8);

            if(ClientUtils.isMouseWithin(k -5, j, 8, 8,n, o)) {
                FormattedCharSequence sequence = Component.translatable("efficiency.restart_needed").getVisualOrderText();
                this.list.screen.setToolTip(Collections.singletonList(sequence));
            }
            return;
        }

        if(isValid()) {
            RenderSystem.setShaderTexture(0, new ResourceLocation("realms", "textures/gui/realms/checkmark.png"));
            GuiComponent.blit(poseStack, k - 5, j, 0.0F, 0.0F, 8, 8, 8, 8);

            if(ClientUtils.isMouseWithin(k -5, j, 8, 8,n, o)) {
                FormattedCharSequence sequence = Component.translatable("efficiency.mod_supports", SharedConstants.getCurrentVersion().getName()).getVisualOrderText();
                this.list.screen.setToolTip(Collections.singletonList(sequence));
            }
        }
    }

    public void moreInfo() {
        this.minecraft.setScreen(new ModInfoScreen(this.list.screen, this));
    }
    @Override
    public Component getNarration() {
        return Component.literal("Modrinth Entry");
    }

    @Override
    public void close() {
        if(nativeImage != null) nativeImage.close();
    }
}
