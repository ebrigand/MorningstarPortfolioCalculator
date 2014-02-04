package com.morningstar.portfoliocalculator.xaoservice.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ebrigand on 02/02/14.
 */
public enum XAOLocationEnum {

    PORTFOLIOS_PATH(Paths.get("src/main/resources/Portfolios/")),
    SECURITIES_PATH(Paths.get("src/main/resources/Securities/"));

    private Path path;

    XAOLocationEnum(Path path){
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
