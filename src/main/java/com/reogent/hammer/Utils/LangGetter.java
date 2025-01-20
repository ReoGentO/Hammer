package com.reogent.hammer.Utils;

import com.reogent.hammer.ConfigManager;
import com.reogent.hammer.Hammer;

public class LangGetter {
    private static final Hammer inst = Hammer.getInstance();
    public final ConfigManager configLang = inst.getLangConfig();
    public String prefix = configLang.getConfig().getString("prefix");
    public String reload_message = configLang.getConfig().getString("reload_message");
    public String missing_arg = configLang.getConfig().getString("missing_arg");
    public String error_message = configLang.getConfig().getString("error_message");
    public String hammer_name = configLang.getConfig().getString("hammer.name");
    public String hammer_lore = configLang.getConfig().getString("hammer.lore");
    public String breeze_name = configLang.getConfig().getString("breeze.name");
    public String breeze_lore = configLang.getConfig().getString("breeze.lore");
}
