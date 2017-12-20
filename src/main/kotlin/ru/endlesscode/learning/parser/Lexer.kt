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


class Lexer {

    private val words: MutableMap<String, Word> = hashMapOf()

    private var peek = ' '
    private var iterator: CharIterator? = null
    private var eof = false

    var source: String
        set(value) {
            iterator = value.iterator()
            eof = false
        }
        get() = iterator.toString()

    fun scanNext(): Token {
        peekNext()

        var skipped = true
        while (skipped) {
            skipped = skipEmptyChars()

            if (peek == '(') {
                if (!peekNextIs('*')) return Token('(')
                skipComment()
                skipped = true
            }
        }

        when {
            peek.isLetter() -> return readIdentifier()
            peek in Word.QUOTES -> return readTerminal(peek)
        }

        return Token(peek).also {
            peek = ' '
        }
    }

    private fun skipEmptyChars(): Boolean {
        var skipped = false

        while (!eof) {
            if (peek != ' ' && peek != '\t' && peek != '\n') break
            skipped = true
            consumePeek()
            peekNext()
        }

        return skipped
    }

    private fun skipComment() {
        consumePeek()

        while (!eof) {
            peekNext()

            if (peek == '*' && peekNextIs(')')) {
                consumePeek()
                break
            }

            consumePeek()
        }
    }

    private fun readIdentifier(): Word {
        val sb = StringBuilder()
        do {
            consumePeek { sb.append(it) }
            peekNext()
        } while (!eof && peek.isValidFollowingSymbol())

        val lexeme = sb.toString()
        return words.getOrPut(lexeme, { Word(Tag.ID, lexeme) })
    }

    private fun readTerminal(quoteType: Char): Word {
        val sb = StringBuilder()
        do {
            consumePeek { sb.append(it) }
            peekNext()

            if (peek == quoteType) {
                consumePeek { sb.append(it) }
                break
            }
        } while (!eof)

        val lexeme = sb.toString()
        return words.getOrPut(lexeme, { Word(Tag.TERMINAL, lexeme) })
    }

    private fun peekNextIs(needed: Char): Boolean {
        consumePeek()
        peekNext()

        if (peek != needed) return false
        consumePeek()
        return true
    }

    private fun consumePeek(consume: (Char) -> Unit = { }) {
        consume(peek)
        peek = ' '
    }

    private fun peekNext() {
        if (peek != ' ') return

        val iterator = this.iterator ?: throw Error("Source should be initialized!")
        if (iterator.hasNext()) {
            this.peek = iterator.nextChar()
        } else {
            this.peek = ' '
            eof = true
        }
    }

    private fun Char.isValidFollowingSymbol(): Boolean {
        return this.isLetterOrDigit() || this == '_'
    }
}
