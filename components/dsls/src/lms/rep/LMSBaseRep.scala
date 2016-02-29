package dsl.lms.rep

import ch.epfl.yinyang.api.{ _ } //
import org.scala_lang.virtualized.virtualize

import scala.virtualization.lms.common._ //different from base.Base
import org.scala_lang.virtualized.SourceContext

trait LMSYY extends BaseExp with BaseYinYangManifest with VariablesExp with CodeGenerator with FullyStaged { //mix-in CodeGenerator to check that option of YY
  implicit def implicitLift[T: Manifest]: LiftEvidence[T, Rep[T]] = new PolyLift[T]
  //  implicit def LiftInt = new PolyLift[Int]
  //  implicit def LiftUnit = new PolyLift[Unit]
  //  implicit def LiftBoolean = new PolyLift[Boolean]
  //  implicit def LiftString = new PolyLift[String]
  //  implicit def LiftDouble = new PolyLift[Double]

  //implicit
  class PolyLift[T: Manifest] extends LiftEvidence[T, Rep[T]] {
    def lift(v: T) = unit(v) //Const(v)
    def hole(tpe: TypeRep[T], symbolId: Int): Rep[T] = ??? //how to access holetable from here? or what else should we do?
  }
  //  def compilationVars(symbols: List[Symbol]): List[VarType] = Nil
  //from codegenerator, if we arrive here at least the type checker passes
  def compile[T: TypeRep, Ret](unstableHoleIds: Set[Int]): Ret = ???
  def generateCode(className: String, unstableHoleIds: Set[Int]): String = ???
  def interpret[T: TypeRep](params: Any*): T = ???
}

trait BooleanLMS extends LMSYY with BooleanOpsExp with PrimitiveOpsExp with ImplicitOpsExp with ListOpsExp with TupleOpsExp with SeqOpsExp with HashMapOpsExp with RecordOps {
  //  implicit def repTo[T: Manifest](a: Rep[CI]) = new CIOpsCls(a)
  object Complex {
    def apply(i: Rep[Int], j: Rep[Int]): Rep[Complex] = ???
  }

  object Tuple2 {
    def apply[A: Manifest, B: Manifest](a: Rep[A], b: Rep[B])(implicit pos: SourceContext) = make_tuple2(a, b)
  }

  object Map {
    def apply[K: Manifest, V: Manifest](elems: Rep[(K, V)]*)(implicit pos: SourceContext) = {
      val map = hashmap_new[K, V]()
      for (elem <- elems)
        hashmap_update(map, elem._1, elem._2)
      map
    }

  }

  type Complex = dsl.lms.Complex
  implicit class ComplexOps(c: Rep[Complex]) {
    def conv(rhs: Rep[Complex]): Rep[Complex] = ???
    def blop(rhs: Rep[Complex]): Rep[Int] = ???
    def impli(i: Rep[Int])(implicit j: Int) = i
    def mani[A: Manifest](a: Rep[A]) = ???
    def implVal[A](a: Rep[A])(implicit m: Manifest[A]) = ???
  }
}

class PrimitiveDSL extends LMSYY with PrimitiveOpsExp

class VectorDSL extends LMSYY with VectorOpsExp

trait VectorOps extends Variables with PrimitiveOps with LiftPrimitives with ImplicitOps {
  trait Vector[A] //extends DeliteCollection[A]

  //syntax
  object Vector {
    def apply[A: Manifest](length: Rep[Int]) = vectorNew(length)
  }

  implicit class VectorOpsCls[A: Manifest](x: Rep[Vector[A]]) {
    def +(y: Rep[Vector[A]]) = vectorPlus(x, y)
    //    def +(y: Rep[A])(implicit n: Numeric[A], o: Overloaded1) = vectorPlusScalar(x, y)
    def *(y: Rep[A]) = scalarTimes(x, y)
    def sum = vectorSum(x)
    def filter(pred: Rep[A] => Rep[Boolean]) = vectorFilter(x, pred)
    def length = vectorLength(x)
    def apply(idx: Rep[Int]) = vectorApply(x, idx)
    def pprint = vectorPrint(x)
  }

