package lu.jimenez.research.filemetrics

import antlr.C.FunctionLexer
import antlr.C.ModuleLexer
import antlr.C.ModuleParser
import ast.ASTNode
import lu.jimenez.research.filemetrics.ast.GlobalASTWalker
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeTrue
import parsing.C.Functions.ANTLRCFunctionParserDriver
import parsing.C.Modules.ANTLRCModuleParserDriver
import parsing.FunctionParser
import parsing.TokenSubStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by Matthieu Jimenez on 29/01/2016.
 */
class CodeMetricsTest{


    companion object UtilitaryFunctions {
        fun parseAndWalk(input: String): ASTNode {
            val driver = ANTLRCFunctionParserDriver()
            val parser = FunctionParser(driver)

            val tokens = tokenStreamFromString(input)
            parser.parseAndWalkTokenStream(tokens)
            return parser.parser.builderStack.peek().item
        }

        internal fun parse(input: String): ParseTree {
            val driver = ANTLRCFunctionParserDriver()
            val parser = FunctionParser(driver)

            return parser.parseString(input)
        }

        fun parseModule(input: String):ModuleLexer{
            val parser = ANTLRCModuleParserDriver()
            val lex =  ModuleLexer(ANTLRInputStream(input));
            return lex
        }

        private fun tokenStreamFromString(input: String): TokenSubStream {
            val inputStream = ANTLRInputStream(input)
            val lex = FunctionLexer(inputStream)
            val tokens = TokenSubStream(lex)
            return tokens
        }

        fun parseAndWalkModule(input: String): List<ASTNode> {
            val parser = ANTLRCModuleParserDriver()
            val walker = GlobalASTWalker()
            parser.addObserver(walker)

            val inputStream = ANTLRInputStream(Regex(" NULL").replace(input, " 0"))
            val lex = ModuleLexer(inputStream)
            val token = TokenSubStream(lex)
            parser.parseAndWalkTokenStream(token)
            //println(walker.codeItems.toString())
            return walker.codeItems

        }
        fun tryToWork(list: List<ASTNode>?) {
            if (list != null) {
                for (i in list) {
                    print(i.typeAsString + " ")
                    if (i.escapedCodeStr != "null")
                        print(i.escapedCodeStr + " ")
                    tryToWork(i.children)
                }
            }
        }
    }
}