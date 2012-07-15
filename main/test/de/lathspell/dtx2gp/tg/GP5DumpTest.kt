package de.lathspell.dtx2gp.tg

import de.lathspell.dtx2gp.tg.TGHelper
import org.junit.Assert.assertTrue
import org.junit.Test

class GP5DumpTest {

    [Test]
    fun dumpTest() {
        val song = TGHelper().readGP5Song("data/test6.gp5")
        println(TGHelper().dumpCompactTGSong(song))
        println(TGHelper().dumpTGSong(song))
        assertTrue(true)
    }
}
