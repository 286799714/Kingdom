package com.maydaymemory.kingdom.core.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

public class ConfigUtil {
    private static final Map<String, Map<String, YamlConfiguration> > map = new HashMap<>();

    public static void saveDefault(Plugin plugin, String resourcePath){
        File file = new File(plugin.getDataFolder(), resourcePath);
        if(!file.exists()){
            plugin.saveResource(resourcePath,false);
        }else {
            //Checking if the config file need to update default value. If so, save the default value.
            try {
                InputStream input = plugin.getResource(resourcePath);
                if (input == null) return;
                FileConfiguration resourceConfig = new YamlConfiguration();
                FileConfiguration fileConfig = new YamlConfiguration();
                resourceConfig.load(new InputStreamReader(input));
                fileConfig.load(file);
                for (Map.Entry<String, Object> mapEntry : resourceConfig.getValues(true).entrySet()) {
                    String path = mapEntry.getKey();
                    if (!fileConfig.contains(path)) {
                        fileConfig.set(path, mapEntry.getValue());
                        List<String> comments = resourceConfig.getComments(path);
                        List<String> inlineComments = resourceConfig.getInlineComments(path);
                        fileConfig.setComments(path, comments);
                        fileConfig.setInlineComments(path, inlineComments);
                    }
                }
                fileConfig.save(file);
            }catch (IOException | InvalidConfigurationException e){
                throw new RuntimeException(e);
            }
        }
    }

    private static Configuration loadConfiguration(Plugin plugin, String path){
        File file = new File(plugin.getDataFolder(), path);
        if(!file.exists()){
            return null;
        }
        FileConfiguration configuration = map.computeIfAbsent(plugin.getName(), k -> new HashMap<>())
                .computeIfAbsent(path, k -> new YamlConfiguration());
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return configuration;
    }
    public static void load(Plugin plugin) {
        load(plugin, null);
    }

    public static void load(Plugin plugin, String bypass) {
        //Inject into the static field in all class annotated by ConfigInject in the plugin.
        URL url = plugin.getClass().getResource("");
        if(url == null) return;
        String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
        ClassLoader classLoader = ConfigUtil.class.getClassLoader();
        try {
            URL javaURL = new URL(jarPath);
            JarURLConnection urlConnection = (JarURLConnection) javaURL.openConnection();
            Enumeration<JarEntry> entries = urlConnection.getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if(entry.getName().endsWith(".class")) {
                    String cp = entry.getName()
                            .replaceAll("\\\\","/")
                            .replaceAll("/", ".")
                            .replaceAll(".class", "");
                    if(bypass != null && entry.getName().contains(bypass)) continue;
                    Class<?> clazz = classLoader.loadClass(cp);
                    if(clazz == null) continue;
                    for(Field field : clazz.getDeclaredFields()){
                        //Require the field to be like:
                        //@LanguageInject static Configuration config;
                        if(Modifier.isStatic(field.getModifiers())
                                && ConfigurationSection.class.isAssignableFrom(field.getType())
                                && field.isAnnotationPresent(ConfigInject.class)) {
                            ConfigInject anno = field.getAnnotation(ConfigInject.class);
                            Configuration configuration = loadConfiguration(plugin, anno.path());
                            if(configuration == null) continue;
                            field.setAccessible(true);
                            field.set(null, configuration);
                        }
                    }
                }
            }
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException ignore) {}
    }
}
