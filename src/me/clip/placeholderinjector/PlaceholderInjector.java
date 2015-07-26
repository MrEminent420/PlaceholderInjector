package me.clip.placeholderinjector;

import me.clip.placeholderinjector.chat.ChatInjector;
import me.clip.placeholderinjector.holographicdisplays.HolographicDisplaysInjector;
import me.clip.placeholderinjector.inventory.InventoryInjector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class PlaceholderInjector extends JavaPlugin {

	@Override
	public void onEnable() {
		
		if (setupHooks()) {
			
			loadCfg();
			
			if (getConfig().getBoolean("chat.inject")) {
				new ChatInjector(this);
			}
			
			if (getConfig().getBoolean("inventory.inject")) {
				new InventoryInjector(this);
			}
			
			if (getConfig().getBoolean("holographicdisplays.inject")) {
				
				if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
					new HolographicDisplaysInjector(this, getConfig().getInt("holographicdisplays.update_interval"));
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
				+ "\nCreated by extended_clip"
				+ "\n");
		
		if (c.contains("ignore_asyncplayerchatevent")) {
			c.set("ignore_asyncplayerchatevent", null);
		}
		c.addDefault("chat.inject", true);
		
		if (c.contains("chat.ignore_asyncplayerchatevent")) {
			c.set("chat.ignore_asyncplayerchatevent", null);
		}
		
		c.addDefault("inventory.inject", true);
		
		c.addDefault("holographicdisplays.inject", true);
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
