package DTX

import de.lathspell.tuxguitar.TGHelper
import java.io.File
import kotlin.test.assertFalse
import org.apache.commons.io.FileUtils
import org.herac.tuxguitar.song.factory.TGFactory
import org.herac.tuxguitar.song.managers.TGSongManager
import org.herac.tuxguitar.song.models.TGBeat
import org.herac.tuxguitar.song.models.TGChannel
import org.herac.tuxguitar.song.models.TGDuration
import org.herac.tuxguitar.song.models.TGMeasure
import org.herac.tuxguitar.song.models.TGNote
import org.herac.tuxguitar.song.models.TGSong
import org.herac.tuxguitar.song.models.TGTrack
import org.herac.tuxguitar.song.models.TGVoice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GP5WriteTest2Test {

    [Test]
    fun writeTest2() {
        // song
        val factory : TGFactory = TGFactory()
        val manager : TGSongManager = TGSongManager()
        val song : TGSong = manager.newSong()!!
        song.setArtist("Testartist2")
        song.setName("Testsong2")
        song.setAlbum("Testalbum2")

        // track
        assertEquals(1, song.countTracks())
        val track : TGTrack = song.getTrack(0)!!
        track.setName("Drumkit")
        TGChannel.setPercussionChannel(track.getChannel())
        assertTrue(track.isPercussionTrack())

        // measure 0 is already present
        assertEquals(1, track.countMeasures())
        val m0 : TGMeasure = track.getMeasure(0)!!

        // measure 0
        assertEquals(1, m0.getNumber()) // Measure number counting from 1
        assertEquals(4, m0.getTimeSignature()?.getNumerator())
        assertEquals(4, m0.getTimeSignature()?.getDenominator()?.getValue())
        assertEquals(0, m0.countBeats())
        m0.getTempo()?.setValue(123);

        // Measure 0, Beat 1
        val m0b0 : TGBeat = factory.newBeat()!!
        val m0b0v0 : TGVoice = factory.newVoice(0)!!
        val m0b0v1 : TGVoice = factory.newVoice(1)!!

        var m0b0v0n : TGNote = factory.newNote()!!
        m0b0v0n.setValue(42)
        m0b0v0n.setString(6)
        m0b0v0.addNote(m0b0v0n)

        var m0b0v1n : TGNote = factory.newNote()!!
        m0b0v1n.setValue(35)
        m0b0v1n.setString(6)
        m0b0v1.addNote(m0b0v1n)

        m0b0.setVoice(0, m0b0v0)
        m0b0.setVoice(1, m0b0v1)
        m0.addBeat(m0b0)

        // Measure 0, Beat 1
        val m0b1 : TGBeat = factory.newBeat()!!
        val m0b1v0 : TGVoice = factory.newVoice(0)!!
        val m0b1v1 : TGVoice = factory.newVoice(1)!!

        var m0b1v0n : TGNote = factory.newNote()!!
        m0b1v0n.setValue(42)
        m0b1v0n.setString(6)
        m0b1v0.addNote(m0b1v0n)

        var m0b1v1n : TGNote = factory.newNote()!!
        m0b1v1n.setValue(38)
        m0b1v1n.setString(6)
        m0b1v1.addNote(m0b1v1n)

        m0b1.setVoice(0, m0b1v0)
        m0b1.setVoice(1, m0b1v1)
        m0.addBeat(m0b1)

        // Measure 0, Beat 2
        val m0b2 : TGBeat = factory.newBeat()!!
        val m0b2v0 : TGVoice = factory.newVoice(0)!!
        val m0b2v1 : TGVoice = factory.newVoice(1)!!

        val halfDuration : TGDuration = factory.newDuration()!!
        halfDuration.setValue(TGDuration.HALF)
        m0b2v0.setDuration(halfDuration)
        m0b2v1.setDuration(halfDuration)
        m0b2v0.setEmpty(false)
        m0b2v1.setEmpty(false)
        assertFalse(m0b2v0.isEmpty())
        assertFalse(m0b2v1.isEmpty())
        assertTrue(m0b2v0.isRestVoice())
        assertTrue(m0b2v1.isRestVoice())

        m0b2.setVoice(0, m0b2v0)
        m0b2.setVoice(1, m0b2v1)
        assertTrue(m0b2.isRestBeat())
        m0.addBeat(m0b2)

        println(TGHelper().dumpCompactTGSong(song))
        println(TGHelper().dumpTGSong(song))
        TGHelper().writeGP5Song("data/test2.gp5", song)

        FileUtils.writeStringToFile(File("data/test2.dump"), TGHelper().dumpTGSong(song))
    }

}
