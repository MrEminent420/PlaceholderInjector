package me.clip.placeholderinjector.tab;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
@author extended_clip
*/
public class TabPacketListener extends PacketAdapter {
	
	public TabPacketListener(PlaceholderInjector i){
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		WrapperPlayServerPlayerListHeaderFooter  packet = new WrapperPlayServerPlayerListHeaderFooter(e.getPacket());
		
		if (e.getPlayer() == null) {
			return;
		}
		
		if (packet.getHeader() != null) {
			
			WrappedChatComponent wcpHeader = packet.getHeader();
			
			String headerJson = wcpHeader.getJson();
			
			if (PlaceholderAPI.getPlaceholderPattern().matcher(headerJson).find()) {
				
				headerJson = PlaceholderAPI.setPlaceholders(e.getPlayer(), headerJson);
				
				wcpHeader.setJson(headerJson);
				
				packet.setHeader(wcpHeader);
			}
		}
		
		if (packet.getFooter() != null) {
			
			WrappedChatComponent wcpFooter = packet.getFooter();
			
			String footerJson = wcpFooter.getJson();
			
			if (PlaceholderAPI.getPlaceholderPattern().matcher(footerJson).find()) {
				
				footerJson = PlaceholderAPI.setPlaceholders(e.getPlayer(), footerJson);
				
				wcpFooter.setJson(footerJson);
				
				packet.setFooter(wcpFooter);
			}
		}
	}
}
