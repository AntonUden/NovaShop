package net.novauniverse.novashop.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;

import net.novauniverse.novashop.NovaShop;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ShopGUI {
	public static void openMainMenu(Player player) {
		GUIReadOnlyHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6 * 9, "Shop");

		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, VersionIndependantUtils.get().getColoredItem(DyeColor.WHITE, ColoredBlockType.GLASS_PANE));
		}

		inventory.setItem(0, new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Close").build());
		holder.addClickCallback(0, new GUIClickCallback() {
			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				entity.closeInventory();
				return GUIAction.CANCEL_INTERACTION;
			}
		});

		for (ShopCategory category : NovaShop.getInstance().getCategories()) {
			inventory.setItem(category.getSlot(), category.getIcon());
			holder.addClickCallback(category.getSlot(), new GUIClickCallback() {

				@Override
				public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
					if (entity instanceof Player) {
						openCategory((Player) entity, category);
					}
					return GUIAction.CANCEL_INTERACTION;
				}
			});
		}

		player.openInventory(inventory);
	}

	public static void openCategory(Player player, ShopCategory category) {
		openCategory(player, category, 1);
	}
	
	public static void openCategory(Player player, ShopCategory category, int page) {
		int totalPages = 1;
		
		GUIReadOnlyHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6*9, category.getName() + ". Page " + page + "/" + totalPages);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, VersionIndependantUtils.get().getColoredItem(DyeColor.WHITE, ColoredBlockType.GLASS_PANE));
		}
		
		inventory.setItem(0, new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Back").build());
		holder.addClickCallback(0, new GUIClickCallback() {
			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				if(entity instanceof Player) {
					openMainMenu((Player) entity);
				}
				return GUIAction.CANCEL_INTERACTION;
			}
		});
		
		player.openInventory(inventory);
	}
}