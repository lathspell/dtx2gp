package de.lathspell.tuxguitar

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.ArrayList
import java.util.HashMap
import java.util.Map
import kotlin.test.assertTrue
import org.apache.commons.lang.StringUtils
import org.herac.tuxguitar.io.gtp.*
import org.herac.tuxguitar.io.tg.TGInputStream
import org.herac.tuxguitar.io.tg.TGOutputStream
import org.herac.tuxguitar.song.factory.TGFactory
import org.herac.tuxguitar.song.models.TGBeat
import org.herac.tuxguitar.song.models.TGDivisionType
import org.herac.tuxguitar.song.models.TGDuration
import org.herac.tuxguitar.song.models.TGMeasure
import org.herac.tuxguitar.song.models.TGMeasureHeader
import org.herac.tuxguitar.song.models.TGNote
import org.herac.tuxguitar.song.models.TGSong
import org.herac.tuxguitar.song.models.TGTrack
import org.herac.tuxguitar.song.models.TGVoice

class TGHelper {

    public fun readGP5Song(fname: String): TGSong {
        val gp5settings: GTPSettings = GTPSettings()
        val gp5is: GP5InputStream = GP5InputStream(gp5settings)

        val factory: TGFactory = TGFactory()
        val fis: InputStream = FileInputStream(fname)
        gp5is.init(factory, fis);
        assertTrue(gp5is.isSupportedVersion())

        return gp5is.readSong()!!
    }

    public fun writeGP5Song(fname: String, song: TGSong) {
        val fos: FileOutputStream = FileOutputStream(fname)
        val gp5factory: TGFactory = TGFactory()
        val gp5settings: GTPSettings = GTPSettings()
        val gp5os: GP5OutputStream = GP5OutputStream(gp5settings)
        gp5os.init(gp5factory, fos);
        gp5os.writeSong(song)
    }

    public fun readTGSong(fname: String): TGSong {
        val tgis: TGInputStream = TGInputStream()

        val factory: TGFactory = TGFactory()
        val fis: InputStream = FileInputStream(fname)
        tgis.init(factory, fis);
        assertTrue(tgis.isSupportedVersion())

        return tgis.readSong()!!
    }

    public fun writeTGSong(fname: String, song: TGSong) {
        val fos: FileOutputStream = FileOutputStream(fname)
        val tgfactory: TGFactory = TGFactory()
        val tgos: TGOutputStream = TGOutputStream()
        tgos.init(tgfactory, fos);
        tgos.writeSong(song)
    }

    public fun dumpCompactTGSong(song: TGSong): String {
        var s = String.format("%s - %s (Album: %s)\n",
                song.getArtist(),
                song.getName(),
                song.getAlbum(),
                song.getComments())!!;

        for (trackNo in 0..song.countTracks() - 1 ) {
            s += dumpCompactTGTrack(song.getTrack(trackNo)!!)
        }

        return s
    }

    public fun dumpCompactTGTrack(track: TGTrack): String {
        var s = String.format("Track %d \"%s\" %s\n",
                track.getNumber(),
                track.getName(),
                if (track.isPercussionTrack()) "(percussion)" else "")!!;

        for (measureNo in 0..track.countMeasures() - 1) {
            s += dumpCompactTGMeasure(track.getMeasure(measureNo)!!)
        }

        return s
    }

