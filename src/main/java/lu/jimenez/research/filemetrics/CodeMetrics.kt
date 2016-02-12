/////////////////////////////////////////////////////////////////////////////////////////
//                 University of Luxembourg  -
//                 Interdisciplinary center for Security and Trust (SnT)
//                 Copyright © 2016 University of Luxembourg, SnT
//
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 3 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//
//
//    Author: Matthieu Jimenez – SnT – matthieu.jimenez@uni.lu
//
//////////////////////////////////////////////////////////////////////////////////////////


package lu.jimenez.research.filemetrics

import antlr.C.ModuleLexer
import ast.ASTNode
import ast.functionDef.FunctionDef
import lu.jimenez.research.filemetrics.ast.TestASTWalker
import lu.jimenez.research.filemetrics.function.MetricsFunctions
import lu.jimenez.research.filemetrics.global.GlobalASTFunctions
import lu.jimenez.research.filemetrics.node.NodeFunctions
import org.antlr.v4.runtime.ANTLRInputStream
import parsing.C.Modules.ANTLRCModuleParserDriver
import parsing.TokenSubStream
import java.util.*

/**
 * Code Metrics Class
 *
 * Class that allow the computation of different code metrics.
 * List of Code Metrics:
 *  * Blank Line
 *  * LOC
 *  * preprocessor Lines
 *  * Lines of comments
 *  * Comment/LOC ratio
 *  * Number of Function
 *  * Number of Variable declaration
 *  * Cyclomatic Complexity of all Function
 *  * Essential Complexity of all Function
 *
 *
 */
class CodeMetrics(val fileContent: String) {

    //Basic Metric (Preprocessed)
    var blankLines = 0
    var linesOfCode = 0
    var preprocessorLines = 0
    var commentingLines = 0

    //list of AST Node in the file
    val listofNode: MutableList<ASTNode>

    /**
     * Initialization Method, compute the basic metric and the AST
     * Basic Metrics being:
     * * comments line
     * * preprocessor line
     * * lines of Codes
     */
    init {
        val fc = fileContent.split("\n")
        val lines = fc.size
        var i = 0
        while (i < lines) {
            var studiedline = fc[i]
            //Multi Line Comment (Blocking Metric)
            if (studiedline.contains("/*")) {
                commentingLines++
                while (!studiedline.contains("*/")) {
                    commentingLines++
                    i++
                    studiedline = fc[i]
                }
            }
            //Single Line Comment(Not Blocking Metric)
            if (studiedline.contains("//")) {
                commentingLines++
            }
            //Blank Line
            if (studiedline.isEmpty()) {
                blankLines++
            } else {
                //Preprocessor Lines
                if (studiedline.startsWith("#")) {
                    preprocessorLines++
                } else {
                    linesOfCode++
                }
            }
            i++

        }
        val parser = ANTLRCModuleParserDriver()
        val walker = TestASTWalker()
        parser.addObserver(walker)

        val inputStream = ANTLRInputStream(compatibleFileContent())
        val lex = ModuleLexer(inputStream)
        val token = TokenSubStream(lex)
        parser.parseAndWalkTokenStream(token)
        listofNode = walker.codeItems
    }

    /****************************************************************************************************************
     *                                                                                                              *
     * Simple Metrics                                                                                               *
     *                                                                                                              *
     ****************************************************************************************************************/

    /**
     * Function to compute the Ratio of Comment against LOC
     * Formula : commentlines/linesOfCode
     *
     * @return comment Density (Float)
     */
    fun commentDensity(): Float {
        return commentingLines.toFloat() / linesOfCode.toFloat()
    }

    /**
     * Function to count the number of function in the file
     * Formula: Number of Function
     *
     * @return number of Function (Int)
     */
    fun countDeclFunction(): Int {
        var i = 0
        for (node in listofNode) {
            if (node is FunctionDef)
                i++
        }
        return i
    }

    /**
     * Function to count the number of variable declared in a File
     * Note the number of Line is not taken into consideration, the number here is the *number of Declaration*
     *
     * @return number of variable Declaration (Int)
     */
    fun countDeclvariable(): Int {
        var numberOfVariable = 0
        for (node in listofNode) {
            numberOfVariable += NodeFunctions.countingVariableOfASTNode(node)
        }
        return numberOfVariable
    }


    /****************************************************************************************************************
     *                                                                                                              *
     * Complexity Metrics                                                                                           *
     *                                                                                                              *
     ****************************************************************************************************************/

