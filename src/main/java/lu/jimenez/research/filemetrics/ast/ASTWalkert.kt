package lu.jimenez.research.filemetrics.ast

import ast.ASTNode
import ast.ASTNodeBuilder
import ast.walking.ASTWalker
import org.antlr.v4.runtime.ParserRuleContext
import java.util.*

class TestASTWalker : ASTWalker() {

    var codeItems: MutableList<ASTNode>

    init {
        codeItems = LinkedList<ASTNode>()
    }

    override fun startOfUnit(ctx: ParserRuleContext, filename: String) {

    }

    override fun endOfUnit(ctx: ParserRuleContext, filename: String) {

    }

    override fun processItem(item: ASTNode, itemStack: Stack<ASTNodeBuilder>) {
        codeItems.add(item)
    }


}