    public fun dumpCompactTGMeasure(measure: TGMeasure): String {
        /* COMPILER ERROR!

        org.jetbrains.jet.codegen.CompilationException: Back-end (JVM) Internal error: wrong code generatedjava.lang.ArrayIndexOutOfBoundsException 0
        Cause: 0
        File being compiled and position: (98,6) in /srv/home/james/workspace/kotlin_dtx/tuxguitar/src/TGHelper.kt
        The root cause was thrown at: null:-1
        at org.jetbrains.jet.codegen.FunctionCodegen.endVisit(FunctionCodegen.java:333)
        at org.jetbrains.jet.codegen.FunctionCodegen.generatedMethod(FunctionCodegen.java:315)
        at org.jetbrains.jet.codegen.FunctionCodegen.generateMethod(FunctionCodegen.java:75)
        at org.jetbrains.jet.codegen.FunctionCodegen.gen(FunctionCodegen.java:65)
        at org.jetbrains.jet.codegen.MemberCodegen.generateFunctionOrProperty(MemberCodegen.java:45)
        at org.jetbrains.jet.codegen.ClassBodyCodegen.generateDeclaration(ClassBodyCodegen.java:88)
        at org.jetbrains.jet.codegen.ImplementationBodyCodegen.generateDeclaration(ImplementationBodyCodegen.java:882)
        at org.jetbrains.jet.codegen.ClassBodyCodegen.generateClassBody(ClassBodyCodegen.java:80)
        at org.jetbrains.jet.codegen.ClassBodyCodegen.generate(ClassBodyCodegen.java:63)
        at org.jetbrains.jet.codegen.ClassCodegen.generateImplementation(ClassCodegen.java:78)
        at org.jetbrains.jet.codegen.ClassCodegen.generate(ClassCodegen.java:68)
        at org.jetbrains.jet.codegen.NamespaceCodegen.generate(NamespaceCodegen.java:147)
        at org.jetbrains.jet.codegen.NamespaceCodegen.generate(NamespaceCodegen.java:109)
        at org.jetbrains.jet.codegen.GenerationState.generateNamespace(GenerationState.java:180)
        at org.jetbrains.jet.codegen.GenerationState.compileCorrectFiles(GenerationState.java:156)
        at org.jetbrains.jet.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.generate(KotlinToJVMBytecodeCompiler.java:279)
        at org.jetbrains.jet.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.analyzeAndGenerate(KotlinToJVMBytecodeCompiler.java:232)
        at org.jetbrains.jet.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.analyzeAndGenerate(KotlinToJVMBytecodeCompiler.java:216)
        at org.jetbrains.jet.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileModule(KotlinToJVMBytecodeCompiler.java:80)
        at org.jetbrains.jet.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileModules(KotlinToJVMBytecodeCompiler.java:101)
        at org.jetbrains.jet.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.java:117)
        at org.jetbrains.jet.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.java:49)
        at org.jetbrains.jet.cli.common.CLICompiler.exec(CLICompiler.java:117)
        at org.jetbrains.jet.cli.jvm.K2JVMCompiler.exec(K2JVMCompiler.java:188)
        at org.jetbrains.jet.cli.jvm.K2JVMCompiler.exec(K2JVMCompiler.java:49)
        at org.jetbrains.jet.cli.common.CLICompiler.exec(CLICompiler.java:47)
        at sun.reflect.GeneratedMethodAccessor155.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:616)
        at org.jetbrains.jet.plugin.compiler.CompilerUtils.invokeExecMethod(CompilerUtils.java:175)
        at org.jetbrains.jet.plugin.compiler.JetCompiler.execInProcess(JetCompiler.java:248)
        at org.jetbrains.jet.plugin.compiler.JetCompiler.access$000(JetCompiler.java:54)
        at org.jetbrains.jet.plugin.compiler.JetCompiler$1.invoke(JetCompiler.java:235)
        at org.jetbrains.jet.plugin.compiler.JetCompiler$1.invoke(JetCompiler.java:232)
        at org.jetbrains.jet.plugin.compiler.CompilerUtils.outputCompilerMessagesAndHandleExitCode(CompilerUtils.java:314)
        at org.jetbrains.jet.plugin.compiler.JetCompiler.runInProcess(JetCompiler.java:232)
        at org.jetbrains.jet.plugin.compiler.JetCompiler.runCompiler(JetCompiler.java:139)
        at org.jetbrains.jet.plugin.compiler.JetCompiler.doCompile(JetCompiler.java:127)
        at org.jetbrains.jet.plugin.compiler.JetCompiler.compile(JetCompiler.java:102)
        at com.intellij.compiler.impl.CompileDriver.compileSources(CompileDriver.java:1931)
        at com.intellij.compiler.impl.CompileDriver.translate(CompileDriver.java:1254)
        at com.intellij.compiler.impl.CompileDriver.doCompile(CompileDriver.java:986)
        at com.intellij.compiler.impl.CompileDriver.doCompile(CompileDriver.java:747)
        at com.intellij.compiler.impl.CompileDriver.access$1000(CompileDriver.java:104)
        at com.intellij.compiler.impl.CompileDriver$8.run(CompileDriver.java:665)
        at com.intellij.compiler.progress.CompilerTask.run(CompilerTask.java:155)
        at com.intellij.openapi.progress.impl.ProgressManagerImpl$TaskRunnable.run(ProgressManagerImpl.java:469)
        at com.intellij.openapi.progress.impl.ProgressManagerImpl$2.run(ProgressManagerImpl.java:178)
        at com.intellij.openapi.progress.impl.ProgressManagerImpl.executeProcessUnderProgress(ProgressManagerImpl.java:218)
        at com.intellij.openapi.progress.impl.ProgressManagerImpl.runProcess(ProgressManagerImpl.java:169)
        at com.intellij.openapi.progress.impl.ProgressManagerImpl$8.run(ProgressManagerImpl.java:378)
        at com.intellij.openapi.application.impl.ApplicationImpl$6.run(ApplicationImpl.java:434)
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:471)
        at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:334)
        at java.util.concurrent.FutureTask.run(FutureTask.java:166)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1110)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:603)
        at java.lang.Thread.run(Thread.java:679)
        at com.intellij.openapi.application.impl.ApplicationImpl$1$1.run(ApplicationImpl.java:145)
        Caused by: java.lang.ArrayIndexOutOfBoundsException: 0
        at org.jetbrains.jet.internal.org.objectweb.asm.Frame.a(Unknown Source)
        at org.jetbrains.jet.internal.org.objectweb.asm.Frame.a(Unknown Source)
        at org.jetbrains.jet.internal.org.objectweb.asm.MethodWriter.visitMaxs(Unknown Source)
        at org.jetbrains.jet.codegen.FunctionCodegen.endVisit(FunctionCodegen.java:327)
        ... 58 more
        */
        /*
        val timesig = measure.getTimeSignature()!!

        var s = String.format("  Measure %d (%d/%d %dbpm%s)\n",
                measure.getNumber(),
                timesig.getNumerator(),
                timesig.getDenominator()?.getValue(),
                measure.getTempo()?.getValue(),
                if (measure.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE) " triplet" else ""
        )!!

        // They can indeed be unordered!
        val orderedBeats : Map<Long,TGBeat> = TreeMap<Long,TGBeat>()
        for (beatNo in 0..measure.countBeats() - 1) {
            val beat : TGBeat = measure.getBeat(beatNo)!!
            orderedBeats.put(beat.getStart(), beat)
        }

        for (beat in orderedBeats.values()) {
            s += dumpCompactTGBeat(beat)
        }

        // Sanity Checks
        if (measure.countBeats() > 0) {
            val expectedTime: Double = Double.valueOf(timesig.getNumerator().toString())!!.div(Double.valueOf(timesig.getDenominator()?.getValue().toString())!!)
            var v0time: Double = 0.0
            var v1time: Double = 0.0
            for (beatNo in 0..measure.countBeats() - 1) {
                val beat: TGBeat = measure.getBeat(beatNo)!!
                val v0: TGVoice? = beat.getVoice(0)
                if (v0 != null) v0time = v0time.plus(1.0.div(v0.getDuration()!!.getValue()))
                val v1: TGVoice? = beat.getVoice(1)
                if (v1 != null) v1time = v1time.plus(1.0.div(v1.getDuration()!!.getValue()))
            }
            if (expectedTime != v0time || expectedTime != v1time) {
                // println("WARN: Timing error: expected=$expectedTime got v0=$v0time and v1=$v1time. So far:\n" + s + "\n")
                // doesn't work well with rests
            }
        }

        return s
        */ return ""
    }

