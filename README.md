# interpol

Interprocess communication for scala.

## Usage

```scala
interpol.Signals()("HUP") {
  case _ => reload()
}
```


## resources

[signals](http://www.cs.utah.edu/dept/old/texinfo/glibc-manual-0.02/library_21.html)
[signals in java](http://ringlord.com/dl/Signals-in-Java.pdf)

Doug Tangren (softprops) 2013
