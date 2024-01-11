package pipelineNestedGroup_types;

use pipelineSimple_types;

// UInt_64_t = Bit(64); // UInt<64>
// SInt_64_t = Bit(64); // SInt<64>
SInt_64_t = pipelineSimple_types.SInt_64_t;
UInt_64_t = pipelineSimple_types.UInt_64_t;

NumberGroup = pipelineSimple_types.NumberGroup;

Month_t = Bit(4); // UInt<4>
Day_t = Bit(5);   // UInt<5>
Year_t = Bit(12); // UInt<12>
UTC_t = Bit(5);   // SInt<5>

#A DateTimeGroup represents a specific date time#
Group DateTimeGroup {
    month: Month_t;
    day: Day_t;
    year: Year_t;
    utc: UTC_t;
}

#A NestedNumberGroup represents a number group with a nested date time group#
Group NestedNumberGroup {
    value: SInt_64_t;
    time: UInt_64_t;
    
    numberGroup: NumberGroup; // nested group
    date: DateTimeGroup; // nested group
}

// Group Stats {
//     average: UInt_64_t;
//     sum: UInt_64_t;
//     max: UInt_64_t;
//     min: UInt_64_t;
// }

// A stream that contains nested groups
NestedNumberGroup_stream = Stream(NestedNumberGroup, t=1.0, d=1, c=1);
// Stats_stream = Stream(Stats, t=1.0, d=1, c=1);