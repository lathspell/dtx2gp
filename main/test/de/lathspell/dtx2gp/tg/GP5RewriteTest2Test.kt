package DTX

import de.lathspell.dtx2gp.tg.TGHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.test.assertEquals
import org.apache.commons.io.FileUtils
import org.herac.tuxguitar.io.gtp.*
import org.herac.tuxguitar.song.factory.TGFactory
import org.herac.tuxguitar.song.models.TGSong
import org.junit.Test

class GP5RewriteTest2Test {

    [Test]
    fun testRewriteTest2() {
        val fnameIn = "data/test2.gp5"
        val fnameOut = "data/test2-rewrite.gp5"
        val fnameOutDump = "data/test2-rewrite.dump"

        val tgFactory: TGFactory = TGFactory()
        val gtpEmptySettings: GTPSettings = GTPSettings()

        // Read original
        val fis: InputStream = FileInputStream(fnameIn)
        val gp5is: GP5InputStream = GP5InputStream(gtpEmptySettings)
        gp5is.init(tgFactory, fis);
        val gp5song : TGSong = gp5is.readSong()!!

        // Rewrite unmodified
        val fos: FileOutputStream = FileOutputStream(fnameOut)
        val gp5os: GP5OutputStream = GP5OutputStream(gtpEmptySettings)
        gp5os.init(tgFactory, fos);
        gp5os.writeSong(gp5song)
        FileUtils.writeStringToFile(File(fnameOutDump), TGHelper().dumpTGSong(gp5song))

        // Reread previously written song
        val fis2 = FileInputStream(fnameOut)
        val gp5is2 = GP5InputStream(gtpEmptySettings)
        gp5is2.init(tgFactory, fis2)
        val gp5song2 = gp5is2.readSong()!!

        // Compare
        val dump1 = TGHelper().dumpTGSong(gp5song)
        val dump2 = TGHelper().dumpTGSong(gp5song2)
        assertEquals(dump1, dump2)
        // println(TGHelper().dumpCompactTGSong(gp5song))
        // println(dump1)
    }
}
