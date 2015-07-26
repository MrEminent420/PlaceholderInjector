package me.clip.placeholderinjector.sign;

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
	
	public SignPacketListener(PlaceholderInjector i){
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.UPDATE_SIGN);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		if (e.getPlayer() == null) {
			return;
		}
		
		WrapperPlayServerUpdateSign  packet = new WrapperPlayServerUpdateSign(e.getPacket());
		
		WrappedChatComponent[] lines = packet.getLines();
		
		for (WrappedChatComponent component : lines) {
			
			String json = component.getJson();
			
			if (PlaceholderAPI.getPlaceholderPattern().matcher(json).find()) {
				json = PlaceholderAPI.setPlaceholders(e.getPlayer(), json);
			}
			
			component.setJson(json);
		}
		
		packet.setLines(lines);
	}
}
