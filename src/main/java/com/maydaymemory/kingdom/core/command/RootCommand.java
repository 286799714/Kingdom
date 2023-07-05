package com.maydaymemory.kingdom.core.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RootCommand extends Command {
    protected Map<String, SubCommand> subCommandMap = new HashMap<>();
    protected Map<String, ParameterMeta[]> parameterMetaMap = new HashMap<>();
    protected Map<String, CommandHandler> annoMap = new HashMap<>();

    protected int helpMessagePerPage = 7;

    public RootCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public RootCommand(String name) {
        super(name);
    }

    public void init() {
        Class<? extends RootCommand> c = this.getClass();
        Field[] fields =  c.getDeclaredFields();
        for(Field field : fields){
            if(!field.isAnnotationPresent(CommandHandler.class)) continue;
            if(!SubCommand.class.isAssignableFrom(field.getType())) continue;
            CommandHandler anno = field.getAnnotation(CommandHandler.class);
            field.setAccessible(true);
            SubCommand subCommand;
            try {subCommand = (SubCommand) field.get(this);} catch (IllegalAccessException e) {throw new RuntimeException(e);}
            if(subCommand == null) return;
            subCommandMap.put(anno.name(), subCommand);
            parameterMetaMap.put(anno.name(), subCommand.getParameterMeta());
            annoMap.put(anno.name(), anno);
        }
    }

    @Override
    public @Nonnull List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull String label, @Nonnull String[] args){
        if(subCommandMap.isEmpty()) init();
        if(args.length == 1){
            return subCommandMap.keySet().stream()
                    .filter(s -> s.contains(args[0]))
                    .sorted(new SpellComparator())
                    .collect(Collectors.toList());
        }else {
            String subCommandName = args[0];
            SubCommand subCommand = subCommandMap.get(subCommandName);
            if(subCommand == null){
                return new ArrayList<>();
            }
            if(args.length > 2) {
                ParameterTranslator translator = subCommand.getTranslator(getProviderNamespace());
                CommandMeta meta = new CommandMeta(this.getName(), subCommandName, null, null, parameterMetaMap.get(subCommandName));
                if(!translator.translate(sender, Arrays.copyOfRange(args, 1, args.length-1), this, subCommand, meta, args.length - 3))
                    return new ArrayList<>();
                subCommand.injectParameter(translator);
            }
            return subCommand.tabComplete(sender, args.length-2).stream()
                    .filter(s -> s.contains(args[args.length-1]))
                    .sorted(new SpellComparator())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean execute(@Nonnull CommandSender commandSender, @Nonnull String label, @Nonnull String[] args) {
        if(subCommandMap.isEmpty()) init();
        if(args.length == 0){
            sendHelpMessage(commandSender, 1);
            return true;
        }
        if(args[0].equals("help")){
            if(args.length == 1) sendHelpMessage(commandSender, 1);
            else {
                try {
                    sendHelpMessage(commandSender, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sendHelpMessage(commandSender, 1);
                }
            }
            return true;
        }
        String subCommandName = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        SubCommand subCommand = subCommandMap.get(subCommandName);
        CommandHandler anno = annoMap.get(subCommandName);
        if(subCommand == null){
            executionRejectCallback(RejectCause.COMMAND_NOT_FOUND, commandSender, args, null);
            return true;
        }
        ParameterTranslator translator = subCommand.getTranslator(getProviderNamespace());
        if(anno.permission().length() == 0 || commandSender.hasPermission(anno.permission())){
            if(anno.playerOnly() && !(commandSender instanceof Player)){
                executionRejectCallback(RejectCause.PLAYER_ONLY, commandSender, args, subCommand);
                return true;
            }
            if(translator.getParameterLength() > subArgs.length){
                executionRejectCallback(RejectCause.MISSING_ARGUMENT, commandSender, args, null);
                return true;
            }
            CommandMeta meta = new CommandMeta(this.getName(), subCommandName, anno.permission(), anno.description(), parameterMetaMap.get(subCommandName));
            if(!translator.translate(commandSender, subArgs, this, subCommand, meta)){
                return true;
            }
            subCommand.injectParameter(translator);
            subCommand.execute(commandSender, label, subArgs);
        }else {
            executionRejectCallback(RejectCause.NO_PERMISSION, commandSender, args, null);
        }
        return true;
    }

    public void sendHelpMessage(CommandSender sender, int page){
        List<String> subcommands = subCommandMap.keySet().stream()
                .sorted(new SpellComparator())
                .collect(Collectors.toList());
        int total = subcommands.size() / helpMessagePerPage + (subcommands.size() % helpMessagePerPage > 0 ? 1 : 0);
        if(page > total) page = total;
        sender.sendMessage(ChatColor.UNDERLINE + "   " + getName() + ChatColor.RESET+ " (" + ChatColor.GOLD + page + ChatColor.WHITE + "/" + total + ")");
        for(int i = (page - 1) * helpMessagePerPage; i < page * helpMessagePerPage && i < subcommands.size(); i++){
            TextComponent[] components = getHelpTextComponent(subcommands.get(i));
            if(components != null) sender.spigot().sendMessage(components);
        }
        sender.sendMessage("/" + getName()+ ChatColor.AQUA + " help" + ChatColor.RESET + " [" + ChatColor.AQUA + "page" + ChatColor.RESET +"]");
    }

    /**Return the help information of subcommand in the form of TextComponent.
     * The Component of the subcommand has the ClickEvent that suggest command,
     * And the Component of the parameters has HoverEvent that show information.
     * @param name the name of subcommand.*/
    public TextComponent[] getHelpTextComponent(String name){
        ParameterMeta[] meta = parameterMetaMap.get(name);
        SubCommand command = subCommandMap.get(name);
        CommandHandler anno = annoMap.get(name);
        if(meta == null) return null;
        if(command == null) return null;
        List<TextComponent> components = new ArrayList<>();
        components.add(new TextComponent("/" + getName() + " "));
        TextComponent sub = new TextComponent(ChatColor.AQUA + name);
        sub.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + getName() + " " + name + " "));
        components.add(sub);
        for(ParameterMeta parameterMeta : meta){
            String parameterName = processMessage(parameterMeta.getName());
            String hover = processMessage(parameterMeta.getHover());
            TextComponent ptc = new TextComponent(ChatColor.RESET + " [" + ChatColor.AQUA + parameterName + ChatColor.RESET +"]");
            ptc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
            components.add(ptc);
        }
        if(anno.description().length() != 0) components.add(new TextComponent(" -- " + processMessage(anno.description()) ));
        return components.toArray(new TextComponent[0]);
    }

    /**Return the usage information of subcommand in the form of string.
     * @param name the name of subcommand.*/
    public String getUsage(String name){
        ParameterMeta[] meta = parameterMetaMap.get(name);
        SubCommand command = subCommandMap.get(name);
        if(meta == null) return null;
        if(command == null) return null;
        StringBuilder builder = new StringBuilder();
        builder.append('/').append(getName()).append(' ').append(name);
        for(ParameterMeta parameterMeta : meta){
            String parameterName = processMessage(parameterMeta.getName());
            builder.append(" [").append(parameterName).append(']');
        }
        return builder.toString();
    }

    protected abstract void executionRejectCallback(@Nonnull RejectCause cause, @Nonnull CommandSender commandSender, @Nonnull String[] args, SubCommand subCommand);

    protected abstract String getProviderNamespace();

    protected abstract String processMessage(String key);

    private static class SpellComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            try {
                String s1 = new String(o1.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                String s2 = new String(o2.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                return s1.compareTo(s2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public enum RejectCause{
        COMMAND_NOT_FOUND,
        PLAYER_ONLY,
        MISSING_ARGUMENT,
        NO_PERMISSION,
    }
}
