package pipelineNestedStream_types;

// Import the other packages
use pipelineNestedGroup_types;
use pipelineSimple_types;

#The Number Group from PipelineNestedGroup extended with a nested stream#
Group NumberGroupWithString {
    numberNested: pipelineNestedGroup_types.NumberGroup;
    date: pipelineNestedGroup_types.DateTimeGroup;
    my_custom_string: Char_stream;
}

// Define a char stream
Char_t = Bit(8); // UInt<8>
Char_stream = Stream(Char_t, t=1.0, d=1, c=1);

#The Stats from PipelineSimple extended with a nested stream#
Group StatsWithString {
    stats: pipelineSimple_types.Stats;
    my_custom_string: Char_stream;
}

NumberGroup_stream = Stream(NumberGroupWithString, t=1.0, d=1, c=1);
Stats_stream = Stream(StatsWithString, t=1.0, d=1, c=1);