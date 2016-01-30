package dsl.lms

/**
 * Created by cedricbastin on 22/01/16.
 */
//object Complex {
//  def apply(i: Int, j: Int): Complex = ??? //object Complex {def apply(i:rep[Int], j:Rep[Int]) is in trait BooleanLMS
//}
//trait Complex { //mpde.vector.test
//  def conv(c: Complex): Complex = ???
//  def blop(c: Complex): Int = ???
//}
//a case class can simulate both the trait and the object.apply
case class Complex(i: Int, j: Int) { //mpde.vector.test
  def conv(c: Complex): Complex = ???
  def blop(c: Complex): Int = ???
}

case class Vector[T](length: Int) {
//  def +(y: Vector[A]) = vectorPlus(x, y)
//  //    def +(y: Rep[A])(implicit n: Numeric[A], o: Overloaded1) = vectorPlusScalar(x, y)
//  def *(y: Rep[A]) = scalarTimes(x, y)
//  def sum = vectorSum(x)
//  def filter(pred: Rep[A] => Rep[Boolean]) = vectorFilter(x, pred)
//  def length = vectorLength(x)
//  def apply(idx: Rep[Int]) = vectorApply(x, idx)
//  def pprint = vectorPrint(x)
}
