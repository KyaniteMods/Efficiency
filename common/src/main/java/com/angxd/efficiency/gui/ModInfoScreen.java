package com.angxd.efficiency.gui;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.widget.ModListEntry;
import com.angxd.efficiency.gui.widget.ModVersionsList;
import com.angxd.efficiency.utils.RenderingUtils;
import com.angxd.efficiency.Efficiency;
import com.angxd.rinthify.data.projects.Project;
import com.angxd.rinthify.data.projects.SearchHit;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Environment(EnvType.CLIENT)
public class ModInfoScreen extends Screen {
    private final Screen lastScreen;
    private final SearchHit project;
    private final ModListEntry entry;
    private ModVersionsList versionsList;
    public Button installButton;
    public ModInfoScreen(Screen screen, ModListEntry entry) {
        super(Component.literal(entry.modrinthProject.title));
        this.lastScreen = screen;
        this.entry = entry;
        this.project = entry.modrinthProject;
    }

    public void updateButtonStatus(boolean value) {
        if(!Efficiency.REQUIRES_RESTART.contains(this.project.slug) && this.entry.available && !this.entry.modrinthProject.project_id.equals("P7dR8mSH"))
            this.installButton.active = value;
        else
            this.installButton.active = false;
    }

    @Override
    protected void init() {
        this.versionsList = new ModVersionsList(this, this.project, this.minecraft, this.entry.list.api, this.width, this.height, 90, this.height - 64, 36);
        this.addWidget(this.versionsList);

        this.addRenderableWidget(new Button(this.width / 2 + 100, this.height - 35, 150, 20, CommonComponents.GUI_CANCEL, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));

        this.installButton = this.addRenderableWidget(new Button(this.width / 2 - 95, this.height - 35, 100, 20, Component.translatable("efficiency.install"), (button) -> {
            if(this.versionsList.getSelected() != null) {
                this.minecraft.setScreen(new ConfirmScreen((value) -> {
                    if (value) {
                        this.minecraft.setScreen(new ModInstallingScreen(this.minecraft, this, this.versionsList.api, this.project, this.versionsList.getSelected().version));
                    }else{
                        this.minecraft.setScreen(this);
                    }
                }, Component.translatable("efficiency.installation_prompt"), Component.literal(this.project.title + " : " + this.versionsList.getSelected().version.name)));
            }
        }));

        this.updateButtonStatus(!Efficiency.REQUIRES_RESTART.contains(this.project.slug) && this.entry.available);
    }


    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        this.versionsList.render(poseStack, i, j, f);
        poseStack.scale(2, 2, 2);
        drawString(poseStack, this.font, this.title, 42, 10, 16777215);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        RenderingUtils.drawWrappedString(poseStack, this.minecraft, this.project.description, 85, 40, 450, 2, 0xD6D5CB);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, entry.iconId);
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, 15, 15, 0.0F, 0.0F, 64, 64, 64, 64);
        RenderSystem.disableBlend();

        if (!this.project.categories.isEmpty()) {
            int startX = 85;
            for (int i2 = 0; i2 < this.project.categories.stream().count(); i2++) {
                if (i2 > 4) return;
                String category = this.project.categories.get(i2);
                RenderingUtils.drawSimpleBadge(
                        poseStack, minecraft,
                        startX, this.minecraft.font.lineHeight + 55,
                        minecraft.font.width(category + 15), FormattedCharSequence.forward(StringUtils.capitalize(category), Style.EMPTY),
                        RenderingUtils.getBadgeColor(category), 0xCACACA);
                startX = startX + 20 + minecraft.font.width(category);
            }
        }

        super.render(poseStack, i, j, f);
    }

    @Override
    public void renderBackground(PoseStack matrices) {
        overlayBackground(0, 0, this.width, this.height, 64, 64, 64, 255, 255);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public void overlayBackground(int x1, int y1, int x2, int y2, int red, int green, int blue, int startAlpha, int endAlpha) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(x1, y2, 0.0D).uv(x1 / 32.0F, y2 / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.vertex(x2, y2, 0.0D).uv(x2 / 32.0F, y2 / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.vertex(x2, y1, 0.0D).uv(x2 / 32.0F, y1 / 32.0F).color(red, green, blue, startAlpha).endVertex();
        buffer.vertex(x1, y1, 0.0D).uv(x1 / 32.0F, y1 / 32.0F).color(red, green, blue, startAlpha).endVertex();
        tessellator.end();
    }
}
