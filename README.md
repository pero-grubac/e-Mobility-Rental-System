# ePJ2 - e-Mobility Rental System

ePJ2 is an e-mobility company that rents out electric cars, bikes, and scooters within the city of Java. The project's goal is to develop a system to track the use of these vehicles, generate detailed financial reports, and monitor the status of the rented vehicles.

## Features

<ul>
  <li><span style="font-size: 1.2em;">Vehicle Information: Stores data on electric cars, bikes, and scooters, including ID, purchase date, price, manufacturer, model, current battery level, range, and speed.</span></li>
  <li><span style="font-size: 1.2em;">Rental Tracking: Logs rental date and time, user information, current location, and usage duration.</span></li>
  <li><span style="font-size: 1.2em;">Billing: Calculates total rental cost based on travel duration and additional factors like distance, damage, discounts, and promotions.</span></li>
  <li><span style="font-size: 1.2em;">Simulation: Simulates vehicle rentals over time, generates detailed billing, and displays routes on a shared map.</span></li>
  <li><span style="font-size: 1.2em;">Reports: Generates daily and summary reports including income, discounts, promotions, maintenance costs, and more.</span></li>
  <li><span style="font-size: 1.2em;">Graphical Interfaces: Implements GUIs using JavaFX and Swing for map display, vehicle status, damage reports, and financial results.</span></li>
</ul>

## Billing Formulae

<ul>
  <li><span style="font-size: 1.2em;">Distance: Base Price * DISTANCE_NARROW for narrow areas, Base Price * DISTANCE_WIDE for wide areas.</span></li>
  <li><span style="font-size: 1.2em;">Damage: Base Price = 0 if damaged.</span></li>
  <li><span style="font-size: 1.2em;">Discounts: Base Price - (Base Price * DISCOUNT).</span></li>
  <li><span style="font-size: 1.2em;">Promotions: Base Price - (Base Price * DISCOUNT_PROM).</span></li>
  <li><span style="font-size: 1.2em;">Total Price: Base Price * Travel Duration.</span></li>
</ul>

## Reports

<ul>
  <li><span style="font-size: 1.2em;">Daily Reports: Show income, discounts, promotions, travel income, maintenance costs, repair costs, company costs, and taxes.</span></li>
  <li><span style="font-size: 1.2em;">Additional Functionalities: Identify the most and least profitable vehicles, and those with the highest repair costs.</span></li>
</ul>

## Simulation

<ul>
  <li><span style="font-size: 1.2em;">Parallel Simulation: Executes vehicle rentals in parallel threads, showing movement on a shared map.</span></li>
  <li><span style="font-size: 1.2em;">Pause and Resume: Pauses simulation at the end of a rental day and resumes the next day.</span></li>
</ul>

## Data Serialization

<ul>
  <li><span style="font-size: 1.2em;">Serialization: Stores vehicle objects and their status in binary files, which can be deserialized for later use.</span></li>
</ul>
