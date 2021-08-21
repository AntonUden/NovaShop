package net.novauniverse.novashop.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.novashop.NovaShop;
import net.novauniverse.novashop.shop.ShopGUI;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ShopCommand extends NovaCommand {

	public ShopCommand(String name) {
		super(name, NovaShop.getInstance());

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setPermission("novashop.command.shop");
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		ShopGUI.openMainMenu((Player) sender);

		return true;
	}
}