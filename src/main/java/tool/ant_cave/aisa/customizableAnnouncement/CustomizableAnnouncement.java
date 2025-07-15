// ... existing code ...
package tool.ant_cave.aisa.customizableAnnouncement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class CustomizableAnnouncement extends JavaPlugin {

    private File messagesFile;
    private YamlConfiguration messagesConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("CustomizableAnnouncement enabled");

        // Initialize configuration files
        saveDefaultConfig();
        messagesFile = new File(getDataFolder(), "config.yml");
        if (!messagesFile.exists()) {
            saveResource("config.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register reload command
        getCommand("reloadAnnouncement").setExecutor(new ReloadCommand(this));
    }

    // Method to reload messages from the configuration file
    public void reloadMessages() {
        messagesFile = new File(getDataFolder(), "config.yml");
        if (messagesFile.exists()) {
            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        } else {
            saveResource("config.yml", false);
            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        }
    }

    // Method to get a message from the messages configuration
    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(path, ""));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("CustomizableAnnouncement disabled");
    }

    // Listener class for player join events
    public static class PlayerJoinListener implements Listener {

        private final CustomizableAnnouncement plugin;

        public PlayerJoinListener(CustomizableAnnouncement plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();

            // 获取基础消息
            String everyoneMessage = plugin.getMessage("welcome.everyone");
            String selfMessage = plugin.getMessage("welcome.self");

            // 替换变量 {server} 和 {player}
            String serverName = plugin.getConfig().getString("serverName", "Server");
            String playerName = player.getName();

            // 发送欢迎消息给所有玩家
            if (!everyoneMessage.isEmpty()) {
                String formattedEveryoneMessage = everyoneMessage
                        .replace("{server}", serverName)
                        .replace("{player}", playerName);
                Bukkit.broadcastMessage(formattedEveryoneMessage);
            }

            // 发送欢迎消息给加入的玩家
            if (!selfMessage.isEmpty()) {
                String formattedSelfMessage = selfMessage
                        .replace("{server}", serverName)
                        .replace("{player}", playerName);
                player.sendMessage(formattedSelfMessage);
            }
        }
// ... existing code ...

    }

    // Command executor for reload command
    public static class ReloadCommand implements CommandExecutor {

        private final CustomizableAnnouncement plugin;

        public ReloadCommand(CustomizableAnnouncement plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            if (sender.hasPermission("customizableAnnouncement.reload")) {
                plugin.reloadMessages();
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return false;
            }
        }
    }
}