  //operations
  def vectorNew[A: Manifest](length: Rep[Int]): Rep[Vector[A]]
  def vectorPlus[A: Manifest](x: Rep[Vector[A]], y: Rep[Vector[A]]): Rep[Vector[A]]
  def vectorPlusScalar[A: Manifest](x: Rep[Vector[A]], y: Rep[A]): Rep[Vector[A]]
  def scalarTimes[A: Manifest](x: Rep[Vector[A]], y: Rep[A]): Rep[Vector[A]]
  def vectorSum[A: Manifest](x: Rep[Vector[A]]): Rep[A]
  def vectorFilter[A: Manifest](x: Rep[Vector[A]], pred: Rep[A] => Rep[Boolean]): Rep[Vector[A]]
  def vectorLength[A: Manifest](x: Rep[Vector[A]]): Rep[Int]
  def vectorApply[A: Manifest](x: Rep[Vector[A]], idx: Rep[Int]): Rep[A]
  def vectorPrint[A: Manifest](x: Rep[Vector[A]]): Rep[Unit]
}

trait VectorOpsExp extends VariablesExp with VectorOps with PrimitiveOpsExp with ImplicitOpsExp {

  def vectorNew[A: Manifest](length: Exp[Int]) = ???

  private def infix_data[A: Manifest](x: Exp[Vector[A]]) = ???

  def vectorLength[A: Manifest](x: Exp[Vector[A]]) = ???

  def vectorApply[A: Manifest](x: Exp[Vector[A]], idx: Exp[Int]) = ???

  def vectorPlus[A: Manifest](x: Exp[Vector[A]], y: Exp[Vector[A]]) = ???

  def vectorPlusScalar[A: Manifest](x: Exp[Vector[A]], y: Exp[A]) = ???

  def scalarTimes[A: Manifest](x: Exp[Vector[A]], y: Exp[A]) = ???

  def vectorSum[A: Manifest](x: Exp[Vector[A]]) = ???

  def vectorFilter[A: Manifest](x: Exp[Vector[A]], pred: Exp[A] => Exp[Boolean]) = ???

  def vectorPrint[A: Manifest](x: Exp[Vector[A]]) = ???

}

object TestDSL extends App with BooleanLMS with LiftBoolean with LiftNumeric with MiscOpsExp { //lifts are normally done by YinYang!
  import org.scala_lang.virtualized.SourceContext //implicit SourceContext
  def m()(implicit s: SourceContext) = s
  val sc = m()
  val x: Rep[Boolean] = true
  val y = x || x
  val a = List(1, 2, 3)
  val b = a(1)

  val rec = Record(test = unit("rsdagf")) //this is how it should work!

  val lll: Rep[List[Int]] = List(unit(1), unit(2), unit(3), unit(4))
  val llr: Rep[List[Int]] = List(1, 2, 3, 4)
  val lli = llr.map(_ => unit("string"))
  reifyEffects {
    println(lli) //doesn't want to show output
  }
  //    val r2 = Record(i = unit(34), s = unit("sdf"))

  //TODO: why does this even work???
  type A = Int
  type B = Rep[A];
  type C = Rep[B];
  val i0: Rep[A] = unit(7)
  //    //    val i00: Rep[Int] = unit(unit(7))
  //    val i000: Rep[Rep[Int]] = unit(7)
  //    //    val ii = i000 * i000 //fails!
  //
  val i1: C = unit(7)
  val i2: Rep[B] = unit(4);
  //  val i3 = (i1 * i0) + i2; //this obviously doesn't work

  val i = 4
  implicit def conv(i: Int) = new { def xx() = i }
  i.xx()
  implicit def c(r: Rep[Int]) = new { def a() = r + r }
  val r: Rep[Int] = i
  r.a()

  val tup: Rep[(Int, Boolean)] = (unit(2), unit(true))
  val t1 = tup._1

  //    val a1: Rep[Tuple3[Boolean, Int, Boolean]] = (x, 4, y)
  //    val b1 = a1._2
  //    val c1 = (a1._1, a1._3)
}
