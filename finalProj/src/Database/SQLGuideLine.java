package Database;

public class SQLGuideLine {
	
	// **********
	// basic query, by default bag semantics
	String query = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table>"
			+ "WHERE <condition1> AND <condition2> ...";
	// note that * can be used to select all attrs
	// note that 'harry''s' represents "harry's" in sql
	
	// basic query, to force set semantics
		String setQuery = "SELECT DISTINCT <attr1>, <attr2>, ..."
				+ "FROM <table>"
				+ "WHERE <condition1> AND <condition2> ...";
	
	// projection operation
	String projection = "SELECT (<attr>)"
			+ "FROM <table>";
	
	// renaming columns
	String renaming = "SELECT <attr_name> AS <new_attr_name>, <attr_name> AS <new_attr_name>"
			+ "FROM <table>"
			+ "WHERE (<conditions>)";
	
	// expressions as values in columns
	String expression = "SELECT <attr_name> AS <new_attr_name>, <attr_name> * para AS <result_name>, <some CONSTANT> AS <new_attr_name_notin_table>"
			+ "FROM <table>"
			+ "WHERE (<conditions>)";
	
	// **********
	// select patterns
	String applyPattern = "WHERE <attr_name> LIKE 'hello____'";  // matches string with hello and some 4 chars following
	// % repr any string
	// _ repr any one char
	// note that a pattern must be quoted like a string
	
	// multi-relation query
	String mutiRQuery = "SELECT <attr1>"
			+ "FROM <table1>, <table2>"
			+ "WHERE <condition_in_table1>, <condtion_in_table2>, ...";
	
	// self-cooies query
	String selfQuery = "SELECT <self_copy1.attr1>, <self_copy2.attr1>, ..."
			+ "FROM <self_copy1>, <self_copy2>"
			+ "WHERE <self_copy1.attr2> ? <self_copy2.attr2> AND ...";
	// note that <self_copy1.attr1> < <self_copy2.attr1> is to avoid a dup pair with the same value in attr1 from self1 and self2
	
	// **********
	// union / intersection/ difference, by default set semantics
	String unionQuery = "(<query1>)"
						+ "UNION / INTERSECT/ EXCEPT"
						+ "(<query2>)";
	// note that the attrs selected in both queries should be consistent
	
	// union / intersection/ difference, force bag semantics
		String bagUnionQuery = "(<query1>)"
							+ "UNION / INTERSECT/ EXCEPT ALL"
							+ "(<query2>)";
		// note that the attrs selected in both queries should be consistent
	
	// **********
	// subquery
	String subQuery = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table>"
			+ "WHERE <attr> = (<query>)";
	
	// membership check
	String membershipQuery = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table>"
			+ "WHERE <attr1> IN / NOT IN (<query>)";
	
	// existence check
	String existenceQuery = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table>"
			+ "WHERE EXISTS / NOT EXISTS (<query>)";
	// note that the condition is evaluated to be true iff the result of the subquery is nonempty
	
	
	// quanlifier check
	String qualifierQuery = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table>"
			+ "WHERE <attr> ? ALL / ANY (query)";
	
	// **********
	// subquery in FROM
	String fromSubQuery = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table>, (<query>)"
			+ "WHERE <condition1> AND <condition2> ...";
	
	// theta join on 2 tables 
	String thetaJoinQuery = "SELECT <attr1>, <attr2>, ..."
			+ "FROM <table1> JOIN <table2> ON <condition1>"
			+ "WHERE <condition2>";
	
	// **********
	// aggregation
	String aggrQuery = "SELECT SUM/AVG/MIN/MAX/COUNT(<attr1>) ..."
				+ "FROM <table>"
				+ "WHERE <condition1> ...";
	
	// aggregation with dups removed
		String uniqueAggrQuery = "SELECT SUM/AVG/MIN/MAX/COUNT(DISTINCT <attr1>) ..."
				+ "FROM <table>"
				+ "WHERE <condition1> ...";
	
	// **********
	// Grouping
	String groupingQuery = "SELECT <attr1>, <attr2>, ..."
				+ "FROM <table>"
				+ "WHERE <condition1> AND <condition2> ..."
				+ "GROUP BY <attr1>, <attr2>";
	// note that grouping take place after selection
	// note that if any aggregation is used then each element of a select clause must
	// either be aggregated or appear in a group-by clause
	
	// Having clause - selection on groups
	String havingQuery = "SELECT <attr1>, SUM(<attr2>)"
			+ "FROM <table>"
			+ "WHERE <condition1> AND <condition2> ..."
			+ "GROUP BY <attr1>"
			+ "HAVING MIN(<attr3> ? condtion)";  // first group all tuples by <attr1>
												// then find the grouping with the having clause satisfied
												// finally compute the sum of values of all attr2s in that group
	
	// **********
	// database insert
	String insert = "INSERT INTO <relation_name> (<attr1>, <attr2> ...)"
			+ "VALUES (<data_for_attr1>, <data_for_attr2> ...)";
	
	// insert of the result of a query
	String insertSubquery = "INSERT INTO <relation_name> (<attr1>, <attr2> ...)"
			+ "<query>";
	// note that the attrs in the subquery should match the attrs of the relation to be inserted into
	
	// database delete
	String deleteTuples = "DELETE FROM <relation_name>"
			+ "WHERE <condition1> AND <condition2> ...";
	
	// delete a whole relation
	String deleteRelation = "DELETE FROM <relation_name>";
	
	// database update
	String update = "UPDATE <relation_name>"
					+ "SET <new_assignment>"
					+ "WHERE <condition> ...";
	// note that 'prefix-' || <attr> means append a prefix to an old value of attr
	
	// database create table
	String createTable = "CREATE TABLE <table_name> "
			+ "(<attr1> <type(bytes)>, "
			+ "<attr2> <type(bytes)>)";
	// note that CHAR(20) means no matter how many char that you are going to use, 20 bytes will be allocated
	// while VARCHAR(20) means that only space used will be allocated and up to 20
	
	// types:
	// INT / BOOLEAN / REAL / FLOAT/ CHAR(N) / VARCHAR(N) / DATE & TIME ...
	
	// declaring keys
	String declarekeys = "CREATE TABLE <table_name>"
						+ "(<attr1> <type(bytes)>, "
						+ "<attr2> <type(bytes)>)"
						+ "UNIQUE (<attr1>, <attr2>)"
						+ "PRIMARY KEY (<attr1>)";
	// 1. note that primary key means that not 2 tuples have the same primary key
	// while (unique1, unique2, unique3) mean that no 2 tuples can have the same value simultaneously on unique1, unique2 and unique3
	// 2. note that can only have one primary key but many unique(s)
	// 3. note that primary keys can not take NULL values while unique(s) can
	
	// add constrain: not null
	String constrainNotNull = "CREATE TABLE <table_name>"
							+ "(<attr1> <type(bytes)> NOT NULL, "  // attr1 can not take NULL values
							+ "<attr2> <type(bytes)>)";
	
	// add constrain: default value
	String constrainByDefault = "CREATE TABLE <table_name>"
								+ "(<attr1> <type(bytes)> default 'd', "  // attr1 by default is d
								+ "<attr2> <type(bytes)>)";
	
	// changing columns: add an attr to a relation
	String addAttr = "ALTER TABLE <relation_name>"
					+ "ADD <new_attr_name> <type(size)> DEFAULT 'd'";
	
	// changing columns: drop an attr
	String dropAttr = "ALTER TABLE <relation_name>"
					+ "DROP <attr_name>";
	
	// **********
	// create a view
	
	

	

}
