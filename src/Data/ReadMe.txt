Both Json and txt files are used to store the data used when processing program.

******** File description ********

1. Weather_*.txt: Store the daily weather data.

2. Prosumers.json: Store prosumers data.
		A array of prosumers which includes house number, a array of wind turbines 
		with blade length( in m ) and max power output( in Watt ), 
		a array of photovolatic panels with panel area( in m^2 ) and max power output( in Watt ), 
		and a location of file that contains the data of energy consumption.

3. Relation_*.json: Store relation between all prosumers defined in "Prosumers.json".
		A list of connections includes resistance( in R ) and max load( in J ) 
		between two house with specified number. 
		A house number for electric utility.
		
4. EnergyConsumption_*.json: Store the energy consumption data for an individual prosumer.
		A list of pair of time and energy consumption 
		which "0": "0.5" means prosumer cosumes 0.5kWh at 0:00.
		
******** Comments ********
Different combination of those files can be used to test the different topology at different situations.
