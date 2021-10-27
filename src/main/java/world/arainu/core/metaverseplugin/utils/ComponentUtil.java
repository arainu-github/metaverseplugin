package world.arainu.core.metaverseplugin.utils;

import net.kyori.adventure.text.TextComponent;

import static java.util.stream.Collectors.joining;

/**
 * 厄介なPaperのComponentAPIを使いやすくする関数を集めたクラス
 *
 * @author kumitatepazuru
 */
public class ComponentUtil {
    /**
     * PaperのTextComponentをStringに変換する関数
     * @param component TextComponent
     * @return 変換後の文字列
     */
    public static String toString(TextComponent component){
        return component.content()+component.children().stream().map((e) -> e instanceof TextComponent ? ((TextComponent) e).content() : "").collect(joining(""));
    }
}