    public fun dumpCompactTGBeat(beat: TGBeat): String {
        var s = String.format("    Beat %6d: ", beat.getStart())!!

        for (voiceNo in 0..beat.countVoices() - 1) {
            s += String.format("%-16s", dumpCompactTGVoice(beat.getVoice(voiceNo)!!))
        }

        s += "\n"

        return s
    }

    public fun dumpCompactTGVoice(voice: TGVoice): String {
        val durationMap: Map<Int, String> = HashMap<Int, String>()
        durationMap.put(1, "1") // \uD834\uDD5D
        durationMap.put(2, "2") // \uD834\uDD5E
        durationMap.put(4, "\u2669")
        durationMap.put(8, "8") // \uD834\uDD60
        durationMap.put(16, "\u266C") // \uD834\uDD61
        val duration: TGDuration = voice.getDuration()!!
        var s: String = String.format("(%s) ", durationMap.get(duration.getValue()))!!

        if (voice.isEmpty()) {
            if (!voice.isRestVoice()) throw Exception("Empty voice but not RestVoice?")
            return s + "-"
        }

        if (voice.isRestVoice()) {
            return s + "rest"
        }

        var l = ArrayList<String>()
        for (noteNo in 0..voice.countNotes() - 1) {
            l.add(dumpCompactTGNote(voice.getNote(noteNo)!!))
        }
        s += StringUtils.join(l, ",")

        return s
    }

