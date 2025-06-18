<!DOCTYPE html>
<html>
<body>
    <h1 align="center">🚗🛵🚴 e-Mobility Rental System</h1>

  <p align="center">
    <img src="https://img.shields.io/badge/Java-Application-red?logo=java&logoColor=white" />
    <img src="https://img.shields.io/badge/Swing-GUI_Framework-blue?logo=awt&logoColor=white" />
  </p>
    <p>
        ePJ2 is an e-mobility company that rents out electric cars, bikes, and scooters within the city of Java. The project's goal is to develop a system to track the use of these vehicles, generate detailed financial reports, and monitor the status of the rented vehicles.
    </p>

  <h2>✨ Features</h2>
    <ul>
        <li>📋 <strong>Vehicle Information:</strong> Stores data on electric cars, bikes, and scooters, including ID, purchase date, price, manufacturer, model, current battery level, range, and speed.</li>
        <li>📊 <strong>Rental Tracking:</strong> Logs rental date and time, user information, current location, and usage duration.</li>
        <li>💰 <strong>Billing:</strong> Calculates total rental cost based on travel duration and additional factors like distance, damage, discounts, and promotions.</li>
        <li>🔄 <strong>Simulation:</strong> Simulates vehicle rentals over time, generates detailed billing, and displays routes on a shared map.</li>
        <li>📈 <strong>Reports:</strong> Generates daily and summary reports including income, discounts, promotions, maintenance costs, and more.</li>
        <li>🖥️ <strong>Graphical Interfaces:</strong> Implements GUIs using JavaFX and Swing for map display, vehicle status, damage reports, and financial results.</li>
    </ul>

   <h2>💵 Billing Formula</h2>
    <ul>
        <li>📏 <strong>Distance:</strong> <code>Base Price * DISTANCE_NARROW</code> for narrow areas, <code>Base Price * DISTANCE_WIDE</code> for wide areas.</li>
        <li>🔧 <strong>Damage:</strong> <code>Base Price = 0</code> if damaged.</li>
        <li>🎁 <strong>Discounts:</strong> <code>Base Price - (Base Price * DISCOUNT)</code>.</li>
        <li>📢 <strong>Promotions:</strong> <code>Base Price - (Base Price * DISCOUNT_PROM)</code>.</li>
        <li>⏱️ <strong>Total Price:</strong> <code>Base Price * Travel Duration</code>.</li>
    </ul>

   <h2>📊 Reports</h2>
    <ul>
        <li>📅 <strong>Daily Reports:</strong> Show income, discounts, promotions, travel income, maintenance costs, repair costs, company costs, and taxes.</li>
        <li>🚩 <strong>Additional Functionalities:</strong> Identify the most and least profitable vehicles, and those with the highest repair costs.</li>
    </ul>

   <h2>🔄 Simulation</h2>
    <ul>
        <li>⚙️ <strong>Parallel Simulation:</strong> Executes vehicle rentals in parallel threads, showing movement on a shared map.</li>
        <li>⏸️ <strong>Pause and Resume:</strong> Pauses simulation at the end of a rental day and resumes the next day.</li>
    </ul>

  <h2>📂 Data Serialization</h2>
    <ul>
        <li>💾 <strong>Serialization:</strong> Stores vehicle objects and their status in binary files, which can be deserialized for later use.</li>
    </ul>
</body>
</html>
