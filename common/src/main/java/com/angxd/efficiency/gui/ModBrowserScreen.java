package com.angxd.efficiency.gui;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.gui.widget.ModFilterSidebar;
import com.angxd.efficiency.gui.widget.ModList;
import com.angxd.efficiency.gui.widget.ModListEntry;
import com.angxd.efficiency.utils.ClientUtils;
import com.angxd.rinthify.ModrinthApi;
import com.angxd.rinthify.data.misc.Category;
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
    public ModrinthApi api;
    public ModList list;
    public ModFilterSidebar sidebar;
    @Nullable
    private List<FormattedCharSequence> toolTip;

    public Button installButton;
    public Button infoButton;

    public List<Category> categories;
    public ModBrowserScreen(Screen screen) {
        super(Component.translatable("efficiency.mod_browser"));
        this.lastScreen = screen;
        try
        {
            this.api = ModrinthApi.builder()
                    .build();
            this.categories = this.api.getEndpoints().TAGS.getCategories().stream().filter((category -> category.project_type.equals("mod"))).toList();
        }catch (Exception e) {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public void tick() {
        this.searchBox.tick();
        super.tick();
    }

    public void setInstallButtonActive(boolean value) {
        this.installButton.active = value;
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.searchBox = new EditBox(this.font, this.width / 2 + 70, 15, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
        this.searchBox.setResponder((string) -> {
            this.list.update(string);
        });

        this.infoButton = this.addRenderableWidget(new Button(this.width / 2 - 170, this.height - 35, 100, 20, Component.translatable("efficiency.more_info"), (button) -> {
            if(this.list.getSelected() != null) this.list.getSelected().moreInfo();
        }));

        this.installButton = this.addRenderableWidget(new Button(this.width / 2 - 65, this.height - 35, 100, 20, Component.translatable("efficiency.install"), (button) -> {
            if(this.list.getSelected() != null) {
                Version validVersion = ClientUtils.getValidVersion(api.getEndpoints().PROJECTS.getVersions(this.list.getSelected().modrinthProject.slug));

                this.minecraft.setScreen(new ConfirmScreen((value) -> {
                    if (value) {
                        this.minecraft.setScreen(new ModInstallingScreen(this.minecraft, this, this.api, this.list.getSelected().modrinthProject, validVersion));
                    }else{
                        this.minecraft.setScreen(this);
                    }
                }, Component.translatable("efficiency.installation_prompt"), Component.literal(this.list.getSelected().modrinthProject.title + " : " + validVersion.name)));
            }
        }));

        this.addRenderableWidget(new Button(this.width / 2 + 125, this.height - 35, 150, 20, CommonComponents.GUI_CANCEL, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));


     //   if(!this.categories.isEmpty()) {
       //     int startY = 50;
        //    for (int i2 = 0; i2 < this.categories.stream().count(); i2++) {
         //       this.addRenderableWidget(new Checkbox(10, startY, 20, 20, Component.literal(this.categories.get(i2).name), false));
         //       startY = startY + 25;
        //    }
       // }
//
        this.list = new ModList(this, this.minecraft, this.width / 2 + 500, this.height, 48, this.height - 64, 36);
        this.sidebar = new ModFilterSidebar(this, this.minecraft, 130, this.height, 48, this.height - 15, 25);
        this.addWidget(this.searchBox);
        this.addWidget(this.list);
        this.addWidget(this.sidebar);
        this.setInstallButtonActive(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return super.keyPressed(i, j, k) ? true : this.searchBox.keyPressed(i, j, k);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.searchBox.charTyped(c, i);
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
 //       drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);

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
