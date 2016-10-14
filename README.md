#FileMetrics

##Introduction
This project aims at providing a library for computing common metrics of a C file from Java program.
The analysis is based on the result provided by the [joern tool](https://github.com/fabsx00/joern).
To allow a better handling of dependency the tool has been ported organized for maven and only the parts relevant to this project were kept.
This version can be found in the joern repository

##Use
The library is written in Kotlin but can be used from Java.
The library take the File content (String) as an input.

    val cm = CodeMetrics(fileContent)
    cm.cyclomaticComplexity()


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

## Other uses 
This library uses the following function to compute the metrics that can be reuse in other context:

* Function level

    * list of Parameters  (distinction possible between reference and value)
    * list of calls 
    * list of assignment 
    * list of declaration 
    * all complexity metrics + nesting + fan in / fan out
    
* File Level

    * list of function names
    * list of Global variable
    *  map of call made by a function
