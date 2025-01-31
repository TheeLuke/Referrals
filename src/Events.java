package theeluke.referrals;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import theeluke.referrals.items.Rewards;

public class Events implements Listener {

    private Main plugin;
    private ReferralFile file;

    public Events(Main plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        file = new ReferralFile(plugin);

        Player p = e.getPlayer();
        String pName = p.getName();
        Rewards rewards = new Rewards(plugin);

        if(!(file.config.isSet(file.getPath(p.getName()) + ".uuid"))) {
            file.setNewPlayer(p);
            file.save();
            p.sendMessage("setting a new player");
        }

        if(file.config.isSet(file.getPath(p.getName()) + ".rewards")) {
            if(file.config.get(file.getPath(p.getName()) + ".rewards").equals(true)) {
                Rewards key = new Rewards(plugin);
                key.successReferral(p);
                file.config.set(file.getPath(p.getName()) + ".rewards", false);
                file.save();
            }
        }
    }
}



