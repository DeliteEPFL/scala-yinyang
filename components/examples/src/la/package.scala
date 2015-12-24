package dsl

import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object la {

  def la[T](block: => T): T = macro implementations.liftRep[T]
  def laDebug[T](block: => T): T = macro implementations.liftRepDebug[T]
  def boolS[T](block: => T): T = macro implementations.boolShallow[T]
  def boolD[T](block: => T): T = macro implementations.boolDeep[T]
  def intD[T](block: => T): T = macro implementations.intYY[T]
  def boolDLMS[T](block: => T): T = macro implementations.boolLMS[T]
  def typeOnly[T](block: => T): T = macro implementation.typeTranform[T]

  object implementations {
    def liftRep[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.VectorDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeValDef" -> true,
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)
    }

    def liftRepDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.VectorDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeValDef" -> true,
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)

    def boolShallow[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
        },
        None, None,
        Map(
          "direct" -> true,
          "virtualizeFunctions" -> true,
          "virtualizeValDef" -> true,
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)

    def intYY[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.IntDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> false,
          "virtualizeValDef" -> false,
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)
    }

    def boolDeep[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> false,
          "virtualizeValDef" -> false,
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)
    }

    //    def boolLMS[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
    //
    //      object O extends TypeTreeTransformation {
    //
    //        //private type Ctx = this.Ctx
    //        val typeTransformer = new GenericTypeTransformer[Ctx](c) {
    //          override val IRType = "Rep"
    //        }
    //        new TypeTreeTransformer().transform(block.tree.asInstanceOf[this.c.universe.Tree])
    //      }
    //      ???
    //    }

    def boolLMS[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanLMS", //don't use version with Reify
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> false,
          "virtualizeValDef" -> false,
          "debug" -> 3,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)
    }
  }

  object implementation { //extends scala.reflect.api.Trees
    //abstract
    class TTTransformer[C <: Context, T](val c: C) extends TypeTreeTransformation {
      type Ctx = C
      import c.universe._ //Tree, TypTree, ValDef, showRaw...
      val name = "dsl.la.rep.BooleanDSL" //TODO: make meaningful
      override val className = name
      val debugLevel = 3
      val typeTransformer =
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep" //just forward to Rep in BooleanDSL
          override val className = name
          //          override def constructRepTree(ctx: TypeContext, inType: Type): Tree = {
          //            log("GenericTypeTransformer")
          //            super.constructRepTree(ctx, inType)
          //          }
        }
      def apply[T](block: c.Expr[T]): c.Expr[T] = {
        println("NOW applying the typer")
        println("NOW before typing : " + showCode(block.tree))
        val t =
          //          ValDef(Modifiers(), TermName("x"), TypeTree(), Literal(Constant(7)))
          block.tree
        //          Block(List(ValDef(Modifiers(), TermName("x"), TypeTree(), Literal(Constant(true)))), Literal(Constant(())))
        //          ValDef(Modifiers(), TermName("x"), TypeTree(), Literal(Constant(true)))
        //Literal(Constant(()))
        val y = c.Expr(TypeTreeTransformer(t))
        println("NOW done typing : " + showCode(y.tree))
        val classUID = 1 //Set to something meaningful
        val classNameG = s"generated$$${className.filter(_ != '.') + classUID}"
        log(s"class generated? $y")
        c.Expr(q"""
          $y //code wrapped in DSL class with name className
          val inst = new ${Ident(TypeName(classNameG))}
          inst.main() //.asInstanceOf[Nothing]
        """)
      }
      //import typeTransformer._ //already imported in super
      //def transform(t: c.Tree) = typeTransformer transform (typeTransformer.other, inType)
    }

    def typeTranform[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      val x = new TTTransformer[c.type, T](c)(block)
      println(s"TYPETRANSFORM: $x")
      x
    }
  }
}
