import javax.inject.Inject
import play.api.http._
import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router

class XFlowRequestHandler @Inject()(
  config       : play.api.Configuration,
  errorHandler : HttpErrorHandler,
  configuration: HttpConfiguration,
  filters      : HttpFilters,
  router       : Router
) extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  val X_FLOW_ID_HEADER = "X-Flow-ID"

  def getXFlow(request: RequestHeader): RequestHeader = {
    val appName = config.getString("application.name").getOrElse("scala-template")
    val xflow = request.headers.get(X_FLOW_ID_HEADER).getOrElse(appName + "-" + java.util.UUID.randomUUID.toString)
    request.copy(headers = request.headers.add((X_FLOW_ID_HEADER, xflow)))
  }

  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) =
    super.handlerForRequest(getXFlow(request))
}

