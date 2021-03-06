package ch.epfl.yinyang
package transformers

import ch.epfl.yinyang._
import ch.epfl.yinyang.transformers._
import scala.reflect.macros.blackbox.Context
import language.experimental.macros
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Converts captured variables to holes, which will be passed to the generated
 * code at runtime as arguments to the apply method. Exposes all holes in the
 * holeTable, which maps from holeIds to symbolIds.
 *
 * Features covered are:
 *   - identifiers -> `hole[T](classTag[T], holeId)`
 *   - fields (TODO)
 *   - no parameter methods (TODO)
 *   - no parameter functions (TODO)
 */
trait HoleTransformation extends MacroModule with TransformationUtils {

  import c.universe._

  /** SymbolIds indexed by holeIds. */
  val holeTable = new ArrayBuffer[Int]

  object HoleTransformer {
    def apply(toHoles: List[Symbol] = Nil, className: String)(tree: Tree) = {
      val t = new HoleTransformer(toHoles map symbolId).transform(tree)
      log("holeTransformed (transforming " + toHoles + "): " + code(t), 2)
      log("holeTable (holeId -> symbolId): " + holeTable, 2)
      t
    }
  }

  /**
   * Transforms all identifiers with symbolIds in `toHoles` to
   * `hole[T](classTag[T], holeId)` and builds the holeTable mapping from
   * holeIds to symbolIds.
   */
  //  TODO this translation needs to happen properly. Absence of explicit lifting should be handled by config. What should
  //  be converted to lift, and what to hole, and what should be rejected.
  //   [[x.y.z.k.this.f]] if f is a field ~> hole(x.y.z.k.this.f)
  //   [[x.y.z.k.this.m(...,...,...)]] if m is a method ~> hole(x.y.z.k.this.m) // (config) to reject or accept?
  class HoleTransformer(toHoles: List[Int]) extends Transformer {

    override def transform(tree: Tree): Tree = tree match {

      case i @ Ident(s) if toHoles contains symbolId(i.symbol) =>
        val sId = symbolId(i.symbol)
        val index = if (holeTable.contains(sId)) holeTable.indexOf(sId)
        else {
          holeTable += symbolId(i.symbol)
          holeTable.size - 1
        }
        q"$$hole($index, $$tpe[${i.tpe.dealias.widen}])"

      case Ident(s) => // TODO move this to a separate phase.
        Ident(s)

      case _ =>
        super.transform(tree)
    }
  }
}
