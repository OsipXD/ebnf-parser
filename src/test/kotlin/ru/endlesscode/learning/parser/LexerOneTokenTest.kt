/*
 * This file is part of parser, licensed under the MIT License (MIT).
 *
 * Copyright (c) Osip Fatkullin <osip.fatkullin@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.endlesscode.learning.parser

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals


@RunWith(Parameterized::class)
class LexerOneTokenTest(
        private val source: String,
        private val token: Token
) {

    private lateinit var lexer: Lexer

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<out Any>> {
            return listOf(
                    arrayOf("a", Word(Tag.ID, "a")),
                    arrayOf("abba", Word(Tag.ID, "abba")),
                    arrayOf("'abb\"a'", Word(Tag.TERMINAL, "'abb\"a'")),
                    arrayOf("\"abb'a\"'", Word(Tag.TERMINAL, "\"abb'a\"")),
                    arrayOf(";", Token(Tag.END)),
                    arrayOf("=", Token(Tag.DEF)),
                    arrayOf(" abb ab", Word(Tag.ID, "abb")),
                    arrayOf(" \t\n    abb ab", Word(Tag.ID, "abb")),
                    arrayOf(" (*= ; \n\t    value1.23*) ab", Word(Tag.ID, "ab"))
            )
        }
    }

    @Before
    fun setUp() {
        this.lexer = Lexer()
        lexer.source = source
    }

    @Test
    fun parseShouldWorkRight() {
        assertEquals(token, lexer.scanNext())
    }
}