package me.clip.placeholderinjector.chat;

import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

/**
@author extended_clip, GGhost
*/
public class ChatListener implements Listener {
	
	public ChatListener(PlaceholderInjector instance) {
		Bukkit.getPluginManager().registerEvents(this, instance);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		
		Player p = e.getPlayer();
		
		String message = e.getMessage();
		
		Matcher matcher = PlaceholderAPI.getPlaceholderPattern().matcher(message);
		
		if (p.hasPermission("placeholderinjector.chat.bypass")) {
			
			if (matcher.find()) {
				
				message = PlaceholderAPI.setPlaceholders(p, message);
			}

		} else {
			
			while (matcher.find()) {
				message = matcher.replaceAll("");
			}
		}
		
		if (message.isEmpty()) {
			e.setCancelled(true);
			return;
		}
		
		e.setMessage(message);
		
		String format = e.getFormat();
		
		System.out.println(format);
		
		format = format.replace("{", "%").replace("}", "%");
		
		matcher = PlaceholderAPI.getPlaceholderPattern().matcher(format);
		
		if (matcher.find()) {
			format = PlaceholderAPI.setPlaceholders(p, format);
		}		
		
		e.setFormat(format);
		
	}
}
