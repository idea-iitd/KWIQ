# KWIQ

Java implementation for finding invariant-cores.

## Compilation:

```java
javac Kwin.java
```

## Run:

```java
java Kwin DatasetFilePath startTime queryWindow threshold
```

## Dataset Format:

First line contains no. of interactions.

Rest of the lines contain interaction in below format:

```java
Type NodeId NodeId Timestamp
```

Type=1 for insertion.

Type=0 for deletion.

NodeId is Id of the endpoint of the edge.

Timestamp is time of interaction.

15wiki.txt is sample dataset with above format.

## startTime

Timestamp of the interaction from which you want to start query.

## queryWindow

No. of days for which you want to run query.

## threshold

invariant-core threshold.
