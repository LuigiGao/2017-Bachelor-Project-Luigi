Smart Grid
This program simulates the energy flows between houses.

Overview
Weather: a system perform weather simulation
Network: a system perform energy flow simulation

Installation



Physics: 
Here the variables and functions for calcutating are showed

********************* Units Transfer *********************
1 W = 1 kg m^2 s^-3
1 J = 1 kg m^2 s^-2
1 W = 1 J s-1
1 J = 1 Ws
1 kWh = 1000 * 3600 W

********************* Variables *********************

************** Weather **************

***** Inputs *****
minimum/maximum wind_speed in 0.1m/s
global_radiation in 1J/cm^2
sunshine_duration in 0.1h
air_density in 1kg/m^3

***** Outputs *****
wind_speed in 0.1m/s
solar_radiation in 1J/cm^2 for 1h

************** Wind Turbine **************

***** Inputs *****
bladeLength in 1m
maxPowerOutput in 1W
Weather.wind_speed in 0.1m/s

***** Outputs *****
power in 1W

************** Photovolraic Panel **************

***** Inputs *****
panelArea in 1m^2
maxPowerOutput in 1W
Weather.solar_radiation in 1J/cm^2

***** Outputs *****
power in 1W

************** Prosumer **************

***** Inputs *****
energy_consumption in 1kWh
Weather.air_density in 1kg/m^3
Weather.wind_speed in 0.1m/s
Weather.solar_radiation in 1J/cm^2 for 1h

***** Outputs *****
+-energy in 1J



************** Network **************

***** Inputs *****
panelArea in 1m^2
maxPowerOutput in 1W
Weather.solar_radiation in 1J/cm^2

***** Outputs *****
joule in 1J

voltage in V
time_interval in h


