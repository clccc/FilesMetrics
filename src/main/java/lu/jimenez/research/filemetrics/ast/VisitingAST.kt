/////////////////////////////////////////////////////////////////////////////////////////
//                 University of Luxembourg  -
//                 Interdisciplinary center for Security and Trust (SnT)
//                 Copyright ©2016 University of Luxembourg, SnT
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


package lu.jimenez.research.filemetrics.ast

import ast.ASTNode
import ast.declarations.ClassDefStatement
import ast.declarations.IdentifierDecl
import ast.declarations.IdentifierDeclType
import ast.expressions.*
import ast.functionDef.*
import ast.statements.*

/**
 * Visiting AST Object
 *
 * Object containing function that allow to walk through an AST
 * This object is more here for understanding purpose
 */
object VisitingAST {
    /**
     * Function that take an AST NOde as an Input and will print his information and those of his child depending of his type
     *
     * @param astNode AST Node to study
     */
    fun whenASTNode(astNode: ASTNode) {
        when (astNode) {
            is FunctionDef -> {
                print("Function Definition " + astNode.functionSignature + "\n with Parameter ")
                whenASTNode(astNode.parameterList)
                whenStatement(astNode.content)
            }
            is ReturnType -> {
                println("ReturnType " + astNode.escapedCodeStr)
            }
            is Parameter -> {
                print("Parameter with ")
                whenASTNode(astNode.name)
                print(" and")
                whenASTNode(astNode.type)
            }
            is ParameterType -> {
                println("Parameter Type " + astNode.escapedCodeStr)
            }
            is IdentifierDecl -> {
                print("Identifier Decl ")
                whenASTNode(astNode.name)
                print(" and ")
                whenASTNode(astNode.type)
            }
            is IdentifierDeclType -> {
                println("Identifier Decl Type " + astNode.escapedCodeStr)
            }
            is ParameterList -> {
                println("Parameter List: ")
                for (parameter in astNode.parameters) whenASTNode(parameter)
            }
            is Expression -> {
                println("Expression -> ")
                whenExpression(astNode)
            }
            is Statement -> {
                println("Statement ->")
                whenStatement(astNode)
            }
            else -> println("something else")
        }
    }

    /**
     * Function that take an [Expression] as Input and will print the detail of this expression
     *
     * @param expression expression to study
     */
    fun whenExpression(expression: Expression) {
        when (expression) {
            is PostfixExpression -> {
                print("Postfix ")
                whenPostFixExpression(expression)
            }
            is BinaryExpression -> {
                print("Binary Exp ")
                whenBinaryExpression(expression)
            }
            is ExpressionHolder -> {
                print("Expression Holder ")
                whenExpressionHolder(expression)
            }
            is CastTarget -> {
                println("Cast Target " + expression.escapedCodeStr)
            }
            is ConditionalExpression -> {
                println("Conditional Expression " + expression.escapedCodeStr)
            }
            is UnaryOperator -> {
                println("Unary Operator " + expression.escapedCodeStr)
            }
            is Identifier -> {
                println("Identifier " + expression.escapedCodeStr)
            }
            is IncDec -> {
                println("IncDec " + expression.escapedCodeStr)
            }
            is ForInit -> {
                println("For init " + expression.escapedCodeStr)
            }
            is SizeofOperand -> {
                println("For init " + expression.escapedCodeStr)
            }
            is CastExpression -> {
                print("CastExpression ")
                whenASTNode(expression.castTarget)
            }
            is UnaryExpression -> {
                println("Unary Expression " + expression.escapedCodeStr)
            }
            is UnaryOp -> {
                println("Unary Op " + expression.escapedCodeStr)
            }
            is Sizeof -> {
                println("Size of " + expression.escapedCodeStr)
            }
            else -> println("something else")
        }
        print(expression.operator)
    }

    /**
     * Function that will take a [BinaryExpression] as Input and will print its details depending on its type
     *
     * @param binary Binary Expression to study
     */
    fun whenBinaryExpression(binary: BinaryExpression) {
        when (binary) {
            is InclusiveOrExpression -> {
                println("InclusiveOrExpression " + binary.escapedCodeStr)
            }
            is EqualityExpression -> {
                println("Equality Expression " + binary.escapedCodeStr)
            }
            is RelationalExpression -> {
                println("Relational Expression " + binary.escapedCodeStr)
            }
            is AndExpression -> {
                println("And Expression " + binary.escapedCodeStr)
            }
            is MultiplicativeExpression -> {
                println("Multiplicative Expression " + binary.escapedCodeStr)
            }
            is AssignmentExpr -> {
                println("Assignement Expression " + binary.escapedCodeStr)
            }
            is AdditiveExpression -> {
                println("Additive Expression " + binary.escapedCodeStr)
            }
            is ShiftExpression -> {
                println("Shift Expression " + binary.escapedCodeStr)
            }
            is BitAndExpression -> {
                println("Bit and Expression " + binary.escapedCodeStr)
            }
            is ExclusiveOrExpression -> {
                println("Exclusive Or Expression " + binary.escapedCodeStr)
            }
            is OrExpression -> {
                println("Or Expression " + binary.escapedCodeStr)
            }
            else -> println("something else")
        }
        print("left ")
        whenExpression(binary.left)
        print("right ")
        whenExpression(binary.right)
    }

