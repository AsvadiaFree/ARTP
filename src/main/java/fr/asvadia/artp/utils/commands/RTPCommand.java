package fr.asvadia.artp.utils.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import fr.asvadia.artp.utils.file.FileManager;
import fr.asvadia.artp.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class RTPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender.hasPermission("artp") || sender instanceof ConsoleCommandSender) 
                && args.length == 1)  {
            YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(lang.getString("PlayerNotFound"));
                return false;
            }

            randomTP(target);
            sender.sendMessage(lang.getString("PlayerTeleport").replaceAll("%player%", target.getName()));
        }
        return false;
    }

    public static void randomTP(Player player) {
        YamlConfiguration config = FileManager.getValues().get(Files.Config);
        YamlConfiguration lang = FileManager.getValues().get(Files.Lang);
        Random rand = new Random();
        int minRadius = config.getInt("minRadius");
        int radius = config.getInt("maxRadius") - minRadius;
        int x;
        int z;
        Material material;
        Location location = player.getLocation();

        do {
            x = rand.nextInt(radius) + minRadius;
            z = rand.nextInt(radius) + minRadius;
            if (rand.nextBoolean())
                x *= -1;
            if (rand.nextBoolean())
                z *= -1;

            location.setX(x);
            location.setY(player.getWorld().getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES));
            location.setZ(z);

            material = location.add(0, -1, 0).getBlock().getType();
        } while (!material.equals(Material.LAVA)
                && !material.equals(Material.WATER)
                && !Board.getInstance().getFactionAt(new FLocation(location)).isWilderness());

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1, 255));
        player.teleport(location.add(0.5, 2, 0.5), PlayerTeleportEvent.TeleportCause.COMMAND);
        player.sendMessage(lang.getString("SuccessfulRTP"));
    }
}
