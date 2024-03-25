import fs2.Stream
@main def hello(): Unit =
  println("Hello world!")
  val s1 = Stream(1,2,3)
  print(s1.toList)



  