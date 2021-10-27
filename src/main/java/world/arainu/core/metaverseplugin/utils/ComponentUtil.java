package world.arainu.core.metaverseplugin.utils;

import net.kyori.adventure.text.TextComponent;

import static java.util.stream.Collectors.joining;

public class ComponentUtil {
    public static String toString(TextComponent component){
        return component.content()+component.children().stream().map((e) -> e instanceof TextComponent ? ((TextComponent) e).content() : "").collect(joining(""));
    }
}
