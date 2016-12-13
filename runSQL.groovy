
/**
 * Executes a read (SELECT) SQL command and returns the results
 * 
 * This is particularly useful for derby databases that do not allow external access
 * 
 * This REST execution point is called 'runSQL'
 * It can be called via: {ARTIFACTORY_URL}/api/plugins/execute/runSQL
 * 
 * NOTE: This allows any read operation to be performed on the database. 
 * Access should be restricted to administrators only.
 * 
 * @author Arturo Aparicio
 */
import org.artifactory.storage.db.DbService
import org.artifactory.storage.db.util.JdbcHelper
import org.artifactory.resource.ResourceStreamHandle
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.sql.*;


 
// REST Entry Point
executions {
    runSQL(version:'0.1',
                   description:'Executes a read SQL command and returns the results.',
                   httpMethod: 'POST') { params, ResourceStreamHandle body ->
        def reader = new InputStreamReader(body.inputStream, 'UTF-8')
        def json = null
        try {
            json = new JsonSlurper().parse(reader)
        } catch (groovy.json.JsonException ex) {
            message = "Problem parsing JSON: $ex.message"
            status = 400
            return
        }
        if (!(json instanceof Map)) {
            message = 'Provided value must be a JSON object'
            status = 400
            return
        }
        if (!json['sql']) {
            message = 'A sql statement is required'
            status = 400
            return
        }
        def resp = null;	
        try {        
            resp = _runSQL(json['sql'])
        } catch (Exception ex) {
            message = 'Exception while running query: ' + ex.getMessage()
            status = 400
            return
        }

        def userResponse = new JsonBuilder()
        userResponse {
            success(true)
            response(resp)
        }       
        message = userResponse.toPrettyString()
    }
}


// Run and return result
private def _runSQL(String sqlStatement) {
    def response = ctx.beanForType(JdbcHelper.class).executeSelect(sqlStatement)
    log.info("SQL Plugin: " + sqlStatement);
    return formatQuery(response);
}

// Format the query
private def ArrayList<HashMap<String,Object>> formatQuery(ResultSet result) {
  try {

    //get metadata
    ResultSetMetaData meta = null;
    meta = result.getMetaData();

    //get column names
    int colCount = meta.getColumnCount();
    ArrayList<String> cols = new ArrayList<String>();
    for (int index=1; index<=colCount; index++)
      cols.add(meta.getColumnName(index));

    //fetch out rows
    ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();

    while (result.next()) {
      HashMap<String,Object> row = new HashMap<String,Object>();
      for (String colName:cols) {
        Object val = result.getObject(colName);
        row.put(colName,val);
      }
      rows.add(row);
    }

    //pass back rows
    return rows;
  }
  catch (Exception ex) {
    System.out.print(ex.getMessage());
    return new ArrayList<HashMap<String,Object>>();
  }
}