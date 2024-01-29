#include <stdlib.h>
#include <iostream>
#include <verilated.h>
#include <verilated_vcd_c.h>
#include <verilated_vpi.h> // Required to get definitions

#include "obj_dir/VDetectTwoOnes.h"

namespace hgdb
{
    void initialize_hgdb_runtime_cxx();
}

vluint64_t main_time = 0; // Current simulation time

double sc_time_stamp()
{                     // Called by $time in Verilog
    return main_time; // converts to double, to match
    // what SystemC does
}

void advance_clock(VDetectTwoOnes &dut, int increment = 1, bool call_update = false)
{
    if (call_update)
    {
        // notice that nextSimTime has to be called *right before* you trigger
        // posedge clock
        // to allow proper updates especially from the IO stimulus
        // call eval before the clock posedge, as follows:
        // dut.in = 1;
        // dut.eval();
        // callCBs();
        // dut.clk = 1;
        // eval();
        // force verilator to handle cbNextSimTime
        VerilatedVpi::callCbs(cbNextSimTime);
    }
    dut.eval();
    dut.clock = ~dut.clock;
    dut.eval();
    VerilatedVpi::callValueCbs(); // required to call callbacks
    VerilatedVpi::callTimedCbs();
    main_time += increment;
}

void reset(VDetectTwoOnes &dut)
{
    // synchronous reset
    dut.reset = 1;
    advance_clock(dut, 10, true);
    dut.reset = 0;
    advance_clock(dut, 10, true);
}

int main(int argc, char **argv)
{
    const auto inputs = {0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0};
    Verilated::commandArgs(argc, argv);
    VDetectTwoOnes dut;
    Verilated::internalsDump(); // See scopes to help debug
    // Create an instance of our module under test
    hgdb::initialize_hgdb_runtime_cxx();

    dut.clock = 0;
    reset(dut);


    for (auto input : inputs)
    {
        dut.io_in = input;
        advance_clock(dut, 1, true);
        advance_clock(dut, 1);
        std::cout << "input = " << input << ", output = " << dut.io_out << std::endl;
    }

    dut.final();

    exit(EXIT_SUCCESS);
}