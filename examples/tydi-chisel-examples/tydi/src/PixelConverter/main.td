package main; //package name (rather than the file name) determines the name in memory.

/*
 * The following code example is a simple RGB to gray converter and vice versa.
 * 
 * It is used to show the usage and representation of Union, Groups and templates for custom data types.
 */

clockdomain_value = "50MHz-1";

// Define an rgb
color_depth = 8;
// color_channel_t = Bit(color_depth);

Group Rgb<color_depth: int> {
  color_channel_t = Bit(color_depth);
  r: color_channel_t;
  g: color_channel_t;
  b: color_channel_t;
}

Union Color<color_depth: int> {
  rgb_t = Rgb<color_depth>;
  rgb: rgb_t;
  gray: rgb_t.color_channel_t; // it is only one channel
}


Group UInt<n: int> {
  value: Bit(n);
}

#This is a custom float representation#
Group Float<n_mantissa: int, n_exponent: int> {
  sign: Bit(1); // anonymous field, 0 is positive, 1 is negative
  mantissa: UInt<n_mantissa>;
  exponent: UInt<n_exponent>;
}

#A position with an x and y coordinated#
Group Pos {
  x: Float<23, 8>;
  y: Float<23, 8>;
}

#A Pixel with a position (x, y) and a color (rg) documentation#
Group Pixel {
  pos: Pos;
  color: Color<color_depth>;
}

// Stream characteristics
complexity_level: int = 1;
dimension: int = 2;
keep: bool = true;
direction: string = "Forward";
throughput: float = 1.;

pixel_stream = Stream(Pixel, c=complexity_level, t=throughput, r=direction, x=keep, d=dimension);

#RGB bypass streamlet documentation#
streamlet pixel_converter_interface {
  input: pixel_stream in / clockdomain_value;
  output: pixel_stream out /"100MHz";
}

// This module will convert a stream of RGB to a stream of gray and vice versa
#RGB bypass implement documentation#
impl pixel_converter of pixel_converter_interface {
  #Stream 1#
  self.input => self.output;
}
