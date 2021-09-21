package net.novauniverse.novashop.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import net.novauniverse.novashop.NovaShop;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependantSound;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ShopGUI {
	public static final int START_POS = 2 * 9;
	public static final int PER_PAGE = (6 - 2) * 9;

	public static void openMainMenu(Player player) {
		GUIReadOnlyHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6 * 9, "Shop");

		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.WHITE, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build());
		}

		inventory.setItem(0, new ItemBuilder(Material.BARRIER).setAmount(1).setName(ChatColor.RED + "Close").build());
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
		int totalPages = (int) Math.ceil(((double) category.getItems().size()) / ((double) PER_PAGE));
		int offset = (page - 1) * PER_PAGE;

		if (totalPages == 0) {
			totalPages = 1;
		}

		ShopGUIHolder holder = new ShopGUIHolder(page);
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6 * 9, category.getName() + ". Page " + page + "/" + totalPages);

		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.WHITE, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build());
		}

		inventory.setItem(1, new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Back").build());
		holder.addClickCallback(1, new GUIClickCallback() {
			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				if (entity instanceof Player) {
					openMainMenu((Player) entity);
				}
				return GUIAction.CANCEL_INTERACTION;
			}
		});

		inventory.setItem(0, new ItemBuilder(Material.PAPER).setName((page > 1 ? ChatColor.GREEN : ChatColor.RED) + "Previous page").build());
		holder.addClickCallback(0, new GUIClickCallback() {
			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				if (entity instanceof Player) {
					if (page > 1) {
						VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ORB_PICKUP, 1F, 1F);
						openCategory(player, category, page - 1);
					}
				}
				return GUIAction.CANCEL_INTERACTION;
			}
		});

		final int totalPagesFinal = totalPages;

		inventory.setItem(8, new ItemBuilder(Material.PAPER).setName((page < totalPages ? ChatColor.GREEN : ChatColor.RED) + "Next page").build());
		holder.addClickCallback(8, new GUIClickCallback() {
			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				if (entity instanceof Player) {
					if (page < totalPagesFinal) {
						VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ORB_PICKUP, 1F, 1F);
						openCategory(player, category, page + 1);
					}
				}
				return GUIAction.CANCEL_INTERACTION;
			}
		});

		for (int i = 0; i < PER_PAGE; i++) {
			int slot = i + START_POS;
			int index = offset + i;

			if (category.getItems().size() <= index) {
				break;
			}

			// Log.trace("i: " + i + " slot: " + slot + " index: " + index + " cat size: " +
			// category.getItems().size() + " PER_PAGE: " + PER_PAGE + " START_POS: " +
			// START_POS);

			ShopItem shopItem = category.getItems().get(index);

			inventory.setItem(slot, shopItem.getIcon());
			holder.addClickCallback(slot, new GUIClickCallback() {
				@Override
				public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
					if (entity instanceof Player) {
						Player player = (Player) entity;

						ShopAction action = null;

						switch (clickType) {
						case PICKUP_ALL:
							action = ShopAction.BUY;
							break;

						case PICKUP_HALF:
							action = ShopAction.SELL;
							break;

						default:
							break;
						}

						if (action != null) {
							onShopItemClick(player, shopItem, action);
						}
					}
					return GUIAction.CANCEL_INTERACTION;
				}
			});
		}

		player.openInventory(inventory);
	}

	private static void onShopItemClick(Player player, ShopItem shopItem, ShopAction action) {
		switch (action) {
		case SELL:
			if (!shopItem.canSell()) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You cant sell this item");
				break;
			}

			if (!NovaShop.getInstance().getEconomy().hasAccount(player)) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You dont have an economy account");
				break;
			}

			ItemStack itemToRemove = shopItem.getItem();
			int amountToRemove = itemToRemove.getAmount();

			Map<Integer, ItemStack> found = new HashMap<Integer, ItemStack>();

			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack s = player.getInventory().getItem(i);

				if (s == null) {
					continue;
				}

				if (s.getType() == Material.AIR) {
					continue;
				}

				if (s.isSimilar(itemToRemove)) {
					found.put(i, s);
				}
			}

			int foundAmount = 0;
			for (ItemStack s : found.values()) {
				foundAmount += s.getAmount();
			}

			if (foundAmount < amountToRemove) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You dont have enough items to sell");
				break;
			}

			int toRemove = amountToRemove;
			for (Integer i : found.keySet()) {
				if (toRemove <= 0) {
					break;
				}

				ItemStack stack = player.getInventory().getItem(i);
				if (toRemove >= stack.getAmount()) {
					toRemove -= stack.getAmount();
					player.getInventory().setItem(i, ItemBuilder.AIR);
				} else {
					ItemStack newStack = stack.clone();
					newStack.setAmount(newStack.getAmount() - toRemove);
					player.getInventory().setItem(i, newStack);
					toRemove = 0;
				}
			}

			EconomyResponse sellResponse = NovaShop.getInstance().getEconomy().depositPlayer(player, shopItem.getSellPrice());

			if (sellResponse.type == ResponseType.SUCCESS) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ORB_PICKUP, 1F, 1F);
			} else {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "Failed to deposit money to your account. Please contact staff. Reason: " + sellResponse.type.name());
				break;
			}

			break;

		case BUY:
			if (!shopItem.canBuy()) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You cant buy this item");
				break;
			}

			boolean hasEmptySpace = false;

			for (int i = 0; i < 4 * 9; i++) {
				ItemStack item = player.getInventory().getItem(i);

				if (item == null) {
					hasEmptySpace = true;
					break;
				}

				if (item.getType() == Material.AIR) {
					hasEmptySpace = true;
					break;
				}
			}

			if (!hasEmptySpace) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You need atleast 1 empty slot in your inventory to buy items");
				break;
			}

			if (!NovaShop.getInstance().getEconomy().hasAccount(player)) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You dont have an economy account");
				break;
			}

			if (!NovaShop.getInstance().getEconomy().has(player, shopItem.getBuyPrice())) {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "You dont have enough money to buy this");
				break;
			}

			EconomyResponse buyResponse = NovaShop.getInstance().getEconomy().withdrawPlayer(player, shopItem.getBuyPrice());

			if (buyResponse.type == ResponseType.SUCCESS) {
				player.getInventory().addItem(shopItem.getItem());
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ORB_PICKUP, 1F, 1F);
			} else {
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.ITEM_BREAK, 1F, 1F);
				player.sendMessage(ChatColor.RED + "Failed to withdraw money from your account. Reason: " + buyResponse.type.name());
				break;
			}

			break;

		default:
			player.sendMessage(ChatColor.RED + "Server messed upp while trying to process your interaction");
			break;
		}
	}
}