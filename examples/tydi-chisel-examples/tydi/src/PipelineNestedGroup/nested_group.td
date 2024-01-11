package pipelineNestedGroup;

use pipelineNestedGroup_types;
use pipelineSimple_types;

/**
 * This code implements the following spark code:
 *  
 *  Input: {timestamp, value, date}
 *  Output: {min_value, max_value, sum_value, avg_value}
 *  
 *  df.filter(col("value") >= 0 && col("date").utc() >= +1)
 *      .groupBy("timestamp")
 *      .agg( 
 *          min("value").as("min_value"), 
 *          max("value").as("max_value"), 
 *          sum("value").as("sum_value"), 
 *          avg("value").as("avg_value")
 *      )
 *      .select("min_value", "max_value", "sum_value", "avg_value")
 *
 */

streamlet NonNegativeFilter_interface {
    std_in : pipelineNestedGroup_types.NestedNumberGroup_stream in;
    std_out : pipelineNestedGroup_types.NestedNumberGroup_stream out;
}

impl NonNegativeFilter of NonNegativeFilter_interface {}

streamlet Reducer_interface {
    std_in : pipelineNestedGroup_types.NestedNumberGroup_stream in;

    #Stats_stream is inherited from pipelineSimple#
    std_out : pipelineSimple_types.Stats_stream out;
}

impl Reducer of Reducer_interface {}

streamlet PipelineNested_interface {
    std_in : pipelineNestedGroup_types.NestedNumberGroup_stream in;
    std_out : pipelineSimple_types.Stats_stream out;
}

impl PipelineNested of PipelineNested_interface {
    
    instance filter(NonNegativeFilter);
    instance reducer(Reducer);

    self.std_in => filter.std_in;
    filter.std_out => reducer.std_in;
    reducer.std_out => self.std_out;
}