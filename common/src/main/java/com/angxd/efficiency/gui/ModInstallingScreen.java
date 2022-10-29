package com.angxd.efficiency.gui;

import com.angxd.efficiency.Efficiency;
import com.angxd.efficiency.platform.PlatformHelper;
import com.angxd.rinthify.ModrinthApi;
import com.angxd.rinthify.data.misc.Dependency;
import com.angxd.rinthify.data.misc.Version;
import com.angxd.rinthify.data.projects.Project;
import com.angxd.rinthify.data.projects.SearchHit;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class ModInstallingScreen extends Screen {
    private final Screen lastScreen;
    private final SearchHit project;
    private final Version version;
    private final Minecraft minecraft;
    private final ModrinthApi api;
    private Component status;

    protected ModInstallingScreen(Minecraft minecraft, Screen lastScreen, ModrinthApi api, SearchHit project, Version version) {
        super(Component.literal("Installation"));
        this.lastScreen = lastScreen;
        this.project = project;
        this.version = version;
        this.minecraft = minecraft;
        this.api = api;
        installBaseFile();
    }

    public void installBaseFile() {
        CompletableFuture completableFuture = CompletableFuture.runAsync(() -> {
            this.status = Component.translatable("efficiency.beginning_installation");

            try
            {
                Thread.sleep(1000);
                String fileLocation = PlatformHelper.getModsFolder() + "\\" + this.version.files.get(0).filename;
                Thread.sleep(500);
                installFromUrl(this.version.files.get(0).url, fileLocation);
                Thread.sleep(500);
                this.status = Component.translatable("efficiency.mod_installed", this.project.title);
                Thread.sleep(750);
                if(version.dependencies.stream().count() > 0) {
                    Thread.sleep(500);
                    for(Dependency dependency : version.dependencies) {
                        Version version1 = this.api.getEndpoints().VERSIONS.getVersion(dependency.versionId);
                        Project project1 = this.api.getEndpoints().PROJECTS.getProject(version1.project_id);
                        if(PlatformHelper.isModLoaded(project1.slug)) {
                            this.status = Component.translatable("efficiency.mod_already_installed", project1.title);
                        }else{
                            String fileLocation2 = PlatformHelper.getModsFolder() + "\\" + version1.files.get(0).filename;
                            Thread.sleep(500);
                            installFromUrl(version1.files.get(0).url, fileLocation2);
                            Thread.sleep(500);
                            this.status = Component.translatable("efficiency.mod_installed", project1.title);
                            Efficiency.REQUIRES_RESTART.add(project1.slug);
                        }
                        Thread.sleep(1000);
                        continue;
                    }
                }
                this.status = Component.translatable("efficiency.installation_complete", this.project.title);
                Thread.sleep(2000);
                Efficiency.REQUIRES_RESTART.add(this.project.slug);

                if(this.lastScreen instanceof ModInfoScreen modInfoScreen) {
                    modInfoScreen.installButton.active = false;
                }

                this.minecraft.setScreen(this.lastScreen);
            }catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public void installFromUrl(String urlString, String resultLocation) throws IOException {
        URL url = new URL(urlString);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(resultLocation);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.status, this.width / 2, this.height / 2, 16777215);
        super.render(poseStack, i, j, f);
    }

    @Override
    public void renderBackground(PoseStack matrices) {
        overlayBackground(0, 0, this.width, this.height, 64, 64, 64, 255, 255);
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
