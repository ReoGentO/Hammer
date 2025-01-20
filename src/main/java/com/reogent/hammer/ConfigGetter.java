package com.reogent.hammer;

public class ConfigGetter {
    private static final Hammer inst = Hammer.getInstance();
    public final ConfigManager config = inst.getMainConfig();

}
