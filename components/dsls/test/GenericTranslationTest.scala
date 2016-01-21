package mpde.vector.test

import dsl.la.rep.VectorDSL
import org.scalatest._
import dsl.la._ //TODO:carefull, this also includes Vector[T] !!
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import reflect.runtime.universe._
import java.io.{ PrintStream, ByteArrayOutputStream }

import scala.virtualization.lms.common._

@RunWith(classOf[JUnitRunner])
class GenericTranslationSpec extends FlatSpec with ShouldMatchers {
  "Generic translation" should "work for val definitions" in {
    intercept[NotImplementedError] {
      la {
        val v = 1
        v
      }
    }
  }

  it should "work for vars" in {
    intercept[NotImplementedError] {
      la {
        var v = 1
        v = 2
        v
      }
    }
  }

  it should "work for lambdas" in {
    intercept[NotImplementedError] {
      la {
        val id = (x: Int) => x
        id
      }
    }
  }

  it should "work for application" in {
    intercept[NotImplementedError] {
      la {
        val id = (x: Int) => x
        id(1)
      }
    }
  }

  it should "work for all types of application" in {
    intercept[NotImplementedError] {
      la {
        val id0 = () => 1
        val id1 = (x: Int) => x
        val id2 = (x: Int, y: Int) => x + y
        id0()
        id1(1)
        id2(1, 2)
      }
    }
  }

  it should "work for AnyRef functions" in {
    intercept[NotImplementedError] {
      la {
        val id0 = 1
        val hash = 1.hashCode
        val hash2 = 2.hashCode
        hash != hash2
      }
    }
  }

  it should "work for locally defined functions" in {
    intercept[NotImplementedError] {
      val x: Int = la {
        def id[T](x: T): T = x
        def id2[T, U <: AnyRef](p1: T, p2: U): Unit = ()
        id2[Int, String](1, "string")
        id[Int](1)
      }
    }
  }

  it should "work for type aliases" in {
    intercept[NotImplementedError] {
      la {
        println("hello")
        type X = Int
        type Y = dsl.la.Vector[X]
        val x: Y = dsl.la.Vector(1, 2, 3)
        val ys = x dotProduct x
        val yp = x(1) * x(2)

        val z: dsl.la.Vector[Int] = dsl.la.Vector(1, 2, 3)
        val zz = z(1) * z(2)
        val w = dsl.la.Vector(1, 2, 3)
      }
    }
  }

  it should "interesting wrapping of VectorOps" in {
    //    intercept[NotImplementedError] { //TODO why does this not throw a NotImplementedError
    trait T extends VectorDSL {
      type R[+T] = T
      def m() =
        laDebug {
          val x = this.Vector(1, 2, 3)
          val ys = x dotProduct x
          val yp = x(1) * x(2)
        }
      val a = m()
    }
    //    }
  }

  it should "work with captured variables" in {
    val captured = 1
    val captured1 = 2
    val captured2 = 3
    intercept[NotImplementedError] {
      la {
        val x: Int = captured + captured1 + captured2
        x + 1
      }
    }
  }

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

  // records tests:

  // use scala-records: problem: inner macro gets expanded before YY
  it should "record test with scala-records" in {
    import records._ //use scala Records in shallow!
    intercept[NotImplementedError] {
      lmsDebug {
        val r = Record(h = 5)
      }
    }
  }

  // use Dynamic, problem: result is not typed and applyDynamicNamed(UNIT("apply"))(v: (String, Any)*)
  object Record extends Dynamic {
    def applyDynamicNamed(method: String)(v: (String, Any)*): Any = ??? //
  }
  it should "record test with Dynamic " in {
    intercept[NotImplementedError] {
      lmsDebug {
        val r = Record(h = 5)
      }
    }
  }

  it should "record test with RecordOps and Rep[T]=T" in {
    intercept[NotImplementedError] {
      trait T extends RecordOps {
        type Rep[T] = T
        lmsDebug {
          val r = Record(h = 5)
        }
      }
    }
  }

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
  //  it should "should work with scala collection" in {
  //    intercept[NotImplementedError] {
  //      boolDLMS {
  //        val l: List[Int] = scala.collection.immutable.List(1, 2, 3, 4)
  //        val a = l(1)
  //        val i = l.length
  //        //        val m = l.map(_ + 1)
  //      }
  //    }
  //  }
  //

  //  it should "work with complex numbers" in {
  //    intercept[NotImplementedError] {
  //      boolDLMS {
  //        val c = dsl.la.Complex(1, 2)
  //        val x = c conv c
  //        val y = c blop c
  //      }
  //    }
  //  }
  //
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
