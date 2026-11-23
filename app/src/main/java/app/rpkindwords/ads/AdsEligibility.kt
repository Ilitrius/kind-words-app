package app.rpkindwords.ads

/** Regeln, wann Werbung erscheinen darf (rein, getestet in AdsEligibilityTest). */
object AdsEligibility {
    /** Die ersten Tage nach der Installation bleibt die App werbefrei, damit der erste Eindruck warm bleibt. */
    const val AD_FREE_DAYS = 3
    private const val DAY_MS = 24L * 60 * 60 * 1000

    fun adsAllowed(firstRunAtMillis: Long, nowMillis: Long): Boolean =
        nowMillis - firstRunAtMillis >= AD_FREE_DAYS * DAY_MS
}
