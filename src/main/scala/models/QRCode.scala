package models
import java.time.LocalDateTime
import java.util.UUID

object QRCodeType extends Enumeration {
  type QRCodeType = Value
  val READ, GENERATED = Value
}

case class QRCode(
                   id: String = UUID.randomUUID().toString,
                   content: String,
                   imageBase64: String,
                   qrType: QRCodeType.QRCodeType,
                   createdAt: LocalDateTime = LocalDateTime.now()
                 )

case class QRCodeRequest(content: String)

case class QRCodeResponse(
                           id: String,
                           content: String,
                           imageBase64: String,
                           qrType: String,
                           createdAt: String
                         )

object QRCodeResponse {
  def fromQRCode(qr: QRCode): QRCodeResponse = QRCodeResponse(
    id = qr.id,
    content = qr.content,
    imageBase64 = qr.imageBase64,
    qrType = qr.qrType.toString,
    createdAt = qr.createdAt.toString
  )
}