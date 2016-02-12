package lu.jimenez.research.filemetrics

import antlr.C.FunctionLexer
import antlr.C.ModuleLexer
import ast.ASTNode
import lu.jimenez.research.filemetrics.ast.TestASTWalker
import org.antlr.v4.runtime.ANTLRInputStream
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
class CodeMetricsTest : Spek() {
    val path = javaClass.classLoader.getResource("").path

    init {
        val input = """int ddg_simplest_test()
{
// Make sure x propagates to foo
int x = 0;
foo(x);
}

int ddg_test_struct()
{
struct my_struct foo;
foo.bar = 10;
copy_somehwere(foo);
}"""
        val input2 = "x = y;"
        val input3 = String(Files.readAllBytes(Paths.get(path + "/cfile/tcp.c")))
        val input4 = String(Files.readAllBytes(Paths.get(path + "/cfile/inode.c")))
        val input5 = String(Files.readAllBytes(Paths.get(path + "/cfile/security.c")))
        given("a Parser") {
            val cm = CodeMetrics(input4)
            /** on("calculating the number of loc") {
            val loc = cm.linesOfCode
            it("should return 13") {
            shouldEqual(12, loc)
            }
            }
            on("calculating assignement") {
            val decl = cm.countDeclFunction()
            it("should return 6") {
            shouldEqual(6, decl)
            }
            }*/
            on("complexity") {
                //TODO
                it("") {
                    shouldBeTrue(true)
                }
            }
        }

    }

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

        private fun tokenStreamFromString(input: String): TokenSubStream {
            val inputStream = ANTLRInputStream(input)
            val lex = FunctionLexer(inputStream)
            val tokens = TokenSubStream(lex)
            return tokens
        }

        fun parseAndWalkModule(input: String): List<ASTNode> {
            val parser = ANTLRCModuleParserDriver()
            val walker = TestASTWalker()
            parser.addObserver(walker)

            val inputStream = ANTLRInputStream(Regex(" NULL").replace(input, " 0"))
            val lex = ModuleLexer(inputStream)
            val token = TokenSubStream(lex)
            parser.parseAndWalkTokenStream(token)
            return walker.codeItems

        }
    }
}