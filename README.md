# Aliz Tech Challenge 2

## Problem Statement :

There is a given input textfile (ASCII). Please create an application, that makes a new file ending with -"index.txt". To the index write every word in a new line, in alphabetical order, that occurs in the textfile. After each word please display in which line in the original textfile did the word appear. Separate the numbers with commas.

### Solution :

Developed a solution in Java to read the file and generate index file for the input.

#### Assumptions :

* Each line is terminated by new line and each word is seprated by white space.
* Index is created as case-insensitive. i.e apple & Apple are considered as one word in the output index i.e apple
* Special characters like !+.^:, are not required as part of the index file.

#### To run the Program :

file-indexer-1.0.jar is available in target folder in the repo.

`java -jar file-indexer-1.0.jar <input-file-path> <output-path>`

# Aliz Tech Challenge 3

## Problem Statement :

Assume that your marketing team is working on a new loyalty program. During the preparation they asked you to collect the customers who used to purchase the same product regularly. During the clarification you ended up in the following specification:
You’re going to use your company’s Google Analytics data
You need to collect the visitors who purchased the same goods in at least 2 consecutive weeks. You need to collect the total value of these purchases as well along with the product identifiers. 

### Solution :

#### Assumptions:

* I have used Google's stores Google Analytics data as input for this excercise as i dont have any other google analytics data.

#### Explanation:


#### Google BigQuery:
```sql
select fullVisitorID, (STRUCT(productSKU, productName)) AS product,quantity,totalPrice,consecutiveWeeksCount,lastweekdate
from 
  (select fullVisitorId,productSKU,v2ProductName as productName,consecutiveWeeksCount,count(*) as quantity,sum(productPrice) as totalPrice,max(date) as lastweekdate
  from
    (select *
    from 
      (select fullVisitorId,P.productSKU,P.v2ProductName,P.productPrice,date
      FROM 
        (
        SELECT fullVisitorId,h.product,PARSE_DATE('%Y%m%d', date) AS date
        FROM `bigquery-public-data.google_analytics_sample.ga_sessions_201707*`, UNNEST(hits) as h ),UNNEST(product) as P) as a
      INNER JOIN
      (select fullVisitorId as visitiorID,productSKU as productSKUs,count(difference) as consecutiveWeeksCount
      from 
        (select fullVisitorId,productSKU,week,leader,(week - leader) as difference 
        from 
        (select fullVisitorId,productSKU,week,lead(week) OVER (PARTITION BY fullVisitorId,productSKU ORDER BY week desc) as leader 
        from 
        (select fullVisitorId,P.productSKU,P.v2ProductName,P.productPrice,date, EXTRACT(WEEK FROM date) as week
        FROM 
        (
        SELECT fullVisitorId,h.product,PARSE_DATE('%Y%m%d', date) AS date
        FROM 
        `bigquery-public-data.google_analytics_sample.ga_sessions_201707*`, UNNEST(hits) as h),UNNEST(product) as P))
        )
      where difference =1 group by fullVisitorId,productSKU
      ) as b 
    on a.fullVisitorId=b.visitiorID and a.productSKU=b.productSKUs
    )
  group by fullVisitorId, productSKU, productName,consecutiveWeeksCount
  )

