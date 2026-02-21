package org.a
package utils.route

import scala.quoted.*
import scala.annotation.tailrec

inline def initControllers[T]: List[T] = ${ initControllersImpl[T] }

def initControllersImpl[T: Type](using Quotes): Expr[List[T]] = {
  import quotes.reflect.*

  val traitSymbol = TypeRepr.of[T].typeSymbol

  if !traitSymbol.flags.is(Flags.Trait) then
    report.errorAndAbort(s"${traitSymbol.name} is not a trait.")

  def collect(
    sym: Symbol,
    visited: Set[String] = Set.empty
  ): List[Symbol] = {
    if sym.isNoSymbol || visited.contains(sym.fullName) then Nil
    else {
      val children = sym.declarations.filter { child =>
        (child.isPackageDef && (
          !sym.fullName.equals("scala") &&
            !sym.fullName.startsWith("scala") &&
            !sym.fullName.startsWith("java.") &&
            !sym.fullName.startsWith("dotty.")
        )) || child.isClassDef || child.isTypeDef
      }
      sym +: children.flatMap { child =>
        collect(child, visited + sym.fullName)
      }
    }
  }

  @tailrec
  def findRootOwner(sym: Symbol): Symbol = {
    if sym.isPackageDef || sym.owner.isNoSymbol then sym
    else findRootOwner(sym.owner)
  }

  def implementsTrait(classSymbol: Symbol, traitSymbol: Symbol): Boolean = {
    try {
      val classType = classSymbol.typeRef
      val traitType = traitSymbol.typeRef
      classType <:< traitType
    } catch case _: Exception => false
  }

  // 收集所有符号 -> 去重 -> 过滤出符合条件的实现类
  val allSymbols = collect(findRootOwner(Symbol.spliceOwner)).distinct.collect {
    case classSymbol
      if classSymbol.isClassDef
        && !classSymbol.flags.is(Flags.Trait) 
        && classSymbol != traitSymbol
        && !classSymbol.name.startsWith("<")
        && classSymbol.name.nonEmpty 
        && implementsTrait(classSymbol, traitSymbol) =>
      classSymbol
  }

  val instances = allSymbols.map { cls =>
    New(TypeIdent(cls))
      .select(cls.primaryConstructor)
      .appliedToNone 
      .asExprOf[T]
  }

  Expr.ofList(instances)
}
