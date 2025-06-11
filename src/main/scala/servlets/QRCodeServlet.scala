package servlets

import models.{QRCode, QRCodeRequest, QRCodeResponse, QRCodeType}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods._
import org.scalatra._
import org.scalatra.json._
import services.QRCodeService
import scala.util.{Failure, Success, Try}

class QRCodeServlet extends ScalatraServlet with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  private val qrService = new QRCodeService()

  before() {
    contentType = "application/json"
  }

  post("/generate") {
    Try(parse(request.body).extract[QRCodeRequest]) match {
      case Success(request) =>
        qrService.generateQRCode(request.content) match {
          case Success(qrCode) =>
            status = 201
            QRCodeResponse.fromQRCode(qrCode)
          case Failure(ex) =>
            status = 500
            Map("error" -> s"Failed to generate QR code: ${ex.getMessage}")
        }
      case Failure(_) =>
        status = 400
        Map("error" -> "Invalid request body")
    }
  }

  post("/read") {
    Try(parse(request.body).extract[Map[String, String]]) match {
      case Success(body) =>
        body.get("imageBase64") match {
          case Some(image) =>
            qrService.readQRCode(image) match {
              case Success(qrCode) =>
                status = 200
                QRCodeResponse.fromQRCode(qrCode)
              case Failure(ex) =>
                status = 400
                Map("error" -> s"Failed to read QR code: ${ex.getMessage}")
            }
          case None =>
            status = 400
            Map("error" -> "imageBase64 field is required")
        }
      case Failure(_) =>
        status = 400
        Map("error" -> "Invalid request body")
    }
  }

  get("/download/:id") {
    qrService.getQRCode(params("id")) match {
      case Some(qrCode) =>
        contentType = "image/png"
        response.setHeader("Content-Disposition", s"attachment; filename=qrcode-${qrCode.id}.png")
        java.util.Base64.getDecoder.decode(qrCode.imageBase64)
      case None =>
        contentType = "application/json"
        status = 404
        Map("error" -> "QR code not found")
    }
  }

  get("/history") {
    val qrType = params.get("type").map(_.toUpperCase)

    qrType match {
      case Some("READ") =>
        qrService.getQRCodesByType(QRCodeType.READ).map(QRCodeResponse.fromQRCode)
      case Some("GENERATED") =>
        qrService.getQRCodesByType(QRCodeType.GENERATED).map(QRCodeResponse.fromQRCode)
      case Some(_) =>
        status = 400
        Map("error" -> "Invalid type parameter. Use 'read' or 'generated'")
      case None =>
        qrService.getHistory.map(QRCodeResponse.fromQRCode)
    }
  }

  get("/:id") {
    qrService.getQRCode(params("id")) match {
      case Some(qrCode) => QRCodeResponse.fromQRCode(qrCode)
      case None =>
        status = 404
        Map("error" -> "QR code not found")
    }
  }

  delete("/:id") {
    if (qrService.deleteQRCode(params("id"))) {
      status = 204
      ""
    } else {
      status = 404
      Map("error" -> "QR code not found")
    }
  }
}