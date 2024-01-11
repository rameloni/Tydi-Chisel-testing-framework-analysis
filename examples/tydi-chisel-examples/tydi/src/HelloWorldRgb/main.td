package main; //package name (rather than the file name) determines the name in memory.

clockdomain_value = "50MHz-1";

// Define an rgb
color_depth = 8;
color_channel_type = Bit(color_depth);
Group Rgb {
  r: color_channel_type;
  g: color_channel_type;
  b: color_channel_type;
}

// Stream characteristics
complexity_level: int = 1;
dimension: int = 2;
keep: bool = true;
direction: string = "Forward";
throughput: float = 1.;

stream_type = Stream(Rgb, u=Rgb, c=complexity_level, t=throughput, r=direction, x=keep, d=dimension);
stream2_type = Stream(Rgb, user_type=Null, complexity=1, throughput=2., direction="Reverse", keep=true, dimension=1);

#RGB bypass streamlet documentation#
streamlet rgb_bypass {
  input: stream_type in / clockdomain_value;
  output: stream_type out /"100MHz";
  input2: stream2_type in;
  output2: stream2_type out;
}

#RGB bypass implement documentation#
impl helloworld_rgb of rgb_bypass {
  #Stream 1#
  self.input => self.output;
  #Stream 2#
  self.input2 => self.output2;
}
