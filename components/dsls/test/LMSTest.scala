package mpde.vector.test

import dsl.lms._
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import reflect.runtime.universe._
import java.io.{ PrintStream, ByteArrayOutputStream }

import scala.virtualization.lms.common.RecordOps

@RunWith(classOf[JUnitRunner])
class LMSTest extends FlatSpec with ShouldMatchers {

  //LMS TESTS

  //simple types work

  //Booleans work
  it should "work with booleans" in {
    intercept[NotImplementedError] {
      lms {
        val x = true
        val y = !x
        val z = x && x
      }
    }
  }

  //integers work
  it should "work with integers" in {
    intercept[NotImplementedError] {
      lms {
        val x = 3 //test
        val y = 4
        val z = x + y
        val w = 5 + y
      }
    }
  }

  //TODO why doesn this work?
  it should "nested Rep's ??? " in {
    intercept[NotImplementedError] {
      lms {
        type A = Int //will be Rep[Int]
        type B = A //will be Rep[A] = Rep[Rep[Int]] ???
        type C = B //will be Rep[B] = Rep[Rep[Rep[Int]]] ???
        //        type R[T] = T
        val c: C = 7
        val b: B = 4
        val cc: B = (c * b) + 3
      }
    }
  }

  it should "work on primitiveOps " in {
    intercept[NotImplementedError] {
      primitiveDsl {
        val x = 3 + 9
        val s = "segrf"
        val d = 6.0 * 8
      }
    }
  }

  //  trait T extends dsl.lms.rep.VectorOpsExp {
  //    val v0: T.this.Rep[T.this.Vector[Int]] = T.this.Vector.apply[Int](4)(scala.reflect.ManifestFactory.Int);
  //    val v1: T.this.Rep[T.this.Vector[Int]] = T.this.Vector.apply[Int](3)(scala.reflect.ManifestFactory.Int);
  //    val v2: T.this.Rep[T.this.Vector[Int]] = T.this.VectorOpsCls[Int](v0)(scala.reflect.ManifestFactory.Int).+(v1);
  //  }
  //
  //println("TEST: " + new T {}.v2)
  //this doesn't work because YinYang will also wrap Manifests!
  //  it should "work on primitiveOps " in {
  //    trait TT extends dsl.lms.rep.VectorOps {
  //      type Rep[T] = T
  //      intercept[NotImplementedError] {
  //        vectorDsl {
  //          val v0 = Vector[Int](4)
  //          val v1 = Vector[Int](3)
  //          val v2 = v0 + v1
  //        }
  //      }
  //    }
  //  }

  // records tests:

  //  // use scala-records: problem: inner macro gets expanded before YY
  //  it should "record test with scala-records" in {
  //    import records._ //use scala Records in shallow!
  //    intercept[NotImplementedError] {
  //      lmsDebug {
  //        val r = Record(h = 5)
  //      }
  //    }
  //  }
  //
  // use Dynamic, problem: result is not typed and applyDynamicNamed(UNIT("apply"))(v: (String, Any)*)
  import scala.language.dynamics
  object Record extends Dynamic {
    def applyDynamicNamed(method: String)(v: (String, Any)*): Any = ??? //
  }
  //  object Record {
  //    def apply(h: Int) = ???
  //  }
  //  class Test extends RecordOps {
  //    val r = Record(h = 5)
  //  }

  //  it should "record test with Dynamic " in {
  //    intercept[NotImplementedError] {
  //      lmsDebug {
  //        val r = Record(h = 5)
  //        //val r = Record(h = 5)
  //      }
  //    }
  //  }
  //
  //  it should "record test with RecordOps and Rep[T]=T" in {
  //    intercept[NotImplementedError] {
  //      trait T extends RecordOps {
  //        type Rep[T] = T
  //        lmsDebug {
  //          val r = Record(h = 5)
  //        }
  //      }
  //    }
  //  }

  //  //tuples don't work
  //  it should "work with tuples" in {
  //    intercept[NotImplementedError] {
  //      boolDLMS {
  //        val i = 1
  //        val x = "edfsc"
  //        val z = 3.2
  //        val a = (x, i, z)
  //        val b = a._2
  //        val c = (a._1, a._3)
  //        val l = new List(1, 2, 3)
  //        val elem = l(1)
  //      }
  //    }
  //  }
  //
  //  case class MyC(u: Int)
  //  class MyClass(val u: Int)
  //  it should "test build in functionality" in {
  //    intercept[NotImplementedError] {
  //      boolDLMS {
  //        //this also throws NotImplementedError, interesting
  //      }
  //    }
  //  }
  //
  //  it should "how records should be usable be the DSL user" in {
  //    intercept[NotImplementedError] {
  //      //this is how I imagine a DSL user to use YinYang
  //      //but the Record macro is of course not in Scope because it is part of the deep version including Rep[]
  //      boolDLMS {
  //        val r1 = Record(x = 1, y = 2)
  //        val r2 = Record(x = 3, y = 4)
  //        val r = Record(x = r1.x * r2.x, y = r1.y * r2.y)
  //      }
  //    }
  //  }
  //
  //  it should "not break implicit classes" in {
  //    intercept[Throwable] {
  //      boolDLMS {
  //        //how I tried to make it work with LMS records but YinYang breaks it again
  //        trait T extends RecordOps with LiftAll with BaseExp {
  //          //        type R = Record {
  //          //          val s: String
  //          //          val i: Int
  //          //        }
  //          //        val i = 0
  //          //        val s = "sdf"
  //          //          val a1: Rep[Int] = 9
  //          //          val a2: Rep[Int] = 2
  //          val r = Record(i = 1, s = "sdf")
  //        }
  //      }
  //    }
  //  }
  //
  it should "should work with scala collection" in {
    intercept[NotImplementedError] {
      lmsDebug {
        val l: List[Int] = scala.collection.immutable.List(1, 2, 3, 4)
        val a = l(1)
        val i = l.length
        val h = l.head
                val m = l.map(_ => "string")
      }
    }
  }

  it should "work with complex numbers" in {
    intercept[NotImplementedError] {
      implicit val i = 9
      lms {
        val c = dsl.lms.Complex(1, 2)
        val x = c conv c
        val y = c blop c
        //        val bb = c.mani("Hello")
        //        val cc = c.implVal(23.34)
        //        val aa = c.impli(3)
      }
    }
  }

  //  it should "not break implicit methods" in {
  //    intercept[NotImplementedError] {
  //      boolDLMS {
  //        class C2(val i: Int) {
  //          def c2() = i * i * i;
  //        }
  //        implicit def conv(i: Int) = new {
  //          //anonymous class
  //          def c2() = i
  //        }
  //        val i = 4
  //        i.c2()
  //      }
  //    }
  //  }
  //
  //  implicit class C(i: Int) {
  //    def x() = i
  //  }
  //
  //  it should "work with backed in implicits?" in {
  //    boolDLMS {
  //      val i = 1
  //      i.x()
  //    }
  //  }
  //
  //  it should "work with scala collections" in {
  //    boolDLMS {
  //      val f = scala.collection.immutable.List(1, 2, 3, 4) //How can this be forwarded to  object Seq {def apply[A:Manifest](xs: Rep[A]*)} ??
  //      val g = f(2) //SeqOps!
  //      val g2 = f.head //ListOps
  //      val j = f.tail
  //      val h = f.map(_ + 1)
  //    }
  //  }
}
