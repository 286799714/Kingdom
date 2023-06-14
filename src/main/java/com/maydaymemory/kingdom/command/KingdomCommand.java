package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.PluginKingdom;
import com.maydaymemory.kingdom.core.command.CommandHandler;
import com.maydaymemory.kingdom.core.command.SubCommand;
import org.bukkit.command.CommandSender;

public class KingdomCommand extends BaseCommand{

    public KingdomCommand() {
        super("kingdom");
    }

    @CommandHandler(
            name = "reload",
            permission = "kingdom.admin",
            description = "cmd-inf.reload.description")
    public SubCommand reload = new SubCommand() {
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            PluginKingdom.getInstance().loadPlugin();
            sender.sendMessage(processMessage("cmd-inf.reload.success"));
        }
    };
}
