//package dsl.lms.rep
//
//import org.scala_lang.virtualized.SourceContext //to get the implicit SourceContext
//import scala.virtualization.lms.common.{ Base => _, _ } //Base already exists
//
//trait LMSYY extends BaseExp with BaseYinYangManifest with VariablesExp with CodeGenerator { //mix-in CodeGenerator to check that option of YY
//implicit def implicitLift[T: Manifest]: LiftEvidence[T, Rep[T]] = new PolyLift[T]
//  class PolyLift[T: Manifest] extends LiftEvidence[T, Rep[T]] {
//    def lift(v: T) = unit(v) //Const(v)
//    def hole(tpe: TypeRep[T], symbolId: Int): Rep[T] = ??? //how to access holetable from here? or what else should we do?
//  }
//  def compilationVars(symbols: List[Symbol]): List[VarType] = Nil
//  //from codegenerator, if we arrive here at least the type checker passes
//  def compile[T: TypeRep, Ret](unstableHoleIds: Set[Int]): Ret = ???
//  def generateCode(className: String, unstableHoleIds: Set[Int]): String = ???
//  def interpret[T: TypeRep](params: Any*): T = ???
//}
//
//trait BooleanLMSDSL extends LMSYY with BooleanOpsExp with PrimitiveOpsExp with ImplicitOpsExp with ListOpsExp with TupleOpsExp with SeqOpsExp with CodeGenerator with FullyStaged with RecordOps {
//  implicit def repTo[T: Manifest](a: Rep[CI]) = new CIOpsCls(a)
//  object Complex {
//    def apply(i: Rep[Int], j: Rep[Int]): Rep[Complex] = ???
//  }
//  trait Complex =
//  implicit class ComplexOps(c: Rep[Complex]) {
//    def conv(rhs: Rep[Complex]): Rep[Complex] = ???
//  }
//  class CIOpsCls(i: Rep[CI]) {
//    def x() = i
//  }
//}
//
//trait TestDSL extends BooleanLMS with LiftBoolean with LiftNumeric {
//  def main() = {
//    def m()(implicit s: SourceContext) = s
//    val sc = m()
//    val x: Rep[Boolean] = true
//    val y = x || x
//    val a = List(1, 2, 3)
//    val b = a(1)
//
//    val lll: Rep[List[Int]] = List(unit(1), unit(2), unit(3), unit(4));
//    //    val r2 = Record(i = unit(34), s = unit("sdf"))
//
//    type A = Int
//    type B = Rep[A];
//    type C = Rep[B];
//    val i0: Rep[A] = unit(7)
//    //    //    val i00: Rep[Int] = unit(unit(7))
//    //    val i000: Rep[Rep[Int]] = unit(7)
//    //    //    val ii = i000 * i000 //fails!
//    //
//    val i1: C = unit(7)
//    val i2: Rep[B] = unit(4);
//    //    val i3 = (i0 * i1) + i2; //this obviously doesn't work
//
//    val i = 4
//    implicit def conv(i: Int) = new { def xx() = i }
//    i.xx()
//    implicit def c(r: Rep[Int]) = new { def a() = r + r }
//    val r: Rep[Int] = i
//    r.a()
//
//    val tup: Rep[(Int, Boolean)] = (unit(2), unit(true))
//    val t1 = tup._1
//
//    //    val a1: Rep[Tuple3[Boolean, Int, Boolean]] = (x, 4, y)
//    //    val b1 = a1._2
//    //    val c1 = (a1._1, a1._3)
//  }
//
//}