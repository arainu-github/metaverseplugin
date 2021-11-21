package world.arainu.core.metaverseplugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.store.MarkerStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Commandcreatemunicipal extends CommandPlayerOnlyBase {
    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        final HashMap<Player, ArrayList<Location>> markerData = MarkerStore.getMarkerData();
        if(Objects.equals(args[0], "create")) {
            markerData.put(player, new ArrayList<>(){{add(player.getLocation());}});
            MarkerStore.setMarkerData(markerData);
            player.sendMessage(Component.text("次に区域の頂点を決めましょう。パーティクルで囲まれている場所が自治体区域になります。\n")
                    .append(Component.text("ここをクリック").decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("/__createmunicipal add")))
                    .append(Component.text("すると追加されます。\n"))
                    .append(Component.text("ここをクリック").decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("/__createmunicipal end")))
                    .append(Component.text("すると終了して区域を設定します。"))
            );
        } else if(Objects.equals(args[0],"add")){
            final ArrayList<Location> data = markerData.get(player);
            if(data.get(0).getWorld().getUID() == player.getWorld().getUID()){
                data.add(player.getLocation());
                markerData.replace(player,data);
                MarkerStore.setMarkerData(markerData);
                player.sendMessage(Component.text("頂点を追加しました。"));
            } else {
                ChatUtil.error(player,"自治体の始点と同じワールド内で頂点を決めてください。");
            }
        } else if(Objects.equals(args[0],"end")){
            if(Gui.isBedrock(player)){
                CustomForm.Builder builder = CustomForm.builder()
                        .title("自治体を作成")
                        .input("自治体名を入力")
                        .responseHandler((form, responseData) -> {
                            CustomFormResponse response = form.parseResponse(responseData);
                            if (!response.isCorrect()) {
                                ChatUtil.warning(player, "自治体の作成を中断しました。");
                                markerData.remove(player);
                                MarkerStore.setMarkerData(markerData);
                            }
                            else createMunicipal(player, response.getInput(0));
                        });
                final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                fPlayer.sendForm(builder);
            } else {
                AtomicBoolean complete = new AtomicBoolean(false);
                new AnvilGUI.Builder()
                        .onClose(p -> {
                            if (!complete.get()) {
                                ChatUtil.warning(p, "自治体の作成を中断しました。");
                                markerData.remove(p);
                                MarkerStore.setMarkerData(markerData);
                            }
                        })
                        .onComplete((p, text) -> {
                            complete.set(true);
                            createMunicipal(p, text);
                            return AnvilGUI.Response.close();
                        })
                        .title("自治体名を入力")
                        .text("")
                        .plugin(MetaversePlugin.getInstance())
                        .open(player);
            }
        }
        return true;
    }

    private void createMunicipal(Player p, String title){
        final DynmapAPI dynmap = MetaversePlugin.getDynmap();
        final MarkerAPI marker = dynmap.getMarkerAPI();
        MarkerSet markerSet = marker.getMarkerSet("municipal");
        if(markerSet == null) {
            markerSet = marker.createMarkerSet("municipal","自治体",null,true);
        }

        final HashMap<Player, ArrayList<Location>> markerData = MarkerStore.getMarkerData();
        double[] X_list = markerData.get(p).stream().map(Location::getX).mapToDouble(b -> b).toArray();
        double[] Z_list = markerData.get(p).stream().map(Location::getZ).mapToDouble(b -> b).toArray();
//        Bukkit.getLogger().info(String.valueOf(markerSet.getAreaMarkers().size()));
        markerSet.createAreaMarker("m"+markerSet.getAreaMarkers().size(),title,false,markerData.get(p).get(0).getWorld().getName(),X_list,Z_list,true);
        ChatUtil.success(p, "自治体を正常に作成しました。");
    }
}
