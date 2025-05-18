# vanguard-demo

To Run and Test:
1.	Generate CSV:
    o	Compile and run GenerateCsv.java. This will create large_game_sales.csv

2.	Run Spring Boot Application:
    o	Execute GameSalesApplication.java

3.	Task 5.3: Load CSV via /import
    o	Use Postman or curl to POST the large_game_sales.csv file to http://localhost:8080/api/import.
        Monitor the console logs for import progress and time.
        Will be able to see the time taken:
 
4.	Test /getGameSales (Task 3):
    o	http://localhost:8080/api/getGameSales?page=0&size=100
    o	http://localhost:8080/api/getGameSales?fromDate=2024-04-01&toDate=2024-04-05&page=0&size=100
    o	http://localhost:8080/api/getGameSales?priceLessThan=50&page=0&size=100
    o	http://localhost:8080/api/getGameSales?priceGreaterThan=80&page=0&size=100
    o	Test deep pagination: http://localhost:8080/api/getGameSales?page=999&size=100 (using 1M records). 
 
5.	Test /getTotalSales (Task 4):
    o	http://localhost:8080/api/getTotalSales?fromDate=2024-04-01&toDate=2024-04-03
    o	http://localhost:8080/api/getTotalSales?fromDate=2024-04-01&toDate=2024-04-03&gameNo=10
 

 

