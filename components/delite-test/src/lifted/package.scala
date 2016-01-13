import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers.GenericTypeTransformer
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object lifted {
  def lift[T](block: => T): T = macro implementations.lift[T]
  //  def liftDebug[T](block: => T): T = macro implementations.liftDebug[T]
  def optiML[T](block: => T): T = macro implementations.optiML[T]
  //  def optiMLDebug[T](block: => T): T = macro implementations.optiMLDebug[T]
  //  def optiGraph[T](block: => T): T = macro implementations.optiGraph[T]
  //  def optiGraphDebug[T](block: => T): T = macro implementations.optiGraphDebug[T]
  //  def optiGraphAnalysis[T](block: => T): T = macro implementations.optiGraphAnalysis[T]

  object implementations {
    def lift[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "lifted.ScalaDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "shallow" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeVal" -> true,
          "debug" -> 0,
          "featureAnalysing" -> false,
          "ascriptionTransforming" -> false))(block)

    //    def liftDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    //      new YYTransformer[c.type, T](c, "lifted.ScalaDSL",
    //        shallow = false,
    //        debug = true,
    //        rep = true)(block) {}
    //

    def optiML[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "lifted.OptiML",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
        },
        None, None,
        Map(
          "shallow" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeVal" -> true,
          "debug" -> 3,
          "featureAnalysing" -> false,
          "ascriptionTransforming" -> false))(block)

    //
    //    def optiMLDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    //      new YYTransformer[c.type, T](c, "lifted.OptiML",
    //        shallow = false,
    //        debug = true,
    //        rep = true,
    //        mainMethod = "mainDelite")(block) {}
    //
    //    def optiGraph[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    //      new YYTransformer[c.type, T](c, "lifted.OptiGraph",
    //        shallow = false,
    //        debug = false,
    //        rep = true,
    //        mainMethod = "mainDelite")(block) {}
    //
    //    def optiGraphDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    //      new YYTransformer[c.type, T](c, "lifted.OptiGraph",
    //        shallow = false,
    //        debug = true,
    //        rep = true,
    //        mainMethod = "mainDelite")(block) {}
    //
    //    def optiGraphAnalysis[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    //      new YYTransformer[c.type, T](c, "lifted.OptiGraph",
    //        shallow = true,
    //        debug = false,
    //        rep = true,
    //        mainMethod = "mainDelite")(block) {}
  }

}
