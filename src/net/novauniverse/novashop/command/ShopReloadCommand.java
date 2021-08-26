package net.novauniverse.novashop.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.novashop.NovaShop;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class ShopReloadCommand extends NovaSubCommand {

	public ShopReloadCommand() {
		super("reload");

		setAllowedSenders(AllowedSenders.ALL);
		setPermissionDefaultValue(PermissionDefault.OP);
		setPermission("novashop.command.shop.reload");
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		try {
			NovaShop.getInstance().reloadShop();
			sender.sendMessage(ChatColor.GREEN + "The shop has been reloaded");
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Failed to reload shop. Error: " + e.getClass().getName() + " " + e.getMessage() + ". Check the console for more info");
		}
		return true;
	}
}