    public fun dumpCompactTGNote(note: TGNote): String {
        var s: String = note.getValue().toString()
        return s
    }


    public fun dumpTGSong(song: TGSong): String {
        var s = "Name: " + song.getName() + "\n" +
        "Album: " + song.getAlbum() + "\n" +
        "Artist: " + song.getArtist() + "\n" +
        "Comment: " + song.getComments() + "\n" +
        "\n"
        for (trackNo in 0..song.countTracks() - 1 ) {
            s += "Track #$trackNo:\n" + dumpTGTrack(song.getTrack(trackNo)!!)
        }
        return s
    }

    public fun dumpTGTrack(track: TGTrack): String {
        var s = "  Name: " + track.getName() + "\n" +
        "  number: " + track.getNumber() + "\n" +
        "  offset: " + track.getOffset() + "\n" +
        "  isPercussion: " + track.isPercussionTrack() + "\n" +
        "  isMute: " + track.isMute() + "\n" +
        "  countMeasures: " + track.countMeasures() + "\n" +
        "\n"

        for (measureNo in 0..track.countMeasures() - 1) {
            s += "  Measure #$measureNo:\n" + dumpTGMeasure(track.getMeasure(measureNo)!!)
        }

        return s
    }

    public fun dumpTGMeasure(measure: TGMeasure): String {
        val timesig = measure.getTimeSignature()!!
        var s =
        "    Clef: " + measure.getClef() + "\n" +
        "    KeySig: " + measure.getKeySignature() + "\n" +
        "    Start: " + measure.getStart() + "\n" +
        "    Length: " + measure.getLength() + "\n" +
        "    Timing: " + timesig.getNumerator() + "/" + timesig.getDenominator()?.getValue() + "\n" +
        "    Tempo: " + measure.getTempo()?.getValue() + " bpm\n" +
        "    isTripletFeel: " + (measure.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE) + "\n" +
        "    countBeats: " + measure.countBeats() + "\n" +
        "\n"

        for (beatNo in 0..measure.countBeats() - 1) {
            s += "    Beat #$beatNo:\n" + dumpTGBeat(measure.getBeat(beatNo)!!)
        }

        return s
    }

    public fun dumpTGBeat(beat: TGBeat): String {
        var s =
        "      Text: " + beat.getText() + "\n" +
        "      start: " + beat.getStart() + "\n" +
        "      isRest: " + beat.isRestBeat() + "\n" +
        "      isText: " + beat.isTextBeat() + "\n" +
        "      isChord: " + beat.isChordBeat() + "\n" +
        "      countVoices: " + beat.countVoices() + "\n" +
        "      Chord Name: " + beat.getChord()?.getName() + "\n" +
        "      Stroke Value: " + beat.getStroke()?.getValue() + "\n" +
        "\n"
        for (voiceNo in 0..beat.countVoices() - 1) {
            s += "      Voice #$voiceNo:\n" + dumpTGVoice(beat.getVoice(voiceNo)!!)
        }
        return s
    }

    public fun dumpTGVoice(voice: TGVoice): String {
        var s =
        "        index: " + voice.getIndex() + "\n" +
        "        Direction: " + voice.getDirection() + "\n" +
        "        Duration: " + dumpTGDuration(voice.getDuration()!!) + "\n" +
        "        isRest: " + voice.isRestVoice() + "\n" +
        "        isEmpty: " + voice.isEmpty() + "\n" +
        "        countNotes: " + voice.countNotes() + "\n" +
        "\n"
        for (noteNo in 0..voice.countNotes() - 1) {
            s += "        Note #$noteNo:\n" + dumpTGNote(voice.getNote(noteNo)!!)
        }
        return s
    }

    public fun dumpTGNote(note: TGNote): String {
        var s =
        "          String: " + note.getString() + "\n" +
        "          Value: " + note.getValue() + "\n" +
        "          Velocity: " + note.getVelocity() + "\n" +
        "          hasAnyEffect: " + note.getEffect()?.hasAnyEffect() + "\n" +
        "\n"
        return s
    }

    public fun dumpTGDuration(duration: TGDuration): String {
        return String.format("Duration(index=%s, time=%s, value=%s, division=%s)",
                duration.getIndex(),
                duration.getTime(),
                duration.getValue(),
                dumpTGDivision(duration.getDivision()!!))!!
    }

    public fun dumpTGDivision(division: TGDivisionType): String {
        return String.format("Division(enters=%d, times=%d)",
                division.getEnters(),
                division.getTimes())!!
    }
}
