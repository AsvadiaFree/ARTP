package fr.asvadia.artp;

import fr.asvadia.artp.utils.commands.RTPCommand;
import fr.asvadia.artp.utils.file.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        try {
            FileManager.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getCommand("rtp").setExecutor(new RTPCommand());
    }

    public static Main getInstance() {
        return instance;
    }
}
