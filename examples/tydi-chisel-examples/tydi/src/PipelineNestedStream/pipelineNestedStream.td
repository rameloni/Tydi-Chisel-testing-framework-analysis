package pipelineNestedStream;

use pipelineNestedStream_types;
use pipelineNestedGroup_types;
use pipelineSimple_types;

streamlet NonNegativeFilter_interface {
    std_in : pipelineNestedStream_types.NumberGroup_stream in;
    std_out : pipelineNestedStream_types.NumberGroup_stream out;
}
#This is a comment#
impl NonNegativeFilter of NonNegativeFilter_interface {}

streamlet Reducer_interface {
    std_in : pipelineNestedStream_types.NumberGroup_stream in;
    std_out : pipelineNestedStream_types.Stats_stream out;
}

impl Reducer of Reducer_interface {}

streamlet PipelineNestedStream_interface {
    std_in : pipelineNestedStream_types.NumberGroup_stream in;
    std_out : pipelineNestedStream_types.Stats_stream out;
}

impl PipelineNestedStream of PipelineNestedStream_interface {
    instance filter(NonNegativeFilter);
    instance reducer(Reducer);

    self.std_in => filter.std_in;
    filter.std_out => reducer.std_in;
    reducer.std_out => self.std_out;
}