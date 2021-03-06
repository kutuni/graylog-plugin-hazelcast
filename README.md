Hazelcast log4j2 Plugin for Graylog
============================

Plugin designed for [Apache Log4j2](https://logging.apache.org/log4j/2.x/) logs messages send to a [Graylog2](http://www.graylog2.org) servers by [Hazelcast](http://hazelcast.org) cluster.

This plugin work together [Log4j2-Appender](https://github.com/kutuni/hazelcast-log4j--appender).

**Required Graylog version:** 1.0 and later

## Installation

[Download the plugin](https://github.com/kutuni/graylog-plugin-hazelcast/blob/master/target/hcplugin-0.1.jar)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Lastly copy hazelcast-all jar into graylog server.
hazelcast-all-{version}.jar (include version pom.xml) copy to {graylog.home}/embedded/jre/jre/lib/ext/hazelcast-all-{version}.jar

Restart `graylog-server` and you are done.

## Usage

The only thing you need to do in your Hazelcast Logger (HC Logger)  interface is to add a new service called *Graylog*. Click *Services* in the main menu and then hit the *Add new service* button.

![Screenshot: Setup for plugin](https://github.com/kutuni/graylog-plugin-hazelcast/blob/master/graylog-hcplugin-setup.png)


## Build

This project is using Maven and requires Java 7 or higher.

You can build a plugin (JAR) with `mvn package`.
