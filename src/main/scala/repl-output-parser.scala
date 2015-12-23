package wrap

// A highlevel parse of a REPL output expression, capturing the main places where we might break the line
sealed trait ReplExpr
case class Import(value: String) extends ReplExpr
case class CaseClass(name: String) extends ReplExpr
case class Method(name: String, theType: Type) extends ReplExpr
case class Value(name: String, theType: Type, value: String) extends ReplExpr

case class Comment(value: String) extends ReplExpr
case class Error(value: String) extends ReplExpr

// For breaking up the types and values, possibly consider parsing them in more detail
case class Type(name: String, params: List[Type] = Nil)


object ReplOutputParser {

  import atto._, Atto._

  def parse(line: String): ParseResult[ReplExpr] = {
    val withoutComment = line.dropWhile(_ == '/').trim
    parser.parseOnly(withoutComment)
  }

  lazy val parser: Parser[ReplExpr] =
    definitions | imports | value | method

  lazy val ws = whitespace
  lazy val str = stringOf(anyChar)
  lazy val symbol = stringOf(letterOrDigit) // wrong, surely?

  lazy val typeDef: Parser[Type] = for {
    name   <- typePath
    params <- opt(typeParams)
  } yield Type(name, params.toList.flatten)

  lazy val typeParams: Parser[List[Type]] = for {
    _     <- char('[')
    types <- sepBy(typeDef, char(','))
    _     <- char(']')
  } yield types

  lazy val typePath: Parser[String] = for {
    name <- stringOf(letterOrDigit | char('.') | char('#'))
    withs <- opt(withClause)
  } yield withs match {
    case None    => name
    case Some(w) => s"$name$w"
  }

  lazy val withClause: Parser[String] = for {
    _    <- string(" with ")
    name <- typePath
  } yield s" with $name"

  lazy val method: Parser[Method] = for {
    name    <- symbol
    _       <- string(":")
    _       <- ws
    theType <- typeDef
  } yield Method(name, theType)

  lazy val value: Parser[Value] = for {
    name    <- symbol
    _       <- string(":")
    _       <- ws
    theType <- typeDef
    _       <- ws
    _       <- string("=")
    _       <- ws
    v       <- str
  } yield Value(name, theType, v)

  lazy val imports: Parser[Import] =
    string("import") ~> ws ~> str -| Import

  lazy val definitions: Parser[CaseClass] =
    string("defined") ~> ws ~> string("class") ~> ws ~> str -| CaseClass
}