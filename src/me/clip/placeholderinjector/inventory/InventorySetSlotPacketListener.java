package me.clip.placeholderinjector.inventory;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

/**
@author extended_clip, GGhost
*/
public class InventorySetSlotPacketListener extends PacketAdapter {

	public InventorySetSlotPacketListener(PlaceholderInjector i){
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.SET_SLOT);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		if (e.getPlayer() == null) {
			return;
		}
		
		WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(e.getPacket());
		
		if (packet.getWindowId() == 0) {	
			return;
		}

		ItemStack item = packet.getSlotData();
		
		if (item == null || item.getItemMeta() == null || !item.hasItemMeta())  {
			return;
		}
		
		ItemMeta meta = item.getItemMeta();
		
		if (meta.hasDisplayName()) {
			
			if (PlaceholderAPI.getPlaceholderPattern().matcher(meta.getDisplayName()).find()) {
				meta.setDisplayName(PlaceholderAPI.setPlaceholders(e.getPlayer(), meta.getDisplayName()));
			}
		}
		
		if (meta.hasLore()) {
			
			ArrayList<String> updated = new ArrayList<String>();
			
			for (String line : meta.getLore()){
				
				if (PlaceholderAPI.getPlaceholderPattern().matcher(line).find()) {
					
					updated.add(PlaceholderAPI.setPlaceholders(e.getPlayer(), line));
				} else {
					updated.add(line);
				}
				
			}
			
			meta.setLore(updated);
		}
		
		item.setItemMeta(meta);
		
		packet.setSlotData(item);
		}
}
