package interpol

import java.lang.reflect.{InvocationHandler, Method, Proxy}
import scala.util.control.Exception.catching

object Signals {
  def apply() = {
    catching(classOf[ClassNotFoundException]).opt {
      new Signals(Class.forName("sun.misc.SignalHandler"), Class.forName("sun.misc.SignalHandler"))
    }
  }
}

private [interpol] class Signaler(cls: Class[_], handlerCls: Class[_]) {
  private val handleMeth = cls.getMethod("handle", cls, handlerCls)
  private val nameMeth   = cls.getMethod("getName")
  val handle = handleMeth
  val name = nameMeth.getName
  def name(arg: Object) =
    nameMeth.invoke(arg).asInstanceOf[String]
  def instance(signal: String) =
    cls.getConstructor(classOf[String])
       .newInstance(signal)
       .asInstanceOf[Object]
}

private [interpol] class Signals(
  handlerCls: Class[_], signalCls: Class[_]) {
  val sig = new Signaler(signalCls, handlerCls)
  def apply(signal: String)(handler: String => Unit) {
    val prox = Proxy.newProxyInstance(
      handlerCls.getClassLoader,
      Array[Class[_]](handlerCls),
      new InvocationHandler {
        def invoke(proxy: Object, method: Method, args: Array[Object]) = {
          if (method.getName == sig.name) {
            handler(sig.name(args(0)))
          }
          null
        }
      })
    sig.handle.invoke(null, sig.instance(signal), prox)
  }
}

