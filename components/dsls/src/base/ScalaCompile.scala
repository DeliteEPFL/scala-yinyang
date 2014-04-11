package base

import ch.epfl.yinyang.api._
import scala.tools.nsc._
import scala.tools.nsc.util._
import scala.tools.nsc.reporters._
import scala.tools.nsc.io._
import scala.reflect.internal.util.AbstractFileClassLoader
import reflect.runtime.universe.TypeTag
import reflect.ClassTag
import scala.reflect.internal.util.BatchSourceFile
import scala.collection.immutable.Set

import java.io._

trait ScalaCompile { this: CodeGenerator =>

  var compiler: Global = _
  var reporter: ConsoleReporter = _
  val COLON = System getProperty "path.separator"

  def setupCompiler() = {
    val settings = new Settings()

    settings.classpath.value = this.getClass.getClassLoader match {
      case ctx: java.net.URLClassLoader => ctx.getURLs.map(_.getPath).mkString(COLON)
      case _                            => System.getProperty("java.class.path")
    }
    settings.bootclasspath.value = Predef.getClass.getClassLoader match {
      case ctx: java.net.URLClassLoader => ctx.getURLs.map(_.getPath).mkString(COLON)
      case _                            => System.getProperty("sun.boot.class.path")
    }

    settings.encoding.value = "UTF-8"
    settings.outdir.value = "."
    settings.extdirs.value = ""

    reporter = new ConsoleReporter(settings, null, new PrintWriter(System.out)) //writer
    compiler = new Global(settings, reporter)
  }

  var compileCount = 0

  var dumpGeneratedCode = false

  def compile[T: TypeTag, Ret](unstableHoleIds: Set[Int] = Set()): Ret = {
    if (this.compiler eq null)
      setupCompiler()

    val className = "staged$" + compileCount
    compileCount += 1

    val source = generateCode(className, unstableHoleIds)

    if (dumpGeneratedCode) println(source)

    val compiler = this.compiler
    val run = new compiler.Run

    val fileSystem = new VirtualDirectory("<vfs>", None)
    compiler.settings.outputDirs.setSingleOutput(fileSystem)
    //      compiler.genJVM.outputDir = fileSystem

    run.compileSources(List(new BatchSourceFile("<stdin>", source.toString)))
    reporter.printSummary()

    if (reporter.hasErrors)
      println("compilation: had errors")

    reporter.reset

    val parent = this.getClass.getClassLoader
    val loader = new AbstractFileClassLoader(fileSystem, this.getClass.getClassLoader)

    val cls: Class[_] = loader.loadClass(className)
    cls.getConstructor().newInstance().asInstanceOf[Ret]
  }

}