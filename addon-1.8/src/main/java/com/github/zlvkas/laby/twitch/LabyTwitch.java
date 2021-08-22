package com.github.zlvkas.laby.twitch;

import com.github.zlvkas.laby.twitch.module.TwitchModule;
import com.github.zlvkas.laby.twitch.util.TwitchUtil;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Material;

import java.util.List;

public class LabyTwitch extends LabyModAddon {

    private final TwitchUtil twitchUtil = new TwitchUtil();

    @Override
    public void onEnable() {
        final TwitchModule twitchModule = new TwitchModule(this.twitchUtil);
        this.twitchUtil.setTwitchModule(twitchModule);
        super.getApi().registerModule(new TwitchModule(twitchUtil));
    }

    @Override
    public void loadConfig() {
        if (!super.getConfig().has("twitchName")) {
            super.getConfig().addProperty("twitchName", "");
        }

        this.twitchUtil.startUpdater(super.getConfig());
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        list.add(new StringElement("Twitch Name", new ControlElement.IconData(Material.NAME_TAG), super.getConfig().get("twitchName").getAsString(), changedValue -> {
            super.getConfig().addProperty("twitchName", changedValue);
        }));
    }

}