    /**
     *Function that compute the cyclomatic complexity of all functions present in the [fileContent]
     *
     * @return map of function signature (key) and its cyclomatic complexity (value)
     */
    fun cyclomaticComplexity(): Map<String, Int> {
        val listComplexity = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val name = node.name.escapedCodeStr
                listComplexity.put(name, MetricsFunctions.cyclomaticComplexityOfFunction(node))
            }
        }
        return listComplexity
    }

    /**
     * Function that compute the Strict cyclomatic complexity of all functions present in the [fileContent]
     *
     * @return map of function signature (key) and its strict cyclomatic complexity (value)
     */
    fun strictCyclomaticComplexity(): Map<String, Int> {
        val listComplexity = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val name = node.name.escapedCodeStr
                listComplexity.put(name, MetricsFunctions.strictCyclomaticComplexityOfFunction(node))
            }
        }
        return listComplexity
    }

    /**
     * Function to compute the modified Cyclomatic Complexity of all functions present in the [fileContent]
     *
     * @return map of function signature (key) and its modified cyclomatic complexity (value)
     */
    fun modifiedCyclomaticComplexity(): Map<String, Int> {
        val listComplexity = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val name = node.name.escapedCodeStr
                listComplexity.put(name, MetricsFunctions.modifiedCyclomaticComplexityOfFunction(node))
            }
        }
        return listComplexity
    }

    /**
     * Function to compute the essential Complexity of all functions present in the [fileContent]
     *
     * @return map of function signature (key) and its essential complexity (value)
     */
    fun essentialComplexity(): Map<String, Int> {
        val listComplexity = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val name = node.name.escapedCodeStr
                listComplexity.put(name, MetricsFunctions.essentialComplexityOfFunction(node))
            }
        }
        return listComplexity
    }

    /****************************************************************************************************************
     *                                                                                                              *
     * Nesting & Fan Metrics                                                                                        *
     *                                                                                                              *
     ****************************************************************************************************************/

    /**
     * Function to compute the maximum nesting of all function present in the [fileContent]
     *
     * @return map of function signature (key) and its maximum nesting (value)
     */
    fun maxNesting(): Map<String, Int> {
        val listComplexity = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val name = node.name.escapedCodeStr
                listComplexity.put(name, MetricsFunctions.maxNestingFunction(node))
            }
        }
        return listComplexity
    }

    /**
     * Function to compute the Fan In of all function present in the [fileContent]
     *
     * @return map of function signature (key) and its FANIN (value)
     */
    fun fanIn(mapOfCall: Map<String, List<String>>? = null): List<Int> {
        var mapOfCalls = mapOfCall
        if (mapOfCall == null) {
            mapOfCalls = GlobalASTFunctions.mapOfCallMadeByFunctions(listofNode)
        }
        val listofFanIn = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val nameFunction = node.name.escapedCodeStr
                listofFanIn.put(nameFunction, MetricsFunctions.fanInOfFunction(node, mapOfCalls!!))
            }
        }
        return listOf()
    }

    /**
     * Function to compute the Fan Out of all function present in the [fileContent]
     *
     * @return map of function signature (key) and its FANOUT (value)
     */
    fun fanOut(mapOfCall: Map<String, List<String>>? = null, listOfGlobalVariable: List<String>? = null): Map<String, Int> {
        var mapOfCalls = mapOfCall
        if (mapOfCall == null) {
            mapOfCalls = GlobalASTFunctions.mapOfCallMadeByFunctions(listofNode)
        }
        var listOfGlobalVariables = listOfGlobalVariable
        if (listOfGlobalVariable == null) {
            listOfGlobalVariables = GlobalASTFunctions.listofGlobalVariable(listofNode)
        }

        val listofFanOut = HashMap<String, Int>()
        for (node in listofNode) {
            if (node is FunctionDef) {
                val nameFunction = node.name.escapedCodeStr
                val listofCall = mapOfCalls?.get(nameFunction) ?: listOf<String>()

                listofFanOut.put(nameFunction, MetricsFunctions.fanOutOfFunction(node, listofCall, listOfGlobalVariables))
            }
        }
        return listofFanOut
    }

    /****************************************************************************************************************
     *                                                                                                              *
     * Miscellaneous                                                                                                *
     *                                                                                                              *
     ****************************************************************************************************************/

    /**
     *Function that remove variable not handled by the current AST parser and replace them by their corresponding value
     * This function is used as a patch
     *
     * @return corrected string
     */
    fun compatibleFileContent(): String {
        return Regex(" NULL").replace(fileContent, " 0")
    }

}

