package de.lathspell.dtx2gp

import de.lathspell.dtx2gp.dtx.DTXFile
import de.lathspell.dtx2gp.dtx.DTXReader
import de.lathspell.dtx2gp.tg.TGHelper
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.apache.commons.io.FileUtils
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.herac.tuxguitar.song.models.TGSong
import org.junit.Test

class DtxToTuxguitarTest {

    val log = Logger.getLogger(this.javaClass)!!

    constructor {
        val rootLogger = Logger.getRootLogger()!!
        rootLogger.addAppender(ConsoleAppender(PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)))
        rootLogger.setLevel(Level.DEBUG)
        Logger.getLogger("DTX.DTXReader")?.setLevel(Level.INFO)
    }

    [Test]
    fun testConvert3() {
        stdTest("test3")
    }

    [Test]
    fun testConvert4() {
        stdTest("test4")
    }

    [Test]
    fun testConvert5() {
        stdTest("test5")
    }

    [Test]
    fun testConvert6() {
        stdTest("test6")
    }

    fun stdTest(base: String) {
        log.info("Starting $base")

        val text = File("data/$base.dtx").readText("UTF-8")
        val dtxfile: DTXFile = DTXReader().readText(text)
        val tgsong: TGSong = DtxToTuxguitar().convertSong(dtxfile)

        // Write dump and .gp5
        val dump = TGHelper().dumpTGSong(tgsong)
        log.info("Compact dump:\n" + TGHelper().dumpCompactTGSong(tgsong))
        FileUtils.writeStringToFile(File("data/$base.dump"), dump)
        TGHelper().writeGP5Song("data/$base.gp5", tgsong)

        // Check if it's readable
        val readSong = TGHelper().readGP5Song("data/$base.gp5")
        assertTrue(readSong is TGSong)

        // Check if the dump was ok
        assertEquals(FileUtils.readFileToString(File("data/$base.dump.ok")), dump)
    }

}
