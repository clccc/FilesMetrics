# FilesMetrics Project

##Introduction
This project aims at providing a library for computing common metrics of a C file from Java program.
The analysis is based on the result provided by the [joern tool](https://github.com/fabsx00/joern).
To allow a better handling of dependancy the tool has been ported organized for maven and only the parts relevant to this project were kept.
This version can be found here : (https://github.com/electricalwind/joernANTLR)

##Use
The library is written in Kotlin but can be used from Java.
The library take the File content (String) as an input.
``` kotlin
val cm = CodeMetrics(fileContent)
cm.cyclomaticComplexity()
```
Different functions can then be called to retrieved the different Metrics

##Computed Metrics
 List of Code Metrics:
 * Blank Line
 * LOC
 * preprocessor Lines
 * Lines of comments
 * Comment/LOC ratio
 * Number of Function
 * Number of Variable declaration
 * Cyclomatic Complexity of all Function
 * Essential Complexity of all Function
 * FANIN
 * FANOUT

##Dependency
The project relies on the joernANTLR repo

