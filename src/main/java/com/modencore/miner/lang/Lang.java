package com.modencore.miner.lang;

import com.liba.utils.file.FileChecker;

import java.util.Arrays;

public class Lang extends FileChecker {
    public Lang(String filedir) {
        super(filedir);
    }

    @Override
    public void needle() {

        //   Below is how I do it
        //   Below is how I do it

        addParam("gui.title", "User menu", "Works only after server reboot", "the rest is reloaded with the /timber reload command");

        addParam("treecuting.enable.title", "&bProperty: &3Tree cutting");
        addParam("treecuting.enable.lore", Arrays.asList("  ", "&7Status: §aEnable", "&7Enable instant tree cutting"));

        addParam("treecuting.disable.title", "&bProperty: &3Tree cutting");
        addParam("treecuting.disable.lore", Arrays.asList("  ", "&7Status: &4Disable", "&7Disable instant tree cutting"));

        addParam("effects.title", "&eEffects");

        addParam("effects.description", "&bSelected particle: &a{particle}");

        addParam("effects.descriptionnotselected", Arrays.asList("  ", "&7Left click to open Effects menu"));
        addParam("effects.descriptionselected", Arrays.asList("  ", "&7Right click to remove particle"));

        addParam("effects.selectmessage", "§bYou select particle: &a{particle}");

        addParam("norightmessage", "&4No rights.");
        addParam("gui.logscount.title", "&eYou cut down {count} logs");
        addParam("gui.logscount.lore", Arrays.asList("", "&7the logger rankings are coming up."));
    }
}
