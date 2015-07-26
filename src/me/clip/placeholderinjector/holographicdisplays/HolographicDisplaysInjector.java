package me.clip.placeholderinjector.holographicdisplays;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderinjector.PlaceholderInjector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.filoghost.holographicdisplays.HolographicDisplays;
import com.gmail.filoghost.holographicdisplays.bridge.protocollib.WrapperPlayServerEntityMetadata;
import com.gmail.filoghost.holographicdisplays.bridge.protocollib.WrapperPlayServerSpawnEntityLiving;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSEntityBase;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.gmail.filoghost.holographicdisplays.util.VersionUtils;

/**
@author filoghost, extended_clip, GGhost
*/
public class HolographicDisplaysInjector {

	private PlaceholderInjector plugin;
	
	private final int customNameWatcherIndex;
	
	private final int refreshInterval;
	
	private final Set<CraftHologram> holograms = new HashSet<CraftHologram>();
	
	public HolographicDisplaysInjector(PlaceholderInjector i, int interval) {
		plugin = i;
		
		if (HolographicDisplays.is1_8()) {
			customNameWatcherIndex = 2;
		} else {
			customNameWatcherIndex = 10;
		}
		
		if (interval <= 0) {
			refreshInterval = 30;
		} else {
			refreshInterval = interval;
		}
		
		addPacketListener();
		
		plugin.getLogger().info("Intercepting hologram packets for HolographicDisplays placeholders");
		
		startTask();
	}
	
	private void startTask() {
		
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {

			@Override
			public void run() {

				Iterator<CraftHologram> iterator = holograms.iterator();
				
				while (iterator.hasNext()) {
					
					CraftHologram h = iterator.next();
					
					if (h.isDeleted()) {
						iterator.remove();
						return;
					}
					
					h.despawnEntities();
					h.spawnEntities();
				}
			}
			
		}, refreshInterval * 20L, refreshInterval * 20L);
		
	}
	
	private void addPacketListener() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.ENTITY_METADATA) {
			  
			@Override
			public void onPacketSending(PacketEvent event) {
				
				if (event.isCancelled()) {
					return;
				}
				
				if (event.getPlayer() == null) {
					return;
				}
				
				PacketContainer packet = event.getPacket();

				if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

					WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet);
					
					Entity entity = spawnEntityPacket.getEntity(event);
					
					if (entity == null || !isHologramType(entity.getType())) {
						return;
					}
					
					CraftHologram hologram = getHologram(entity);
					
					if (hologram == null) {
						return;
					}
					
					Player player = event.getPlayer();
					
					WrappedDataWatcher dataWatcher = spawnEntityPacket.getMetadata();
					
					String customName = dataWatcher.getString(2);
					
					if (customName == null) {
						return;
					}
					
					if (PlaceholderAPI.getPlaceholderPattern().matcher(customName).find()) {
						
						if (!holograms.contains(hologram)) {
							holograms.add(hologram);
						}
						
						WrappedDataWatcher dataWatcherClone = dataWatcher.deepClone();
						dataWatcherClone.setObject(customNameWatcherIndex, PlaceholderAPI.setPlaceholders(player, customName));
						spawnEntityPacket.setMetadata(dataWatcherClone);
						event.setPacket(spawnEntityPacket.getHandle());
						
					}

				} else if (packet.getType() == PacketType.Play.Server.ENTITY_METADATA) {
					
					WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet);
					Entity entity = entityMetadataPacket.getEntity(event);
					
					if (entity == null) {
						return;
					}

					if (entity.getType() != EntityType.HORSE && !VersionUtils.isArmorstand(entity.getType())) {
						return;
					}
					
					CraftHologram hologram = getHologram(entity);
					
					if (hologram == null) {
						return;
					}
					
					Player player = event.getPlayer();

					List<WrappedWatchableObject> dataWatcherValues = entityMetadataPacket.getEntityMetadata();
						
					for (int i = 0; i < dataWatcherValues.size(); i++) {
						
						if (dataWatcherValues.get(i).getIndex() == customNameWatcherIndex && dataWatcherValues.get(i).getValue() != null) {
								
							Object customNameObject = dataWatcherValues.get(i).deepClone().getValue();
							
							if (customNameObject == null || customNameObject instanceof String == false) {
								return;
							}
							
							String customName = (String) customNameObject;
								
							if (PlaceholderAPI.getPlaceholderPattern().matcher(customName).find()) {
								
								if (!holograms.contains(hologram)) {
									holograms.add(hologram);
								}
								
								entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet.deepClone());
								List<WrappedWatchableObject> clonedList = entityMetadataPacket.getEntityMetadata();
								WrappedWatchableObject clonedElement = clonedList.get(i);
								clonedElement.setValue(PlaceholderAPI.setPlaceholders(player, customName));
								entityMetadataPacket.setEntityMetadata(clonedList);
								event.setPacket(entityMetadataPacket.getHandle());
								return;
							}
						}
					}
				}
			}
		});
	}
	
	private static boolean isHologramType(EntityType type) {
		return type == EntityType.HORSE || type == EntityType.WITHER_SKULL || type == EntityType.DROPPED_ITEM || type == EntityType.SLIME || VersionUtils.isArmorstand(type); // To maintain backwards compatibility
	}
	
	private static CraftHologram getHologram(Entity bukkitEntity) {
		NMSEntityBase entity = HolographicDisplays.getNMSManager().getNMSEntityBase(bukkitEntity);
		if (entity != null) {
			return entity.getHologramLine().getParent();
		}
		
		return null;
	}
	
}
