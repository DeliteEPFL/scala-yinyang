package mpde.vector.test

import dsl.la.rep.BooleanLMS
import org.scalatest._
import dsl.la._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import reflect.runtime.universe._
import java.io.{ PrintStream, ByteArrayOutputStream }
import scala.reflect.macros.Universe

@RunWith(classOf[JUnitRunner])
class GenericTranslationSpec extends FlatSpec with ShouldMatchers {
  //  "Test typeOnly" should "be transformed" in {
  //    intercept[NotImplementedError] {
  //      val res = typeOnly {
  //        val i = 7
  //        val j = 4
  //        i + j
  //      }
  //      println(s"typeonlytest $res")
  //    }
  //  }

  "Boolean translation" should "be shallow" in {
    val x = boolS {
      true || false
    }
    //assert(x)
  }

  //expected: Boolean, found: R[Boolean]
  //  it should "be deep" in {
  //    intercept[NotImplementedError] {
  //      val u: Any = boolD {
  //        val x: Boolean = false
  //        val y: Boolean = true
  //        x || y
  //        true
  //      }
  //    }
  //  }

  //  it should "be int deep" in {
  //    intercept[NotImplementedError] {
  //      intD {
  //        45
  //      }
  //    }
  //  }

  import org.scala_lang.virtualized.SourceContext
  val srcc: SourceContext = SourceContext.m

  it should "be very deep" in {
    //    intercept[NotImplementedError] {
    class C extends BooleanLMS {
      //    type Rep[+T] = T
      def main(): Any = {
        //    boolDLMS {
        //implicit def m(b: Boolean) = { x: Any => true } //any to boolean
        //      implicit def m(b: Boolean) = new BL(b)
        //      val xx = C(7)
        val x: Rep[Boolean] = false
        val y: Exp[Boolean] = x.||(false) //we need to make sure that a SourceContext is available here!
        sys.error("RESULT: " + y )
      }
    }
    new C {}.main()
  }

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
      val res: Int = la {
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
        type X = Int
        type Y = dsl.la.Vector[X]
        val vecRes: Y = dsl.la.Vector(1, 2, 3)
        vecRes
      }
    }
  }

  it should "work with captured variables" in {
    val captured = 1
    val captured1 = 2
    val captured2 = 3
    intercept[NotImplementedError] {
      la {
        val resX: Int = captured + captured1 + captured2
        resX + 1
      }
    }
  }
}
