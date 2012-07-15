package de.lathspell.dtx2gp

import de.lathspell.dtx2gp.dtx.DTXFile
import de.lathspell.dtx2gp.dtx.DTXReader
import de.lathspell.dtx2gp.tg.TGHelper
import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.herac.tuxguitar.song.models.TGSong
import org.junit.Test

class SpielplatzTest {

    {
        val rootLogger = Logger.getRootLogger()!!
        rootLogger.setLevel(Level.DEBUG)
        rootLogger.addAppender(ConsoleAppender(PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)))
        // rootLogger.getLoggerRepository()!!.getLogger("DTX.DTXReader")!!.setLevel(Level.toLevel(Level.INFO_INT))
    }

    val log = Logger.getLogger(this.javaClass)!!

    [Test]
    fun testReadGP5() {
        val base = "test5b"
        val tgsong: TGSong = TGHelper().readGP5Song("data/$base.gp5")
        val dump: String = TGHelper().dumpTGSong(tgsong)
        FileUtils.writeStringToFile(File("data/$base.dump"), dump)
    }

    [Test]
    fun testDtxToGP5() {
        val base = "real"
        val text = File("data/$base.dtx").readText("UTF-8")
        val dtxfile: DTXFile = DTXReader().readText(text)
        val tgsong: TGSong = DtxToTuxguitar().convertSong(dtxfile)

        // Write dump and .gp5
        val dump = TGHelper().dumpTGSong(tgsong)
        FileUtils.writeStringToFile(File("data/$base.dump"), dump)
        TGHelper().writeGP5Song("data/$base.gp5", tgsong)
    }

}
