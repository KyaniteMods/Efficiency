package com.angxd.efficiency.gui;

import com.angxd.efficiency.gui.widgets.ModrinthModListEntryWidget;
import com.angxd.efficiency.gui.widgets.ModrinthModListWidget;
import com.angxd.rinthify.ModrinthApi;
import com.kyanite.crossui.Position;
import com.kyanite.crossui.screen.SpruceScreen;
import com.kyanite.crossui.widget.text.SpruceTextFieldWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ModrinthSearchScreen extends SpruceScreen {
    private final Screen parent;
    private static final ModrinthApi api = ModrinthApi.builder().build();

    // Widgets
    private SpruceTextFieldWidget searchBar;
    private ModrinthModListWidget list;
    private List<FormattedCharSequence> tooltip;

    public ModrinthSearchScreen(Screen parent) {
        super(Component.translatable("efficiency.mod_browser"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.searchBar = new SpruceTextFieldWidget(Position.of((this.width / 2)-100, 15), 200, 20, Component.translatable("selectWorld.search"));
        this.list = new ModrinthModListWidget(Position.of(48, 36), this.width / 2 + (this.width / 4), this.height, 0, ModrinthModListEntryWidget.class, this, api);

        this.searchBar.setChangedListener(list::update);

        this.addWidget(searchBar);
        this.addWidget(list);
    }

    public void setToolTip(List<FormattedCharSequence> list) {
        this.tooltip = list;
    }
}
