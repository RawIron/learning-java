package gameCommons.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;


public abstract class DbManager {}

public interface DataStore {
    public HashMap<String,Connection> retrieve();
    public boolean addPooledConnection(String dataServerKey, DbManager connectPool);
    public boolean closeAll();

    public ResultSet query(String statement, String Region, String key);
    public int execute(String statement, String Region, String key);
}