    /**
     * Function that will take an [ExpressionHolder] as Input and will print its details depending on its type
     *
     * @param holder Expression Holder to study
     */
    fun whenExpressionHolder(holder: ExpressionHolder) {
        when (holder) {
            is Argument -> {
                println("Argument " + holder.escapedCodeStr)
            }
            is Condition -> {
                println("Condition " + holder.escapedCodeStr)
            }
            is InitializerList -> {
                println("Initializer List " + holder.escapedCodeStr + " " + holder.childCount)
            }
            is Callee -> {
                println("Callee" + holder.escapedCodeStr)
            }
            is ArgumentList -> {
                println("Callee" + holder.escapedCodeStr + " " + holder.childCount)
            }
            else -> println("something else")

        }
        whenExpression(holder.expression)
    }

    /**
     * Function that will take a [PostfixExpression] as Input and will print its details depending on its type
     *
     * @param postfix Postfix Expression to study
     */
    fun whenPostFixExpression(postfix: PostfixExpression) {
        when (postfix) {
            is IncDecOp -> {
                println("Inc Dec op " + postfix.escapedCodeStr)
            }
            is PrimaryExpression -> {
                println("Primary Expr" + postfix.escapedCodeStr)
            }
            is PtrMemberAccess -> {
                println("PtrMember Access" + postfix.escapedCodeStr)
            }
            is MemberAccess -> {
                println("Member Access" + postfix.escapedCodeStr)
            }
            is CallExpression -> {
                print("Call Expresion: ")
                whenExpression(postfix.target)
            }
        }
    }

    /**
     * Function that will take a [Statement] as Input and will print its details and those of its eventual children depending on its type
     *
     * @param statement AST Node to study
     */
    fun whenStatement(statement: Statement) {
        when (statement) {
            is CompoundStatement -> {
                println("Compound ->")
                for (stat in statement.statements) whenStatement(stat as Statement)
                println("********************")
            }
            is ClassDefStatement -> {
                print("Class Def ->")
                whenASTNode(statement.name)
                whenASTNode(statement.content)
                println("---------------")
            }
            is BlockStarter -> {
                print("Block Starter -> ")
                whenBlockStarter(statement)
            }
            is Label -> {
                println("Label " + statement.escapedCodeStr)
            }
            is IdentifierDeclStatement -> {
                print("Identifier Decl stat ->")
                for (id in statement.identifierDeclList) whenASTNode(id)
                println("---------------")
            }
            is ExpressionHolderStatement -> {
                print("Expression Holder -> ")
                whenExpressionHolderStatement(statement)
            }
            is JumpStatement -> {
                print("Jump -> ")
                whenJumpStatement(statement)
            }
            is BlockCloser -> {
                println("Block Closer " + statement.escapedCodeStr)
            }
            else -> {
                println("Statement" + statement.childCount)
            }
        }


    }

    /**
     * Function that will take a [JumpStatement] as Input and will print its details depending on its type
     *
     * @param jump jump statement to study
     */
    fun whenJumpStatement(jump: JumpStatement) {
        when (jump) {
            is ThrowStatement -> {
                println("Throw Statement" + jump.escapedCodeStr)
            }
            is ReturnStatement -> {
                println("Return Statement" + jump.escapedCodeStr)
            }
            is BreakStatement -> {
                println("Break Statement" + jump.escapedCodeStr)
            }
            is ContinueStatement -> {
                println("Continue Statement" + jump.escapedCodeStr)
            }
            is GotoStatement -> {
                println("Goto Statement" + jump.escapedCodeStr)
            }
        }
    }

    /**
     * Function that will take a [ExpressionHolderStatement] as Input and will print its details depending on its type
     *
     * @param holderstat Expression Holder Statement to study
     */
    fun whenExpressionHolderStatement(holderstat: ExpressionHolderStatement) {
        when (holderstat) {
            is ExpressionStatement -> {
                print("Expression Statement" + holderstat.escapedCodeStr)
            }
            else -> {
                print(holderstat.escapedCodeStr)
            }
        }
        if (holderstat.expression != null)
            whenExpression(holderstat.expression)
    }

    /**
     * Function that will take a [BlockStarter] as Input and will print its details depending on its type
     *
     * @param blockstart AST Node to study
     */
    fun whenBlockStarter(blockstart: BlockStarter) {
        if ( blockstart.condition != null) {
            print(" condition: ")
            whenExpressionHolder(blockstart.condition)
        }
        when (blockstart) {
            is ForStatement -> {
                print("for ->")
                whenASTNode(blockstart.expression)
                whenASTNode(blockstart.forInitStatement)
            }
            is WhileStatement -> {
                println("While Statement" + blockstart.escapedCodeStr)
            }
            is DoStatement -> {
                println("Do Statement" + blockstart.escapedCodeStr)
            }
            is TryStatement -> {
                print("Try Statement ->")
                for (catc in blockstart.catchNodes)
                    whenBlockStarter(catc)
            }
            is CatchStatement -> {
                println("Catch Statement" + blockstart.escapedCodeStr)
            }
            is IfStatement -> {
                print("If Statement ->" + blockstart.escapedCodeStr)
                if (blockstart.elseNode != null)
                    whenBlockStarter(blockstart.elseNode)
            }
            is ElseStatement -> {
                println("Else Statement" + blockstart.escapedCodeStr)
            }
            is SwitchStatement -> {
                println("Switch Statement" + blockstart.escapedCodeStr)
            }
        }
    }
}