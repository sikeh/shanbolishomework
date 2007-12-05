// how to execute file sharing application

// run tracker first
java jade.Boot tracker:nbbt.TrackerAgent

// run seed next
java jade.Boot -container seed:nbbt.SeedAgent

// finally run peers
java jade.Boot -container c1:nbbt.ClientAgent c2:nbbt.ClientAgent c3:nbbt.ClientAgent c4:nbbt.ClientAgent c5:nbbt.ClientAgent c6:nbbt.ClientAgent c7:nbbt.ClientAgent c8:nbbt.ClientAgent c9:nbbt.ClientAgent c10:nbbt.ClientAgent
