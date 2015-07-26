package me.clip.placeholderinjector.title;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

import com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
@author extended_clip
*/
public class TitlePacketListener extends PacketAdapter {
	
	public TitlePacketListener(PlaceholderInjector i){
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.TITLE);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		WrapperPlayServerTitle packet = new WrapperPlayServerTitle(e.getPacket());
		
		if (e.getPlayer() == null) {
			return;
		}
		
		if (packet.getTitle() == null) {
			return;
		}
		
		WrappedChatComponent wcp = packet.getTitle();
		
		String json = wcp.getJson();
		
		if (PlaceholderAPI.getPlaceholderPattern().matcher(json).find()) {
			
			json = PlaceholderAPI.setPlaceholders(e.getPlayer(), json);
		}
		
		wcp.setJson(json);
		
		packet.setTitle(wcp);
	}
}
