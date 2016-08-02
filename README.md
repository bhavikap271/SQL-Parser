1) Indexing using B+ tree

You'll store an index file by default based on the primary key with B+ tree structure for each table imported/created. This time the primary key will be integer and auto-incremented, and the user will not be asked for the primary key while inserting a record.
You're supposed to update your B+ tree when a change occurs in the table, i.e., insert/update/delete
You should have an item in your menu to display the B+ tree for a table with a proper representation at any time.
You may use open source libraries online for B+ tree but make sure it supports all the operations described above.
 
Please note that your search time must not exceed ~10 iterations while locating any data in your big table. You should NOT search for the index file line by line. Refer to the slides with B+ trees with high number of fanouts. For example, if you're storing your B+ tree in a json format (B+ tree variable name: BTree), then you should be able to access a value, say 50, very fast like: BTree["0-100"]["40-60"]["50-60"][0]. (Recall B+ tree's indices are ranges, and only leaf nodes are variables)
 
 2) Implementation of an SQL parser
 
Similar to phase 1, you will provide an interface to users to query the database, but this time only correct SQL commands are allowed as input. Your system should support these operations:
Select From Where (SFW) queries
select a particular attribute / all attributes (*)
make joins by primary keys (your join operation must use B+ tree that you create in the first section)
set <, =, > constraints at any attribute in Where clause
search by a primary key using B+ tree
order by asc/desc
You can assume that there will NOT be nested queries
Similarly, insert/update/delete operations should be supported including applicable items (except join) in Select 
If the input SQL is not well-structured, you should give the corresponding alert and warning. Your parser should be able to understand
if a table/attribute doesn't exist
the order is wrong or a required item is missing, e.g., Select * From Where name='Joe'
the keys in constraints are missing, e.g., Select * From table Where a>10 And b<10 c>9
You can assume all column names are unique, no need to resolve ambiguities
Once you confirm the validity of the SQL, your dbms should display the correct result accordingly. This is where you will connect the first phase with the second one.

