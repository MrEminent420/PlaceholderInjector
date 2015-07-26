package me.clip.placeholderinjector.inventory;



import com.comphenix.packetwrapper.WrapperPlayServerOpenWindow;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

/**
@author extended_clip, GGhost
*/
public class InventoryWindowPacketListener extends PacketAdapter {
	
	public InventoryWindowPacketListener(PlaceholderInjector i){
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.OPEN_WINDOW);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		if (e.getPlayer() == null) {
			return;
		}
		
		WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow(e.getPacket());
		
		if (packet.getWindowID() == 0) {
			return;
		}

		WrappedChatComponent wcp = packet.getWindowTitle();
		
		String json = wcp.getJson();
		
		if (PlaceholderAPI.getPlaceholderPattern().matcher(json).find()) {
			
			json = PlaceholderAPI.setPlaceholders(e.getPlayer(), json);
		}
		
		wcp.setJson(json);
		
		packet.setWindowTitle(wcp);
	}
}
