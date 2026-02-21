package org.a
package utils.result

import io.circe.{Decoder, Encoder, HCursor, Json}
import sttp.tapir.*

final case class Result[T](
    code: Int,
    msg: String,
    data: Option[T] = None
)

object Result {
  // 序列化
  given [T](using e: Encoder[T]): Encoder[Result[T]] =
    (a: Result[T]) =>
      Json.obj(
        ("code", Json.fromInt(a.code)),
        ("msg", Json.fromString(a.msg)),
        (
          "data",
          a.data match {
            case Some(value) => e(value)
            case None        => Json.Null
          }
        )
      )

  // 反序列化
  given [T](using d: Decoder[T]): Decoder[Result[T]] =
    (c: HCursor) =>
      for {
        code <- c.downField("code").as[Int]
        msg <- c.downField("msg").as[String]
        data <- c.downField("data").as[Option[T]]
      } yield Result(code, msg, data)

  inline given [T: Schema]: Schema[Result[T]] =
    Schema
      .derived[Result[T]]
      .description("统一结果返回体")
      .modify(_.code)(_.description("响应码"))
      .modify(_.msg)(_.description("响应消息"))
      .modify(_.data)(_.description("结果数据"))

  def apply[T](data: T): Result[T] = Result(StatusCode.Success.code, StatusCode.Success.message, Some(data))
  def apply[T](statusCode: StatusCode, data: T): Result[T] = Result(statusCode.code, statusCode.message, Some(data))
  def status(statusCode: StatusCode): Result[StatusCode] = Result(statusCode.code, statusCode.message, None)
}
