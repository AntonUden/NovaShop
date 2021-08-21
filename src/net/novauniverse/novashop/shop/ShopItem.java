package net.novauniverse.novashop.shop;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.novashop.NovaShop;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ShopItem {
	private ItemStack item;
	private ItemStack icon;

	private double buyPrice;
	private double sellPrice;

	public ShopItem(ItemStack item, double buyPrice, double sellPrice) {
		this.item = item;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;

		ItemBuilder builder = new ItemBuilder(item);

		builder.addLore(ChatColor.GOLD + " ");
		if (canBuy()) {
			builder.addLore(ChatColor.GOLD + "Right click to buy for " + ChatColor.AQUA + buyPrice + NovaShop.getInstance().getCurrencyName());
		}

		if (canSell()) {
			builder.addLore(ChatColor.GOLD + "Left click to sell for " + ChatColor.AQUA + buyPrice + NovaShop.getInstance().getCurrencyName());
		}

		icon = builder.build();
	}

	public ItemStack getItem() {
		return item;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public boolean canBuy() {
		return this.buyPrice > 0;
	}

	public boolean canSell() {
		return this.sellPrice > 0;
	}
}