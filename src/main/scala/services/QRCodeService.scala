package services

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.time.LocalDateTime
import java.util.Base64
import javax.imageio.ImageIO
import scala.collection.concurrent.TrieMap
import scala.util.{Failure, Success, Try}
import com.google.zxing._
import com.google.zxing.client.j2se.{BufferedImageLuminanceSource, MatrixToImageWriter}
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import models.{QRCode, QRCodeType}

class QRCodeService {
  private val storage = TrieMap[String, QRCode]()
  private val qrWriter = new QRCodeWriter()
  private val qrReader = new MultiFormatReader()

  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(_.toString)

  def generateQRCode(content: String, size: Int = 300): Try[QRCode] = Try {
    val bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, size, size)
    val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)

    val baos = new ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "PNG", baos)
    val imageBase64 = Base64.getEncoder.encodeToString(baos.toByteArray)

    val qrCode = QRCode(
      content = content,
      imageBase64 = imageBase64,
      qrType = QRCodeType.GENERATED
    )

    storage.put(qrCode.id, qrCode)
    qrCode
  }

  def readQRCode(imageBase64: String): Try[QRCode] = Try {
    val imageBytes = Base64.getDecoder.decode(imageBase64)
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes))

    val source = new BufferedImageLuminanceSource(bufferedImage)
    val bitmap = new BinaryBitmap(new HybridBinarizer(source))
    val result = qrReader.decode(bitmap)

    val qrCode = QRCode(
      content = result.getText,
      imageBase64 = imageBase64,
      qrType = QRCodeType.READ
    )

    storage.put(qrCode.id, qrCode)
    qrCode
  }

  def getQRCode(id: String): Option[QRCode] = storage.get(id)

  private def getAllQRCodes: List[QRCode] = storage.values.toList.sortBy(_.createdAt).reverse

  def getQRCodesByType(qrType: QRCodeType.QRCodeType): List[QRCode] =
    storage.values.filter(_.qrType == qrType).toList.sortBy(_.createdAt).reverse

  def deleteQRCode(id: String): Boolean = storage.remove(id).isDefined

  def getHistory: List[QRCode] = getAllQRCodes
}