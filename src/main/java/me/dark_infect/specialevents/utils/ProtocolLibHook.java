package me.dark_infect.specialevents.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import me.dark_infect.specialevents.SpecialEvents;

public final class ProtocolLibHook {
    public static void register(){
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(SpecialEvents.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Server.CHAT){

        });
    }
}
