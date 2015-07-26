package me.clip.placeholderinjector.sign;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

import com.comphenix.packetwrapper.WrapperPlayServerUpdateSign;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
@author extended_clip
*/
public class SignPacketListener extends PacketAdapter {
	
	private final Set<Location> signs = new HashSet<Location>();
	
	private PlaceholderInjector plugin;
	
	private int updateInterval;
	
	public SignPacketListener(PlaceholderInjector i, int updateInterval){
		
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.UPDATE_SIGN);
		
		plugin = i;
		
		this.updateInterval = updateInterval;
		
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
		
		if (updateInterval > 0) {
			startTask();
		}
	}
	
	private void startTask() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			
			@Override
			public void run() {

				Iterator<Location> locs = signs.iterator();
				
				while (locs.hasNext()) {
					
					Location l = locs.next();
					
					Block b = l.getWorld().getBlockAt(l);
					
					if (b == null || (!(b.getState() instanceof Sign))) {
						
						locs.remove();
						continue;
					}
					
					Sign s = (Sign) b.getState();
					
					s.update();
				}
			}
			
			
		}, 20L * updateInterval, 20L * updateInterval);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		if (e.getPlayer() == null) {
			return;
		}
		
		WrapperPlayServerUpdateSign  packet = new WrapperPlayServerUpdateSign(e.getPacket());
		
		WrappedChatComponent[] lines = packet.getLines();
		
		Location l = new Location(e.getPlayer().getWorld(), packet.getLocation().getX(), packet.getLocation().getY(), packet.getLocation().getZ());
		
		for (WrappedChatComponent component : lines) {
			
			if (component == null || component.getJson() == null) {
				continue;
			}
			
			String json = component.getJson();
			
			if (PlaceholderAPI.getPlaceholderPattern().matcher(json).find()) {
				
				if (!signs.contains(1) && updateInterval > 0) {
					signs.add(l);
				}
				
				json = PlaceholderAPI.setPlaceholders(e.getPlayer(), json);
				
				component.setJson(json);
			}
		}
		
		packet.setLines(lines);
	}
}
