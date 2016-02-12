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
package lu.jimenez.research.filemetrics.function

import ast.functionDef.FunctionDef
import lu.jimenez.research.filemetrics.CodeMetricsTest
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldEqual


class MetricsFunctionsTest : Spek() {

    init {

        given("a function ") {
            val functionString = """static u32 tcp_yeah_ssthresh(struct sock *sk)
            {
                const struct tcp_sock *tp = tcp_sk(sk);
                struct yeah *yeah = inet_csk_ca(sk);
                u32 reduction;

                if (yeah->doing_reno_now < TCP_YEAH_RHO && d<l) {
                reduction = yeah->lastQ;

                reduction = min(reduction, max(tp->snd_cwnd>>1, 2U));

                reduction = max(reduction, tp->snd_cwnd >> TCP_YEAH_DELTA);
            } else
                reduction = max(tp->snd_cwnd>>1, 2U);

                yeah->fast_count = 0;
                yeah->reno_count = max(yeah->reno_count>>1, 2U);

                return max_t(int, tp->snd_cwnd - reduction, 2);
            }"""
            val function: FunctionDef = CodeMetricsTest.parseAndWalkModule(functionString)[0] as FunctionDef

            on("computing its cyclomatic complexity") {
                val cc = MetricsFunctions.cyclomaticComplexityOfFunction(function)
                it("should return 2") {
                    shouldEqual(2, cc)
                }
            }

            on("computing its strict cyclomatic complexity") {
                val scc = MetricsFunctions.strictCyclomaticComplexityOfFunction(function)
                it("should return 3") {
                    shouldEqual(3, scc)
                }
            }
            on("computing its modified cyclomatic complexity") {
                val mcc = MetricsFunctions.modifiedCyclomaticComplexityOfFunction(function)
                it("should return 2") {
                    shouldEqual(2, mcc)
                }
            }
            on("computing its essential complexity") {
                val ec = MetricsFunctions.essentialComplexityOfFunction(function)
                it("should return 1") {
                    shouldEqual(1, ec)
                }
            }
            on("computing its max nesting") {
                val mn = MetricsFunctions.maxNestingFunction(function)
                it("should return 1") {
                    shouldEqual(1, mn)
                }
            }

        }
    }
}