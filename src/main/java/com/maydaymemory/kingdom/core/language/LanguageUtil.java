package com.maydaymemory.kingdom.core.language;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;

public class LanguageUtil {
    private static final Map<String, YamlConfiguration> map = new HashMap<>();

    public static void saveDefault(Plugin plugin){
        URL url = plugin.getClass().getClassLoader().getResource("lang/");
        if(url == null) return;
        String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
        try {
            URL jarURL = new URL(jarPath);
            JarURLConnection connection = (JarURLConnection) jarURL.openConnection();
            Enumeration<JarEntry> jarEntries = connection.getJarFile().entries();
            while (jarEntries.hasMoreElements()){
                JarEntry entry = jarEntries.nextElement();
                String name = entry.getName();
                if(name.startsWith("lang/") && !entry.isDirectory()){
                    File configFile = new File(plugin.getDataFolder(), name);
                    if(configFile.exists() && configFile.isFile()) {
                        //Checking if the language file need to update default value. If so, save the default value.
                        InputStream input = plugin.getResource(name);
                        if (input == null) continue;
                        FileConfiguration resourceConfig = new YamlConfiguration();
                        FileConfiguration fileConfig = new YamlConfiguration();
                        resourceConfig.load(new InputStreamReader(input, StandardCharsets.UTF_8));
                        fileConfig.load(configFile);
                        for(Map.Entry<String, Object> mapEntry : resourceConfig.getValues(true).entrySet()){
                            String path = mapEntry.getKey();
                            if(!fileConfig.contains(path)){
                                fileConfig.set(path, mapEntry.getValue());
                                List<String> comments = resourceConfig.getComments(path);
                                List<String> inlineComments = resourceConfig.getInlineComments(path);
                                fileConfig.setComments(path, comments);
                                fileConfig.setInlineComments(path, inlineComments);
                            }
                        }
                        fileConfig.save(configFile);
                    }else {
                        plugin.saveResource(name, false);
                    }
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean load(Plugin plugin, String language, String... bypass){
        Locale locale;
        if(language == null) locale = Locale.getDefault();
        else locale = new Locale(language);
        return load(plugin, locale, bypass);
    }

    public static boolean load(Plugin plugin, Locale locale, String... bypass) {
        String path = "lang/" + locale.toString() + ".yml";
        //load language configuration from file
        File file = new File(plugin.getDataFolder(), path);
        if(!file.exists()){
            return false;
        }
        FileConfiguration configuration = map.computeIfAbsent(plugin.getName(), k -> new YamlConfiguration());
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        //Inject into the static field in all class annotated by LanguageInject in the plugin.
        URL url = plugin.getClass().getResource("");
        if(url == null) return false;
        String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
        ClassLoader classLoader = LanguageUtil.class.getClassLoader();
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
                    if(Arrays.stream(bypass).noneMatch(cp::contains)) continue;
                    Class<?> clazz = classLoader.loadClass(cp);
                    if(clazz == null) continue;
                    for(Field field : clazz.getDeclaredFields()){
                        //Require the field to be like:
                        //@LanguageInject static Configuration config;
                        if(Modifier.isStatic(field.getModifiers())
                                && ConfigurationSection.class.isAssignableFrom(field.getType())
                                && field.isAnnotationPresent(LanguageInject.class)) {
                            field.setAccessible(true);
                            field.set(null, configuration);
                        }
                    }
                }
            }
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException ignore) {}
        return true;
    }
}
