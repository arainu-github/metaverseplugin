package world.arainu.core.metaverseplugin.iphone;

import github.scarsz.discordsrv.DiscordSRV;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

/**
 * iPhoneからMinecraftとDiscordを連携できるようにするクラス。
 * @author kumitatepazuru
 */
public class LinkDiscord extends iPhoneBase{
    @Override
    public void executeGui(MenuItem menuItem) {
        String code = DiscordSRV.getPlugin().getAccountLinkManager().generateCode(menuItem.getClicker().getUniqueId());
        ChatUtil.success(menuItem.getClicker(),"discordサーバー内の「草ブロック」BOT(A.L.ではありません)のDMに"+code+"を送ってください。");
    }
}
