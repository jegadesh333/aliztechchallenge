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

##### BigQuery Link : https://bigquery.cloud.google.com/savedquery/679380573565:2108d11ef33049c9a7d457ec570bd8e1

```sql
SELECT
  fullVisitorID,
  (STRUCT(productSKU,
      productName)) AS product,
  quantity,
  totalPrice,
  consecutiveWeeksCount,
  lastweekdate
FROM (
  SELECT
    fullVisitorId,
    productSKU,
    v2ProductName AS productName,
    consecutiveWeeksCount,
    COUNT(*) AS quantity,
    SUM(productPrice) AS totalPrice,
    MAX(date) AS lastweekdate
  FROM (
    SELECT
      *
    FROM (
      SELECT
        fullVisitorId,
        P.productSKU,
        P.v2ProductName,
        P.productPrice,
        date
      FROM (
        SELECT
          fullVisitorId,
          h.product,
          PARSE_DATE('%Y%m%d',
            date) AS date
        FROM
          `bigquery-public-data.google_analytics_sample.ga_sessions_201707*`,
          UNNEST(hits) AS h ),
        UNNEST(product) AS P) AS a
    INNER JOIN (
      SELECT
        fullVisitorId AS visitiorID,
        productSKU AS productSKUs,
        COUNT(difference) AS consecutiveWeeksCount
      FROM (
        SELECT
          fullVisitorId,
          productSKU,
          week,
          leader,
          (week - leader) AS difference
        FROM (
          SELECT
            fullVisitorId,
            productSKU,
            week,
            LEAD(week) OVER (PARTITION BY fullVisitorId, productSKU ORDER BY week DESC) AS leader
          FROM (
            SELECT
              fullVisitorId,
              P.productSKU,
              P.v2ProductName,
              P.productPrice,
              date,
              EXTRACT(WEEK
              FROM
                date) AS week
            FROM (
              SELECT
                fullVisitorId,
                h.product,
                PARSE_DATE('%Y%m%d',
                  date) AS date
              FROM
                `bigquery-public-data.google_analytics_sample.ga_sessions_201707*`,
                UNNEST(hits) AS h),
              UNNEST(product) AS P)) )
      WHERE
        difference =1
      GROUP BY
        fullVisitorId,
        productSKU ) AS b
    ON
      a.fullVisitorId=b.visitiorID
      AND a.productSKU=b.productSKUs )
  GROUP BY
    fullVisitorId,
    productSKU,
    productName,
    consecutiveWeeksCount )

