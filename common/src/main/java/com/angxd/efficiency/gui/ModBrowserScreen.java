package com.angxd.efficiency.gui;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.widget.ModFilterSidebar;
import com.angxd.efficiency.gui.widget.ModList;
import com.angxd.efficiency.gui.widget.ModListEntry;
import com.angxd.efficiency.utils.ClientUtils;
import com.angxd.efficiency.utils.ConnectionManager;
import com.angxd.rinthify.data.misc.Version;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ModBrowserScreen extends Screen {
    protected final Screen lastScreen;
    public EditBox searchBox;
    public ModList list;
    public ModFilterSidebar sidebar;
    @Nullable
    private List<FormattedCharSequence> toolTip;

    public Button installButton;
    public Button infoButton;
    public ModBrowserScreen(Screen screen) {
        super(Component.translatable("efficiency.mod_browser"));
        this.lastScreen = screen;
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    public void setInstallButtonActive(boolean value) {
        this.installButton.active = value;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return super.mouseScrolled(d, e, f);
    }
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        this.searchBox = new EditBox(this.font, this.minecraft.screen.width - 230, 15, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
        this.list = new ModList(this, this.minecraft, this.width / 2 + 500, this.height, 48, this.height - 64, 36);
        this.sidebar = new ModFilterSidebar(this, this.minecraft, 130, this.height, 48, this.height - 15, 25);

        this.searchBox.setResponder(this.list::update);

        this.addWidget(this.list);
        this.addWidget(this.sidebar);
        this.addWidget(this.searchBox);

        this.infoButton = this.addRenderableWidget(new Button(150, this.height - 35, 100, 20, Component.translatable("efficiency.more_info"), (button) -> {
            if(this.list.getSelected() != null) this.list.getSelected().moreInfo();
        }));
        this.infoButton.active = false;

        this.installButton = this.addRenderableWidget(new Button(255, this.height - 35, 100, 20, Component.translatable("efficiency.install"), (button) -> {
            if(this.list.getSelected() != null) {
                Version validVersion = ClientUtils.getValidVersion(Efficiency.CONNECTION_MANAGER.API.getEndpoints().PROJECTS.getVersions(this.list.getSelected().modrinthProject.slug));

                this.minecraft.setScreen(new ConfirmScreen((value) -> {
                    this.minecraft.setScreen(value ? new ModInstallingScreen(this.minecraft, this, this.list.getSelected().modrinthProject, validVersion) : this);
                }, Component.translatable("efficiency.installation_prompt"), Component.literal(this.list.getSelected().modrinthProject.title + " : " + validVersion.name)));
            }
        }));

        this.addRenderableWidget(new Button(this.minecraft.screen.width - 180, this.height - 35, 150, 20, CommonComponents.GUI_CANCEL, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));

        this.setInstallButtonActive(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return super.keyPressed(i, j, k) ? true : this.searchBox.keyPressed(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.searchBox.charTyped(c, i);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        this.toolTip = null;

        this.list.render(poseStack, i, j, f);
        this.sidebar.render(poseStack, i, j, f);
        this.searchBox.render(poseStack, i, j, f);
        poseStack.scale(2, 2, 2);
        drawString(poseStack, this.font, this.title, 30, 10, 16777215);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        drawString(poseStack, this.font, "Showing " + this.list.children().stream().count() + " results", 193, 27, 16777215);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Efficiency.MOD_ID, "textures/gui/icon.png"));
        RenderSystem.enableBlend();
        GuiComponent.blit(poseStack, 15, 10, 0.0F, 0.0F, 32, 32, 32, 32);
        RenderSystem.disableBlend();

        if (this.toolTip != null) {
            this.renderTooltip(poseStack, this.toolTip, i, j);
        }

        super.render(poseStack, i, j, f);
    }

    public void setToolTip(List<FormattedCharSequence> list) {
        this.toolTip = list;
    }

    @Override
    public void renderBackground(PoseStack matrices) {
        overlayBackground(0, 0, this.width, this.height, 64, 64, 64, 255, 255);
    }

    @Override
    public void removed() {
        if (this.list != null) {
            this.list.children().forEach(ModListEntry::close);
        }
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
