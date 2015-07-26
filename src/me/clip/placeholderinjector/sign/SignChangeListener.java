package me.clip.placeholderinjector.sign;

import java.util.regex.Matcher;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
@author extended_clip
*/
public class SignChangeListener implements Listener {
	
	public SignChangeListener(PlaceholderInjector i) {
		Bukkit.getPluginManager().registerEvents(this, i);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		
		if (e.getPlayer().hasPermission("placeholderinjector.signs.bypass")) {
			return;
		}
		
		Matcher m;
		
		for (int i = 0 ; i < 3 ; i++) {
			
			String line = e.getLine(i);
			
			m = PlaceholderAPI.getPlaceholderPattern().matcher(line);
			
			if (m.find()) {
				String replace = m.group();
				line = line.replace(replace, "");
				e.setLine(i, line);
			}
		}		
		
	}

}
