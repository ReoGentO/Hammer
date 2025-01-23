package com.reogent.hammer.Utils;

import com.reogent.hammer.Hammer;
import com.reogent.hammer.files.LangConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class LangGetter {
    public final FileConfiguration configLang = LangConfig.get();
    public String prefix = configLang.getString("prefix");
    public String reload_message = configLang.getString("reload_message");
    public String missing_arg = configLang.getString("missing_arg");
    public String error_message = configLang.getString("error_message");
    public String hammer_name = configLang.getString("hammer.name");
    public String hammer_lore = configLang.getString("hammer.lore");
    public String breeze_name = configLang.getString("breeze.name");
    public String breeze_lore = configLang.getString("breeze.lore");

}
