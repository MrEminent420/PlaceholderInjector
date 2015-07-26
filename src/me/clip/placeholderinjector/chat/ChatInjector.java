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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ChatInjector implements Listener {
	
	private PlaceholderInjector plugin;
	
	public ChatInjector(PlaceholderInjector instance) {
		
		plugin = instance;
		
		addPacketListener();
		
		plugin.getLogger().info("Intercepting message packets for placeholders");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		
		Player p = e.getPlayer();
		
		String message = e.getMessage();
		
		Matcher matcher = PlaceholderAPI.getPlaceholderPattern().matcher(message);
		
		if (p.hasPermission("placeholderinjector.chat.bypass")) {
			
			if (matcher.find()) {
				
				message = PlaceholderAPI.setPlaceholders(p, message);
				
				e.setMessage(message);
			}
			
			return;
		}	
		
		while (matcher.find()) {
			message = matcher.replaceAll("");
		}
		
		if (message.isEmpty()) {
			e.setCancelled(true);
			return;
		}
		
		e.setMessage(message);
	}
	
	private void addPacketListener() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT) {

			@Override
			public void onPacketSending(PacketEvent e) {
				
				if (e.getPlayer() == null) {
					return;
				}
				
				StructureModifier<WrappedChatComponent> chat = e.getPacket().getChatComponents();
				
				String msg = chat.read(0).getJson();
				
				if (msg == null || msg.isEmpty()) {
					return;
				}
					
				if (!PlaceholderAPI.getPlaceholderPattern().matcher(msg).find()) {
					return;
				}
				
				msg = PlaceholderAPI.setPlaceholders(e.getPlayer(), msg);
				
				chat.write(0, WrappedChatComponent.fromJson(msg));
			}
			
		});
	}
}
