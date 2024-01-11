package main; //package name (rather than the file name) determines the name in memory.

/*
 * The following code example is a simple RGB to gray converter and vice versa.
 * 
 * It is used to show the usage and representation of Union, Groups and templates for custom data types.
 */

SIZE_CONST: int = 10;
CUSTOM_DATA_T = Bit(10);

Group Array<size: int, data_t: type> {
  data_field: Bit(12);
  for i in [0, 1, 2, 3] {
    data: data_t;
  }
}


// Stream characteristics
complexity_level: int = 1;
dimension: int = 2;
keep: bool = true;
direction: string = "Forward";
throughput: float = 1.;

array_stream = Stream(Array<SIZE_CONST, CUSTOM_DATA_T>, c=complexity_level, t=throughput, r=direction, x=keep, d=dimension);

streamlet doubler_interface {
  input: array_stream in;
  output: array_stream out /"100MHz";
}

// This module will convert a stream of RGB to a stream of gray and vice versa
#A doubler doules all the values in the static array#
impl array_doubler of doubler_interface {
  #Stream 1#
  self.input => self.output;
}
