package rosscala

package object message {


  trait ROSData[T] {
    def _TYPE: String
    def _DEFINITION: String
  }



}
