# runSQL

## What is this?

This allows users to run arbitrary SELECT statements to the database being used by Artifactory. This is particularly useful when using Derby.


### Usage:

After the plugin is installed, it can be called via the REST API.

The syntax is as follow:

POST $ARTIFACTORY_URL/api/plugins/execute/runSQL
BODY:
```
{
	"sql": "<SELECT STATEMENT HERE>"
}
```
* Requires admin access

Example using curl:

```
 curl -XPOST -H "Content-Type: application/json" -u admin:password -d '{"sql": "SELECT * FROM users"}'  "http://localhost:8081/rtifactory/api/plugins/execute/runSQL"
```



### Installation:

1. Follow the plugin installation instructions here: https://www.jfrog.com/confluence/display/RTF/User+Plugins#UserPlugins-DeployingPlugins
   * Note that for Artifactory HA, the installation directory is ${CLUSTER_HOME}/ha-etc/plugins
2. The Artifactory log directory should show the plugin being loaded:
```
2016-01-28 23:00:08,029 [art-init] [INFO ] (o.a.a.p.GroovyRunnerImpl:268) - Loading script from 'runSQL.groovy'.
```

### Logging

Note the use of log.info will not appear in the artifactory logs unless you [enable](https://www.jfrog.com/confluence/display/RTF/User+Plugins#UserPlugins-ControllingPluginLogLevel) it. Another option would be to use info.error, which will write to the logs even without changing logback.xml.

To enable debug logging, add these lines to your logback.xml:
```
<logger name="">
    <level value="runSQL"/>
</logger>
```
