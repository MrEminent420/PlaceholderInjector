package me.clip.placeholderinjector.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
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
public class SpigotChatPacketListener extends PacketAdapter {

	public SpigotChatPacketListener(PlaceholderInjector i) {
		super(i, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}
	
	@Override
	public void onPacketSending(PacketEvent e) {
		
		if (e.getPlayer() == null) {
			return;
		}
		
		StructureModifier<WrappedChatComponent> chat = e.getPacket().getChatComponents();
		
		WrappedChatComponent c = chat.read(0);
		
		if (c == null) {
			
			StructureModifier<BaseComponent[]> modifier = e.getPacket().getSpecificModifier(BaseComponent[].class);
			
			BaseComponent[] components = modifier.readSafely(0);
			
			if (components == null) {
				return;
			}
			
			String msg = ComponentSerializer.toString(components);
			
			if (msg == null) {
				return;
			}
			
			if (!PlaceholderAPI.getPlaceholderPattern().matcher(msg).find()) {
				return;
			}
			
			msg = PlaceholderAPI.setPlaceholders(e.getPlayer(), msg);
			
			modifier.write(0, ComponentSerializer.parse(msg));
			
			return;	
		}
			
		String msg = c.getJson();
			
		if (msg == null) {
			return;
		}
			
		if (!PlaceholderAPI.getPlaceholderPattern().matcher(msg).find()) {
			return;
		}
		
		msg = PlaceholderAPI.setPlaceholders(e.getPlayer(), msg);
		
		chat.write(0, WrappedChatComponent.fromJson(msg));
	}
}
