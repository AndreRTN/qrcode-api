import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.scalatra.servlet.ScalatraListener
import servlets.QRCodeServlet

object QRCodeApiApp extends App {

  private val port = sys.env.getOrElse("PORT", "8080").toInt

  private val server = new Server(port)
  private val context = new ServletContextHandler(ServletContextHandler.SESSIONS)

  context.setContextPath("/")
  context.setResourceBase("src/main/webapp")
  context.setInitParameter(ScalatraListener.LifeCycleKey, "ScalatraBootstrap")
  context.addEventListener(new ScalatraListener)
  context.addServlet(classOf[DefaultServlet], "/")

  server.setHandler(context)

  println(s"Starting QRCode API server on port $port...")
  server.start()
  server.join()
}

class ScalatraBootstrap extends org.scalatra.LifeCycle {
  override def init(context: javax.servlet.ServletContext): Unit = {
    context.mount(new QRCodeServlet, "/api/qrcode/*")
  }
}