package net.novauniverse.novashop.shop;

import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;

public class ShopGUIHolder extends GUIReadOnlyHolder {
	private int page;

	public ShopGUIHolder(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}
}