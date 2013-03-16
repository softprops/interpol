package interpol

import java.lang.reflect.{InvocationHandler, Method, Proxy}
import scala.util.control.Exception.catching

object Signals {
  def apply() = {
    catching(classOf[ClassNotFoundException]).opt {
      Class.forName("sun.misc.Signal")
      new Signals()
    }
  }
}

private [interpol] class Signals {
  private val handlerCls = Class.forName("sun.misc.SignalHandler")
  private val signalCls = Class.forName("sun.misc.Signal")
  private val handleMeth = signalCls.getMethod("handle", signalCls, handlerCls)
  private val nameMeth = signalCls.getMethod("getName")

  private def signalName(arg: Object) =
    nameMeth.invoke(arg).asInstanceOf[String]
  private def newInstance(signal: String) =
    signalCls.getConstructor(classOf[String])
             .newInstance(signal)
             .asInstanceOf[Object]
  def apply(signal: String, handler: String => Unit) {
    val prox = Proxy.newProxyInstance(
      handlerCls.getClassLoader,
      Array[Class[_]](handlerCls),
      new InvocationHandler {
        def invoke(proxy: Object, method: Method, args: Array[Object]) = {
          if (method.getName == handleMeth.getName) {
            handler(signalName(args(0)))
          }
          null
        }
      })
    handleMeth.invoke(null, newInstance(signal), prox)
  }
}

