package org.migdb.migdbclient.resources;

import org.json.simple.JSONArray;

public class ChangeStructure {

    public static JSONArray nodeDataArray = new JSONArray();
    public static JSONArray linkDataArray = new JSONArray();
    public static String jsonFileName= "";

    private static ChangeStructure instance = null;
    protected ChangeStructure() {
        // Exists only to defeat instantiation.
    }
    public static ChangeStructure getInstance() {
        if(instance == null) {
            instance = new ChangeStructure();
        }
        return instance;
    }


}
