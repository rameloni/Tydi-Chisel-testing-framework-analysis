package pipelineSimple;

use pipelineSimple_types;

/**
 * This code implements the following spark code:
 *  
 *  Input: {timestamp, value}
 *  Output: {min_value, max_value, sum_value, avg_value}
 *  
 *  df.filter(col("value") >= 0)
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

#Interface for the non negative filter: df.filter(col("value") >= 0)#
streamlet NonNegativeFilter_interface<in_t: type> {
    std_in : pipelineSimple_types.NumberGroup_stream in;
    std_out : pipelineSimple_types.NumberGroup_stream out;
}

#Implementation of df.filter(col("value") >= 0)#
impl NonNegativeFilter of NonNegativeFilter_interface<pipelineSimple_types.NumberGroup_stream> {}

#Interface for the agg function#
streamlet Reducer_interface {
    std_in : pipelineSimple_types.NumberGroup_stream in;
    std_out : pipelineSimple_types.Stats_stream out;
}

#Implementation of the agg function#
impl Reducer of Reducer_interface {}

#Top level interface#
streamlet PipelineSimple_interface {
    std_in : pipelineSimple_types.NumberGroup_stream in;
    std_out : pipelineSimple_types.Stats_stream out;
}
#Top level implementation. It instantiates the subcomponents and connects them together#
impl PipelineSimple of PipelineSimple_interface {
    // Instantiate the subcomponents
    instance filter(NonNegativeFilter);
    instance reducer(Reducer);

    // Connect the subcomponents
    self.std_in => filter.std_in;
    filter.std_out => reducer.std_in;
    reducer.std_out => self.std_out;
}