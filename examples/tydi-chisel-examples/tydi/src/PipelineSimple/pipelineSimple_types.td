package pipelineSimple_types;

UInt_64_t = Bit(64); // UInt<64>: unsigned 64-bit integer
SInt_64_t = Bit(64); // SInt<64>: signed 64-bit integer

#A composite type (like a struct) that contains a value associated with a timestamp#
Group NumberGroup {
    value: SInt_64_t;
    time: UInt_64_t; 
}

#A composite type (like a struct) that represents the stats of the implemented algorithm#
Group Stats {
    average: UInt_64_t;
    sum: UInt_64_t;
    max: UInt_64_t;
    min: UInt_64_t;
}

// Define the stream types
NumberGroup_stream = Stream(NumberGroup, t=1.0, d=1, c=1);
Stats_stream = Stream(Stats, t=1.0, d=1, c=1);