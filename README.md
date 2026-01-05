# Pet Inventory Management System with AI Assistant
## Author: Huy Hoang Nguyen

The Pet Inventory Management System is a cross-platform desktop application built with **Java**, designed to manage pet shop products and sales efficiently.

The application integrates **AI assistant** capabilities for product queries and uses **Excel** for easy import/export of inventory, data analysis, and monthly financial reporting.

- Quick review: https://drive.google.com/file/d/1mIRjrrJvAeO98Lzt-ABCh-ONDAOFzr6_/view?usp=sharing
---

## Features

### Inventory Management
- Add, edit, and remove products directly in the TableView.
- Import products from **Excel (.xlsx)** to add or update multiple items at once.
- Export inventory to Excel for reporting or backup.
- Automatically highlight low-stock products for easy tracking.
- Supported categories: Dog Food, Cat Food, Dog Treat, Cat Treat, Accessories.
- Input validation ensures quantity and price are accurate.

<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 19 04" src="https://github.com/user-attachments/assets/fe5a3fbe-0674-4096-8534-12ca433b0b5d" />


---

### Sales & Checkout
- Add products to the cart by scanning UPC codes or manual entry.
- Checkout using **Cash** or **Card**.
- Automatically calculate **tax (HST 13%)** and round cash totals.
- Generate simulated receipts in HTML format, including item details, totals, and change.
- Update product quantities in the database in real-time after each sale.
<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 20 45" src="https://github.com/user-attachments/assets/56c192a0-0bfa-46ed-9b9b-01ddf2d5a82b" />
<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 21 05" src="https://github.com/user-attachments/assets/726115cd-e9a7-431d-8f48-5ffde26437da" />
<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 22 28" src="https://github.com/user-attachments/assets/62624e84-73e1-4125-b661-a67d99826ca7" />
---

### Reports & History
- Generate sales reports for a selected date range.
- Export detailed reports to **Excel**, including individual items per order.
- Search previous orders to view products, quantities, subtotals, and payment methods.
  
<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 22 56" src="https://github.com/user-attachments/assets/9ff0e719-da96-4232-94fd-c3efbc34a8e1" />

<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 22 43" src="https://github.com/user-attachments/assets/e44017be-dd05-4232-90f8-e67d9b08b0b3" />

- Excel export:
<img width="1728" height="1080" alt="Screenshot 2026-01-04 at 20 43 17" src="https://github.com/user-attachments/assets/56c04100-30fd-4044-91b9-406f4efcf980" />

<img width="1728" height="1080" alt="Screenshot 2026-01-04 at 20 25 02" src="https://github.com/user-attachments/assets/4c36b0be-4b8b-47e9-9bac-11ee7d53627d" />

<img width="1728" height="1080" alt="Screenshot 2026-01-04 at 20 25 09" src="https://github.com/user-attachments/assets/0f671b4b-627d-4623-9ffe-a97dbd67d4d9" />



---

### AI Assistant
- Integrated with **Gemini AI 2.5** to assist with queries.
- Can return JSON data for direct TableView display or plain text responses.
- Supports queries such as inventory lookup, top-selling items, low-stock alerts, or any other pet-related questions.

<img width="1639" height="931" alt="Screenshot 2026-01-04 at 20 45 06" src="https://github.com/user-attachments/assets/2285e2ba-11cf-4ccb-8ae8-95e8d5ddaf90" />

<img width="1637" height="927" alt="Screenshot 2026-01-04 at 20 24 00" src="https://github.com/user-attachments/assets/1b8d309b-9675-40ce-8ba5-a21070a7a203" />


---

## Technology Stack
- **Java 17+**
- **JavaFX** for user interface
- **MySQL** for database
- **Apache POI** for Excel import/export
- **Gson** and **JSON** for data processing
- **Gemini AI** for intelligent product queries

---
## Project Structure
<img width="402" height="570" alt="Screenshot 2026-01-04 at 21 00 55" src="https://github.com/user-attachments/assets/aabcb9cf-27e9-4451-b1d4-9517203b99c6" />

## Database Schema and Relationsip (MySQL)
<img width="791" height="191" alt="db_schema" src="https://github.com/user-attachments/assets/d93bdccf-a2d7-48b3-bb01-2fde7278dc06" />



