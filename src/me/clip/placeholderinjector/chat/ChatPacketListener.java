package me.clip.placeholderinjector.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
@author extended_clip, GGhost
*/
public class ChatPacketListener extends PacketAdapter {

	public ChatPacketListener(PlaceholderInjector i) {
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
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
}
