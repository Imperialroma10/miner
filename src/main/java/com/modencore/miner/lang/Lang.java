package com.modencore.miner.lang;

import com.liba.utils.file.FileChecker;

public class Lang extends FileChecker {
    public Lang(String filedir) {
        super(filedir);
    }

    @Override
    public void needle() {
        addParam("menutitle", "Here is the name that will be in the menutitle variable, for example Miner menu");
    }
}
