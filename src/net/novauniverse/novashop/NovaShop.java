package net.novauniverse.novashop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import net.milkbowl.vault.economy.Economy;
import net.novauniverse.novashop.command.ShopCommand;
import net.novauniverse.novashop.shop.ShopCategory;
import net.novauniverse.novashop.shop.ShopDecoder;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIManager;

public class NovaShop extends JavaPlugin implements Listener {
	private static NovaShop instance;

	private Economy economy;

	private String currencyName;
	private List<ShopCategory> categories;

	private File shopFile;

	public static NovaShop getInstance() {
		return instance;
	}

	public Economy getEconomy() {
		return economy;
	}

	@Override
	public void onEnable() {
		NovaShop.instance = this;

		categories = new ArrayList<>();

		saveDefaultConfig();
		currencyName = getConfig().getString("currency-name");

		Log.debug(getName(), "Currency name: " + currencyName);

		ModuleManager.require(GUIManager.class);

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		CommandRegistry.registerCommand(new ShopCommand(getConfig().getString("shop-command")));

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			Log.fatal(getName(), "No economy service found. Shutting down");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		economy = rsp.getProvider();

		shopFile = new File(getDataFolder().getAbsolutePath() + File.separator + "shop.json");

		try {
			if (!shopFile.exists()) {
				InputStream in = getClass().getResourceAsStream("/shop.json");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String lines = "";

				while (true) {
					String line = reader.readLine();

					if (line == null) {
						break;
					}

					lines += line;
				}

				JSONObject defaultShopJson = new JSONObject(lines);

				JSONFileUtils.saveJson(shopFile, defaultShopJson, 4);

				Log.info(getName(), "shop.json has been created");
			}
		} catch (IOException e) {
			Log.error(getName(), "Failed to setup default shop file");
			e.printStackTrace();
		}

		try {
			reloadShop();
		} catch (JSONException | IOException e) {
			Log.error(getName(), "Failed to decode shop.json. " + e.getClass().getName() + " " + e.getMessage());
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);
	}

	public String getCurrencyName() {
		return this.currencyName;
	}

	public List<ShopCategory> getCategories() {
		return categories;
	}

	public void reloadShop() throws JSONException, IOException {
		JSONObject json = JSONFileUtils.readJSONObjectFromFile(shopFile);
		categories = ShopDecoder.decodeShopData(json);
		Log.info(getName(), categories.size() + " categories loaded");
	}
}