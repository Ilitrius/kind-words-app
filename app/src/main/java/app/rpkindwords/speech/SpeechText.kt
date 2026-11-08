package app.rpkindwords.speech

/** Bereitet einen Satz zum Vorlesen auf: Emoji, Symbole und Textgesichter werden nicht mitgesprochen. */
object SpeechText {
    private const val KEEP = " .,!?:;'-"

    fun clean(text: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            i += Character.charCount(cp)
            val keep = Character.isLetterOrDigit(cp) || (cp < 128 && KEEP.indexOf(cp.toChar()) >= 0) || cp == 0x2019 || cp == 0x2026
            if (keep) sb.appendCodePoint(cp) else sb.append(' ')
        }
        return sb.toString().replace(Regex("\\s+"), " ").replace(Regex(" ([.,!?:;…])"), "$1").trim()
    }

    /** Lesbar, wenn nach dem Aufbereiten mindestens ein Wort aus zwei Buchstaben übrig bleibt. */
    fun speakable(text: String): Boolean = Regex("\\p{L}{2,}").containsMatchIn(clean(text))
}
