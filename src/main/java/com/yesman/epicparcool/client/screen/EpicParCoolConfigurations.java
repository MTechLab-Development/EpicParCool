package com.yesman.epicparcool.client.screen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

@OnlyIn(Dist.CLIENT)
public class EpicParCoolConfigurations extends Screen {
	protected final Screen parentScreen;

	public EpicParCoolConfigurations(Screen screen) {
		super(Component.translatable("gui.epicparcool.config_screen.title"));
		this.parentScreen = screen;
	}

	@Override
	protected void init() {
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			this.onClose();
		}).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Component warning = Component.translatable("gui.epicparcool.config_screen.warn");

		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
		guiGraphics.drawString(this.font, warning, 6, this.height - 50, 16777215);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
}