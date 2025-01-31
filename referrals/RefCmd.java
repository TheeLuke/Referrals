package theeluke.referrals;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import theeluke.referrals.items.Rewards;

import java.util.*;

public class RefCmd implements CommandExecutor {

    Main main;
    List<String> timer;

    public RefCmd(Main main) {
        this.main = main;
        timer = new ArrayList<>();

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Player is not a sender check
        if(!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        Economy economy = Main.getEconomy();
        Rewards key = new Rewards(main);
        HashMap<String, Integer> leaderboard = new HashMap<>();
        List<String> lbNames = new ArrayList<>();
        int secondsPlayed = p.getStatistic(Statistic.PLAY_ONE_MINUTE)/20;
        ReferralFile file = new ReferralFile(main);

        //Player already used referral check
        if(file.config.getBoolean(p.getUniqueId() + ".usedRef")) {
            //send message telling player they cannot use more than 1 referral
            return true;
        }


        if(args.length < 1) {
            p.sendMessage(ChatColor.RED + p.getName() + ChatColor.GRAY + ":");
            p.sendMessage(ChatColor.GRAY + "Playtime: " + ChatColor.GOLD + (secondsPlayed/60) + " minutes");
            p.sendMessage(ChatColor.GRAY + "Used referral: " + ChatColor.GOLD + file.config.get(file.getPath(p.getName()) + ".usedRef"));
            p.sendMessage(ChatColor.GRAY + "Referral Points: " + ChatColor.GOLD + file.config.get(file.getPath(p.getName()) + ".numRef"));

            //send status message
            return true;
        }else if(args.length > 1) {
            p.sendMessage(main.prefix + main.getConfig().getString("bad-args"));
            //too many arguments!
            return true;
        }

        if(args[0].equalsIgnoreCase("info")) {
            p.sendMessage("§ePlugin made by: §ctheeluke");
            p.sendMessage("§eVersion 1.0 - 6/9/2023");
            p.sendMessage("§eQuestions: §cluke#6048");
            p.sendMessage("");
            p.sendMessage("§8-§7-§8-§7-§8-§7-§8-§7-§8-§7-§8-§7-§8-§7-§8-§7-§8-§7-");
            p.sendMessage("");
            p.sendMessage("§bhttps://www.twitter.com/theeluke");
            p.sendMessage("§4https://www.youtube.com/theeluke");
            return true;
        }



        if(args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb")) {
            if(file.config.getConfigurationSection("players") == null) {
                p.sendMessage(ChatColor.RED + "FATAL ERROR - cannot retrieve players from system.");
            }

            int[] values = new int[file.config.getConfigurationSection("players").getKeys(false).toArray().length];

            int i = 0;
            for(String player : file.config.getConfigurationSection("players").getKeys(false)) {
                values[i] = file.config.getInt(file.getPath(player) + ".numRef");
                i++;
            }
            mergeSort(values);

            for(int j = 0; j < values.length; j++) {
                for(String player : file.config.getConfigurationSection("players").getKeys(false)) {
                    if(values[j] == file.config.getInt(file.getPath(player) + ".numRef") && !(leaderboard.containsKey(player))) {
                        leaderboard.put(player, values[j]);
                        lbNames.add(player);

                    }
                }
            }
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 3, 3);
            p.sendMessage(main.getConfig().getString("lb-header"));
            int place = 1;
            if(lbNames.size() < 10) {
                for(int h = lbNames.size()-1; h >= 0; h--) {
                    p.sendMessage(main.getConfig().getString("lb-player").replace("%index%", place + "").replace("%name%", lbNames.get(h)).replace("%value%", values[h] + ""));
                    place++;
                }
            }else {
                for(int k = lbNames.size()-1; place <= 10; k--) {
                    p.sendMessage(main.getConfig().getString("lb-player").replace("%index%", place + "").replace("%name%", lbNames.get(k)).replace("%value%", values[k] + ""));
                    place++;
                }
            }

            lbNames.clear();
            leaderboard.clear();
            //send the leaderboard lol
            return true;
        }

        if(args[0].equalsIgnoreCase(p.getName())) {
            //cant referral self
            p.sendMessage(main.prefix + main.getConfig().getString("self-referral"));
            return true;
        }

        //this cmd is: /ref <name>
        if(file.config.isSet(file.getPath(args[0].toLowerCase()))) {
            if(!file.config.getBoolean(file.getPath(p.getName()) + ".usedRef")) {//Player usedRef = false
                if(file.config.isSet(file.getPath(p.getName()) + ".whoRef")) {
                    if(file.config.getStringList(file.getPath(p.getName()) + ".whoRef").contains(args[0].toUpperCase())) {
                        p.sendMessage(main.prefix + main.getConfig().get("same-player"));
                        return true;
                    }
                }

                if(secondsPlayed < 900) { //15m
                    //Let them know they are eligible and tell them play for 15 minutes to earn rewards
                    main.getConfig().set(p.getName() + ".whoRef", args[0].toUpperCase());
                    p.sendMessage(main.prefix + main.getConfig().getString("eligible-no-playtime").replace("%minutes%", ((secondsPlayed/60)-15) + ""));
                    return true;
                }else if(secondsPlayed > 3600) { //1hr
                    //tell player they can no longer use referrals
                    p.sendMessage(main.prefix + main.getConfig().getString("exceeded-playtime"));
                    return true;
                }else {
                    if(Bukkit.getPlayer(args[0]) == null) {
                        file.config.set(file.getPath(args[0].toLowerCase()) + ".rewards", true);
                    }else if(!(Bukkit.getPlayer(args[0]).getAddress().getAddress().getHostAddress().equals(p.getAddress().getAddress().getHostAddress()))){
                        Player target = Bukkit.getPlayer(args[0]);
                        System.out.println(Bukkit.getPlayer(args[0]).getName() + ": " + Bukkit.getPlayer(args[0]).getAddress().getAddress().getHostAddress());
                        System.out.println(p.getName() + ": " +p.getAddress().getAddress().getHostAddress());
                        key.successReferral(target);
                    }else {
                        p.sendMessage(main.prefix + main.getConfig().getString("failed-ip"));
                        return true;
                    }
                    key.successReferral(p);
                    file.config.set(file.getPath(p.getName()) + ".usedRef", true);
                    int oldAmt = file.config.getInt(file.getPath(p.getName()) + ".numRef");
                    file.config.set(file.getPath(args[0].toLowerCase()) + ".numRef", oldAmt+1);
                    file.save();
                    if(file.config.isSet(file.getPath(args[0].toLowerCase()) + "whoRef")) {
                        List<String> refList = file.config.getStringList(file.getPath(args[0].toLowerCase()) + "whoRef");
                        refList.add(p.getName().toUpperCase());
                        file.config.set(file.getPath(args[0].toLowerCase()) + ".whoRef", refList);
                        file.save();

                    }else {
                        List<String> refList = new ArrayList<>();
                        refList.add(p.getName().toUpperCase());
                        file.config.set(file.getPath(args[0].toLowerCase()) + ".whoRef", refList);
                        file.save();
                    }


                    //give player rewards
                    //give their target rewards
                }
            }else {
                p.sendMessage(main.prefix + main.getConfig().getString("used-referral"));
                //player has already used referral
                return true;
            }
        }else {
            p.sendMessage(main.prefix + main.getConfig().getString("no-player"));
            return true;
        }

        file.save();
        return true;
    }


    private static void mergeSort(int[] values) {
        int length = values.length;

        if(length < 2) {
            return;
        }

        int midIndex = length/2;
        int[] leftHalf = new int[midIndex];
        int[] rightHalf = new int[length-midIndex];

        for(int i = 0; i < midIndex; i++) {
            leftHalf[i] = values[i];
        }

        for(int i = midIndex; i < length; i++) {
            rightHalf[i-midIndex] = values[i];
        }

        mergeSort(leftHalf);
        mergeSort(rightHalf);
        merge(values, leftHalf, rightHalf);

    }

    private static void merge (int[] values, int[] leftHalf, int[] rightHalf) {
        int leftSize = leftHalf.length;
        int rightSize = rightHalf.length;

        int i = 0, j = 0, k = 0;

        while(i < leftSize && j < rightSize) {
            if(leftHalf[i] <= rightHalf[j]) {
                values[k] = leftHalf[i];
                i++;
            }else {
                values[k] = rightHalf[j];
                j++;
            }
            k++;
        }
        while(i < leftSize) {
            values[k] = leftHalf[i];
            i++;
            k++;
        }

        while(j < rightSize) {
            values[k] = rightHalf[j];
            j++;
            k++;
        }

    }




}
