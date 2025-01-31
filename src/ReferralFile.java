package theeluke.referrals;

import org.bukkit.entity.Player;

import java.io.IOException;

public class ReferralFile extends RefBuilder {

    public ReferralFile(Main main) {
        super(main, "referral.yml");
    }

    public void setNewPlayer(Player player) {
        config.set(getPath(player.getName()) + ".uuid", player.getUniqueId().toString());
        config.set(getPath(player.getName()) + ".numRef", 0);
        config.set(getPath(player.getName()) + ".usedRef", false);
        config.set(getPath(player.getName()) + ".rewards", false);


    }

    public int getRefAmt(Player player) {
        return config.getInt(player.getUniqueId()+ ".numRef");
    }

    public String getPath(String playerName) {
        return "players."+playerName.toLowerCase();
    }



}
