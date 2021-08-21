package net.novauniverse.novashop.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.utils.JSONItemParser;

public class ShopDecoder {
	public static List<ShopCategory> decodeShopData(JSONObject json) {
		JSONArray jsonCategories = json.getJSONArray("categories");
		
		List<ShopCategory> categories = new ArrayList<>();

		for (int i = 0; i < jsonCategories.length(); i++) {
			JSONObject jsonCategory = jsonCategories.getJSONObject(i);

			if (!jsonCategory.has("icon")) {
				Log.error("ShopDecoder", "Category number " + i + " is missing the icon property");
				continue;
			}

			if (!jsonCategory.has("name")) {
				Log.error("ShopDecoder", "Category number " + i + " is missing the name property");
				continue;
			}

			String name = jsonCategory.getString("name");
			int slot = 22;

			if (jsonCategory.has("slot")) {
				slot = jsonCategory.getInt("slot");
			} else {
				Log.warn("ShopDecoder", "No slot defined defined for category " + i);
			}

			ItemStack icon = null;
			try {
				icon = JSONItemParser.itemFromJSON(jsonCategory.getJSONObject("icon"));
			} catch (Exception e) {
				Log.error("ShopDecoder", "An exception occured while parsing category " + i + ". " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				continue;
			}

			if (!jsonCategory.has("items")) {
				Log.error("ShopDecoder", "Category number " + i + " is missing the items property");
				continue;
			}

			List<ShopItem> items = new ArrayList<>();

			JSONArray itemsArray = jsonCategory.getJSONArray("items");

			for (int j = 0; j < itemsArray.length(); j++) {
				JSONObject jsonItem = itemsArray.getJSONObject(i);

				double buyPrice = 0;
				double sellPrice = 0;

				if (jsonItem.has("buy_price")) {
					buyPrice = jsonItem.getDouble("buy_price");
				}

				if (jsonItem.has("sell_price")) {
					sellPrice = jsonItem.getDouble("sell_price");
				}

				if (!jsonItem.has("item")) {
					Log.error("ShopDecoder", "Item number " + j + " in category number " + i + " is missing the item property");
					continue;
				}

				ItemStack item = null;

				try {
					item = JSONItemParser.itemFromJSON(jsonItem.getJSONObject("item"));
				} catch (Exception e) {
					Log.error("ShopDecoder", "An exception occured while parsing item number " + j + " in category number " + i + ". " + e.getClass().getName() + " " + e.getMessage());
					e.printStackTrace();
					continue;
				}

				items.add(new ShopItem(item, buyPrice, sellPrice));
			}

			new ShopCategory(slot, name, icon, items);
		}

		return categories;
	}
}