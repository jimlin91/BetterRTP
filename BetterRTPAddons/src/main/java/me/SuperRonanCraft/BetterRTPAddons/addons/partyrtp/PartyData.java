package me.SuperRonanCraft.BetterRTPAddons.addons.partyrtp;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportPostEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyData {

    @Getter private final Player leader;
    private HashMap<Player, Boolean> members = new HashMap<>();

    public PartyData(Player leader) {
        this.leader = leader;
    }

    public boolean add(Player p) {
        if (!members.containsKey(p)) {
            members.put(p, false);
            return true;
        }
        return false;
    }

    public boolean remove(Player p) {
        return members.remove(p) != null;
    }

    public boolean contains(Player p) {
        return leader.equals(p) || members.containsKey(p);
    }

    public boolean isLeader(Player p) {
        return this.leader.equals(p);
    }

    public boolean allReady() {
        return !members.containsValue(false);
    }

    public void readyUp(Player p) {
        if (members.containsKey(p))
            members.put(p, true);
    }

    public void clear() {
        members.replaceAll((p, v) -> false);
    }

    public String getNotReady() {
        List<Player> notReady = new ArrayList<>();
        members.forEach((p, ready) -> {
            if (!ready) notReady.add(p);
        });
        StringBuilder notReady_str = new StringBuilder("[");
        notReady.forEach(p -> notReady_str.append(p.getName()));
        notReady_str.append("]");
        return notReady_str.toString();
    }

    public void tpAll(RTP_TeleportPostEvent e) {
        members.forEach((p, ready) -> {
            Location loc = e.getLocation();
            //Async tp players
            PaperLib.teleportAsync(p, loc, PlayerTeleportEvent.TeleportCause.PLUGIN).thenRun(() ->
                BetterRTP.getInstance().getText().getSuccessBypass(p,
                    String.valueOf(loc.getBlockX()),
                    String.valueOf(loc.getBlockY()),
                    String.valueOf(loc.getBlockZ()),
                    loc.getWorld().getName(),
                    1));
        });
    }
}
