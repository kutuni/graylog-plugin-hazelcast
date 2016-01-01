Hazelcast log4j2 Plugin for Graylog
============================

Buraya biseyler yaz

**Required Graylog version:** 1.0 and later

## Installation

[Download the plugin](https://github.com/kutuni/graylog-plugin-hazelcast/blob/master/target/hcplugin-0.1.jar)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

## Usage

The only thing you need to do in your Hazelcast Logger (HC Logger)  interface is to add a new service called *Graylog*. Click *Services* in the main menu and then hit the *Add new service* button.

![Screenshot: Setup for plugin](https://github.com/kutuni/graylog-plugin-hazelcast/blob/master/graylog-hcplugin-setup.png)


## Build

This project is using Maven and requires Java 7 or higher.

You can build a plugin (JAR) with `mvn package`.

DEB and RPM packages can be build with `mvn jdeb:jdeb` and `mvn rpm:rpm` respectively.

## Plugin Release

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

This sets the version numbers, creates a tag and pushes to GitHub. TravisCI will build the release artifacts and upload to GitHub automatically.
