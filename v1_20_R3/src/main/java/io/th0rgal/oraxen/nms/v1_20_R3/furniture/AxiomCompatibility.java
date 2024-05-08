package io.th0rgal.oraxen.nms.v1_20_R3.furniture;

import com.moulberry.axiom.event.AxiomManipulateEntityEvent;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.IFurniturePacketManager;
import io.th0rgal.oraxen.utils.logs.Logs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AxiomCompatibility implements Listener {

    public AxiomCompatibility() {
        Logs.logInfo("Registering Axiom-Compatibility for furniture...");
    }

    @EventHandler
    public void onAxiomManipFurniture(AxiomManipulateEntityEvent event) {
        Entity baseEntity = event.getEntity();
        FurnitureMechanic mechanic = OraxenFurniture.getFurnitureMechanic(baseEntity);
        IFurniturePacketManager packetManager = FurnitureFactory.get().packetManager();
        if (baseEntity == null || mechanic == null) return;

        packetManager.removeFurnitureEntityPacket(baseEntity, mechanic);
        packetManager.removeInteractionHitboxPacket(baseEntity, mechanic);
        packetManager.removeBarrierHitboxPacket(baseEntity, mechanic);

        Bukkit.getScheduler().runTaskLater(OraxenPlugin.get(), () -> {
            for (Player player : baseEntity.getWorld().getNearbyPlayers(baseEntity.getLocation(), FurnitureFactory.get().simulationRadius)) {
                packetManager.sendFurnitureEntityPacket(baseEntity, mechanic, player);
                packetManager.sendInteractionEntityPacket(baseEntity, mechanic, player);
                packetManager.sendBarrierHitboxPacket(baseEntity, mechanic, player);
            }
        }, 2L);
    }
}