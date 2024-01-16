package chiselexamples
package expose

import chisel3._
import chiseltest.experimental.expose

import scala.reflect.ClassTag

// A simple object to expose the ports of a Bundle or an Array of Bundles for testing
object ExposeBundle {
  def apply[T <: Bundle](bundle: => T, i: Int): T = {

	val out = IO(Output(bundle.cloneType))
	out.suggestName(bundle.parentModName + bundle.instanceName + "_" + i)
	out.getElements.zip(bundle.getElements).foreach { case (out, in) =>
	  out := expose(in)
	}
	out
  }

  def fromArray[T <: Bundle : ClassTag](
										 in: Array[T],
										 startIndex: Int = 0
									   ): Array[T] = {
	//	val rangenum = startIndex until in.length + startIndex
	//	in.zip(rangenum).map { case (elem, index) => apply(elem, index) }
	in.map(apply(_, startIndex))
  }

}

