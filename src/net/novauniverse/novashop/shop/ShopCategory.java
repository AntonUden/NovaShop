package net.novauniverse.novashop.shop;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ShopCategory {
	private int slot;
	private String name;
	private ItemStack icon;
	private List<ShopItem> items;

	public ShopCategory(int slot, String name, ItemStack icon, List<ShopItem> items) {
		this.slot = slot;
		this.name = name;
		this.icon = icon;
		this.items = items;

		this.icon = new ItemBuilder(this.icon).setName(name).build();
	}

	public int getSlot() {
		return slot;
	}

	public String getName() {
		return name;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public List<ShopItem> getItems() {
		return items;
	}
}