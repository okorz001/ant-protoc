ant-protoc
==========

ant-protoc provides an ant task for running protoc, the protobuf
compiler.

Dependencies
------------

* Java 5+ (1.5+)

ant-protoc uses generics which were introduced in Java 5. Google's protoc
generates Java source that depends on Java 5 for enums, so this should be
a reasonable requirement.

* Ant 1.8+

ant-protoc may work with older versions of ant, but this has not been tested.

* protoc

ant-protoc concievably works with any released version of protoc since the CLI
is stable. To date, only 2.5.0 has been tested.

Building
--------

The repository contains an Ant build script that will compile the task and
create a JAR for distribution.

Usage
-----

Register task from the JAR with a taskdef:

```xml
<taskdef name="protoc" classname="org.korz.ant.ProtocTask">
    <classpath>
        <pathelement location="${jar}"/>
    </classpath>
</taskdef>
```

### Supported Attributes

* srcFile

Specifies a single proto file to compile. This is mutually exclusive with nested
filesets. Required unless filesets are provided.

* protoPath

Corresponds to protoc's --proto_path argument. Required since ant transforms all
filepaths into absolute paths.

* cppOut

Corresponds to protoc's --cpp_out argument. At least one of cppOut, javaOut and
pythonOut is required.

* javaOut

Corresponds to protoc's --java_out argument. At least one of cppOut, javaOut and
pythonOut is required.

* pythonOut

Corresponds to protoc's --python_out argument. At least one of cppOut, javaOut
and pythonOut is required.

### Supported Nested Elements

* fileset

Specifies one or more proto files to compile. May be repeated. This is mutually
exclusive with the srcFile attribute. Required unless srcFile is provided.

Limitations
-----------

* The task always executes protoc. The task needs to check timestamps to
  check if the output is up-to-date.

* protoc must be on the path. The task needs support for an explicit path to
  the protoc executable.

* Third-party generators are not supported. The task needs support for
  arbitrary command line options.

More Info
---------

The authors of ant-protoc are not responsible for either ant or protoc.

Ant is maintained by the Apache Software Foundation:
http://ant.apache.org

Protobuf is maintained by Google:
https://code.google.com/p/protobuf

License
-------

ant-protoc is released under the 2-clause BSD license. The full terms of
the license are provided inline in the source.

Author
------

Oscar Korz <okorz001@gmail.com>
