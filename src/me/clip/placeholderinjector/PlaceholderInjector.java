package me.clip.placeholderinjector;

import me.clip.placeholderinjector.chat.ChatListener;
import me.clip.placeholderinjector.chat.ChatPacketListener;
import me.clip.placeholderinjector.chat.SpigotChatPacketListener;
import me.clip.placeholderinjector.inventory.InventoryWindowPacketListener;
import me.clip.placeholderinjector.inventory.InventorySetSlotPacketListener;
import me.clip.placeholderinjector.sign.SignChangeListener;
import me.clip.placeholderinjector.sign.SignPacketListener;
import me.clip.placeholderinjector.tab.TabPacketListener;
import me.clip.placeholderinjector.title.TitlePacketListener;
import me.clip.placeholderinjector.holographicdisplays.HolographicDisplaysInjector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
@author extended_clip, GGhost
*/
public class PlaceholderInjector extends JavaPlugin {

	@Override
	public void onEnable() {
		
		getLogger().info("Created by extended_clip and GGhost");
		
		if (setupHooks()) {
			
			loadCfg();
			
			if (getConfig().getBoolean("chat.enabled")) {
				getLogger().info("Intercepting chat packets for placeholders");
				new ChatListener(this);
				if (getConfig().getBoolean("chat.intercept_spigot_chat_api_messages")) {
					getLogger().info("Spigot chat API messages will also be intercepted");
					new SpigotChatPacketListener(this);
				} else {
					new ChatPacketListener(this);
				}	
			}
			
			if (getConfig().getBoolean("inventory.enabled")) {
				getLogger().info("Intercepting inventory packets for placeholders");
				new InventorySetSlotPacketListener(this);
				new InventoryWindowPacketListener(this);
				//new InventoryItemsPacketListener(this);
			}
			
			if (getConfig().getBoolean("title.enabled")) {
				getLogger().info("Intercepting title packets for placeholders");
				new TitlePacketListener(this);
			}
			
			if (getConfig().getBoolean("tab.enabled")) {
				getLogger().info("Intercepting tab header and footer packets for placeholders");
				new TabPacketListener(this);
			}
			
			if (getConfig().getBoolean("signs.enabled")) {
				getLogger().info("Intercepting sign packets for placeholders");
				new SignPacketListener(this, getConfig().getInt("signs.update_interval", 30));
				new SignChangeListener(this);
			}
			
			if (getConfig().getBoolean("holographicdisplays.enabled")) {
				
				if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
					new HolographicDisplaysInjector(this, getConfig().getInt("holographicdisplays.update_interval", 60));
				}
			}			
			
		} else {
			
			getLogger().severe("Disabling PlaceholderInjector..");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}
		
	private void loadCfg() {
		FileConfiguration c = getConfig();
		c.options().header("PlaceholderInjector v" + getDescription().getVersion() 
				+ "\nCreated by extended_clip and GGhost"
				+ "\n"
				+ "\nAllow placeholders in any chat window message from any plugin"
				+ "\nIf you want to use placeholders in your essentials chat formatting"
				+ "\nyou must use {<placeholder>} instead of %<placeholder>%"
				+ "\nIf you would like messages created by the Spigot chat API intercepted also,"
				+ "\n(json messages with hover text, click events, etc)"
				+ "\nset intercept_spigot_chat_api_messages to true"
				+ "\nSet to false if you are not using Spigot!"
				+ "\nchat:"
				+ "\n  enabled: true/false"
				+ "\n  intercept_spigot_chat_api_messages: true/false"
				+ "\n"
				+ "\nAllow placeholders in any ItemStack name, lore, or inventory title"
				+ "\ninventory: "
				+ "\n  enabled: true/false"
				+ "\n"
				+ "\nAllow placeholders in any title or subtitle from any plugin"
				+ "\ntitle:"
				+ "\n  enabled: true/false"
				+ "\n"
				+ "\nAllow placeholders in the tab list header and footer"
				+ "\ntab:"
				+ "\n  enabled: true/false"
				+ "\n"
				+ "\nAllow placeholders in signs:"
				+ "\nsigns:"
				+ "\n  enabled: true/false"
				+ "\n  update_interval: <time in seconds to update sign placeholders, 0 to disable>"
				+ "\n"
				+ "\nAllow placeholders in HolographicDisplays holograms"
				+ "\nholographicdisplays:"
				+ "\n  enabled: true/false"
				+ "\n  update_interval: <time in seconds to update holo placeholders>"
				+ "\n"
				+ "\n"
				+ "\nTo add placeholders in chat messages, you need the permission node:"
				+ "\nplaceholderinjector.chat.bypass"
				+ "\n"
				+ "\nTo add placeholders in sign lines, you need the permission node:"
				+ "\nplaceholderinjector.signs.bypass"
				+ "\n");
		
		if (c.contains("chat.inject")) {
			c.set("chat.inject", null);
		}
		if (c.contains("inventory.inject")) {
			c.set("inventory.inject", null);
		}
		if (c.contains("holographicdisplays.inject")) {
			c.set("holographicdisplays.inject", null);
		}
		
		c.addDefault("chat.enabled", true);
		c.addDefault("chat.intercept_spigot_chat_api_messages", true);
		c.addDefault("inventory.enabled", true);
		c.addDefault("title.enabled", true);
		c.addDefault("tab.enabled", true);
		c.addDefault("signs.enabled", true);
		c.addDefault("signs.update_interval", 30);
		
		c.addDefault("holographicdisplays.enabled", true);
		c.addDefault("holographicdisplays.update_interval", 30);
		
		c.options().copyDefaults(true);
		saveConfig();
		reloadConfig();
	}
	
	
	private boolean setupHooks() {
		if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			getLogger().warning("Could not hook into PlaceholderAPI");
			return false;
		}
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			getLogger().warning("Could not hook into ProtocolLib");
			return false;
		}
		return true;
	}
	

}
