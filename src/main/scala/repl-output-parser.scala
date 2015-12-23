package wrap

sealed trait ReplExpr
case class Import(value: String) extends ReplExpr
case class CaseClass(name: String) extends ReplExpr
case class Method(name: String, theType: String) extends ReplExpr
case class Value(name: String, theType: String, value: String) extends ReplExpr
case class Comment(value: String) extends ReplExpr

//case class Type(name: String, params: List[Type])

object ReplOutputParser {

  import atto._, Atto._

  lazy val ws = whitespace
  lazy val str = stringOf(anyChar)
  lazy val symbol = stringOf(letterOrDigit)
  lazy val typeDef = stringOf(letterOrDigit | char('.') | char(',') | char('#') | char('[') | char(']'))

  lazy val parser: Parser[ReplExpr] =
    definitions | imports | value | method

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


  def parse(line: String): ParseResult[ReplExpr] = {
    val withoutComment = line.dropWhile(_ == '/').trim
    parser.parseOnly(withoutComment)
  }


}