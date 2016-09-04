package org.migdb.migdbclient.resources;

import org.json.simple.JSONArray;

public class ChangeStructure {

    public JSONArray nodeDataArray = new JSONArray();
    public JSONArray linkDataArray = new JSONArray();
    public String jsonFileName= "";

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
