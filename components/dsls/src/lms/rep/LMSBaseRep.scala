package dsl.lms.rep

import ch.epfl.yinyang.api.{ _ } //
import org.scala_lang.virtualized.virtualize

import scala.virtualization.lms.common._ //different from base.Base
import org.scala_lang.virtualized.SourceContext

trait LMSYY extends BaseExp with BaseYinYangManifest with VariablesExp with CodeGenerator with FullyStaged { //mix-in CodeGenerator to check that option of YY
  implicit def implicitLift[T: Manifest]: LiftEvidence[T, Rep[T]] = new PolyLift[T]
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
  type Complex = dsl.lms.Complex
  implicit class ComplexOps(c: Rep[Complex]) {
    def conv(rhs: Rep[Complex]): Rep[Complex] = ???
    def blop(rhs: Rep[Complex]): Rep[Int] = ???
  }
}

class PrimitiveDSL extends LMSYY with PrimitiveOpsExp

class VectorDSL extends LMSYY with VectorOpsExp

trait VectorOps extends Variables {
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

trait VectorOpsExp extends VectorOps with PrimitiveOpsExp {

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

//trait VectorOps extends Base {
//  trait Vector[T]
//  object Vector {
//    def apply[T:Manifest](xs: Rep[T]*) = vector_new(xs)
//  }
//  def vector_new[T:Manifest](xs: Seq[Rep[T]]):Rep[Vector[T]] //rather use different constructors
//  def vzeros(n: Rep[Int]): Rep[Vector[Double]]
//  def vliteral[T:Manifest](a: List[Rep[T]]): Rep[Vector[T]]
//  def vapply[T:Manifest](a: Rep[Vector[T]], x: Rep[Int]): Rep[T]
//  def vupdate[T:Manifest](a: Rep[Vector[T]], x: Rep[Int], y: Rep[T]): Rep[Unit]
//  def vlength[T:Manifest](a: Rep[Vector[T]]): Rep[Int]
//  def vplus(a: Rep[Vector[Double]], b: Rep[Vector[Double]]): Rep[Vector[Double]]
//}
//
//trait VectorOpsExp extends VectorOps with EffectExp {
//
//  case class VectorZeros(n: Rep[Int]) extends Def[Vector[Double]]
//  case class VectorLiteral[T](a: List[Rep[T]]) extends Def[Vector[T]]
//  case class VectorApply[T](a: Rep[Vector[T]], x: Rep[Int]) extends Def[T]
//  case class VectorUpdate[T](a: Rep[Vector[T]], x: Rep[Int], y: Rep[T]) extends Def[Unit]
//  case class VectorLength[T](a: Rep[Vector[T]]) extends Def[Int]
//  case class VectorPlus(a: Rep[Vector[Double]], b: Rep[Vector[Double]]) extends Def[Vector[Double]]
//
//  def vzeros(n: Rep[Int]): Rep[Vector[Double]] = VectorZeros(n)
//  def vliteral[T:Manifest](a: List[Rep[T]]): Rep[Vector[T]] = VectorLiteral(a)
//  def vplus(a: Rep[Vector[Double]], b: Rep[Vector[Double]]): Rep[Vector[Double]] = VectorPlus(a,b)
//  def vapply[T:Manifest](a: Rep[Vector[T]], x: Rep[Int]): Rep[T] = VectorApply(a,x)
//  def vupdate[T:Manifest](a: Rep[Vector[T]], x: Rep[Int], y: Rep[T]): Rep[Unit] = VectorUpdate(a,x,y)
//  def vlength[T:Manifest](a: Rep[Vector[T]]): Rep[Int] = VectorLength(a)
//
//  override def mirror[A:Manifest](e: Def[A], f: Transformer)(implicit pos: SourceContext): Exp[A] = (e match {
//    case VectorZeros(n) => vzeros(f(n))
//    case VectorLiteral(a) => vliteral(f(a))
//    case VectorApply(a,x) => vapply(f(a),f(x))(mtype(manifest[A]))
//    case VectorUpdate(a,x,y) => vupdate(f(a),f(x),f(y))
//    case VectorLength(a) => vlength(f(a))
//    case VectorPlus(a, b) => vplus(f(a),f(b))
//    case _ => super.mirror(e,f)
//  }).asInstanceOf[Exp[A]] // why??
//
//  override def mirrorDef[A:Manifest](e: Def[A], f: Transformer)(implicit pos: SourceContext): Def[A] = (e match {
//    case VectorZeros(n) => VectorZeros(f(n))
//    case VectorLiteral(a) => VectorLiteral(f(a))
//    case VectorApply(a,x) => VectorApply(f(a),f(x))
//    case VectorUpdate(a,x,y) => VectorUpdate(f(a),f(x),f(y))
//    case VectorLength(a) => VectorLength(f(a))
//    case VectorPlus(a, b) => VectorPlus(f(a),f(b))
//    case _ => super.mirrorDef(e,f)
//  }).asInstanceOf[Def[A]] // why??
//
//  override def aliasSyms(e: Any): List[Sym[Any]] = e match {
//    case _ => super.aliasSyms(e)
//  }
//
//  override def containSyms(e: Any): List[Sym[Any]] = e match {
//    case VectorLiteral(as) => syms(as)
//    case _ => super.containSyms(e)
//  }
//
//  override def extractSyms(e: Any): List[Sym[Any]] = e match {
//    case VectorApply(a,x) => syms(a)
//    case _ => super.extractSyms(e)
//  }
//
//  override def copySyms(e: Any): List[Sym[Any]] = e match {
//    case _ => super.copySyms(e)
//  }
//}