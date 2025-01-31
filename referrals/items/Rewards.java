package theeluke.referrals.items;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import theeluke.referrals.Main;

public class Rewards {

    Main plugin;
    ItemStack item;
    ItemMeta meta;

    public Rewards(Main plugin) {
        this.plugin = plugin;

        item = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        meta = item.getItemMeta();

        //setting meta
        meta.setDisplayName(plugin.getConfig().getString("crate-key-name"));
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);


    }

    public ItemStack getItem() {
        return item;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public void successReferral(Player p) {

        //successful referral | Player referred server to Target,
        //send Player message: Referral rewards added! Thank you for playing on our server!
        //firework BANG on the player
        //play sound
        //$1000
        //1 crate key
        p.sendMessage(plugin.getPrefix() + plugin.getConfig().getString("successful-referral"));

        Firework firework = p.getWorld().spawn(p.getLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).withColor(Color.YELLOW).with(FireworkEffect.Type.STAR).withFlicker().build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 5, 5);


        Economy economy = Main.getEconomy();
        EconomyResponse response = economy.depositPlayer(p, 1000.0);
        if(response.transactionSuccess()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getConfig().getString("money-action-bar-message").replace("%money%", plugin.getConfig().getDouble("reward-money") + "")));
        }

        int emptySlot = p.getInventory().firstEmpty();
        if(emptySlot == -1) {
            p.getWorld().dropItem(p.getLocation(), getItem());
            p.sendMessage(plugin.getPrefix() + plugin.getConfig().getString("full-inv"));
        }else {
            p.getInventory().setItem(emptySlot, getItem());
        }

    }
}
