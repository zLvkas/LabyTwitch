package com.github.zlvkas.laby.twitch.module;

import com.github.zlvkas.laby.twitch.util.TwitchUtil;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

public class TwitchModule extends SimpleModule {

    private final TwitchUtil twitchUtil;

    public TwitchModule(TwitchUtil twitchUtil) {
        this.twitchUtil = twitchUtil;
    }

    @Override
    public String getDisplayName() {
        return "Twitch";
    }

    @Override
    public String getDisplayValue() {
        return String.valueOf(this.twitchUtil.getFollowers());
    }

    @Override
    public String getDefaultValue() {
        return "-";
    }

    @Override
    public ControlElement.IconData getIconData() {
        return new ControlElement.IconData(Material.BEACON);
    }

    @Override
    public void loadSettings() {

    }

    @Override
    public String getControlName() {
        return "LabyTwitch";
    }

    @Override
    public String getSettingName() {
        return "Twitch Followers";
    }

    @Override
    public String getDescription() {
        return "Displays the twitch followers count of a specified channel";
    }

    @Override
    public int getSortingId() {
        return 0;
    }
}