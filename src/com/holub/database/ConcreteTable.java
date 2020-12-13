/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */
package com.holub.database;

import java.io.*;
import java.util.*;
import com.holub.tools.ArrayIterator;

/**
 * A concrete implementation of the {@link Table} interface that implements an
 * in-memory table. Most of the methods of this class are documented in the
 * {@link Table} class.
 * <p>
 * It's best to create instances of this class using the {@link TableFactory}
 * rather than <code>new</code>.
 * <p>
 * Note that a ConcreteTable is both serializable and "Cloneable", so you can
 * easily store it onto the disk in binary form or make a copy of it. Clone
 * implements a shallow copy, however, so it can be used to implement a rollback
 * of an insert or delete, but not an update.
 * <p>
 * This class is not thread safe.
 *
 * @include /etc/license.txt
 */

/* package */ class ConcreteTable implements Table {
	// Supporting clone() complicates the following declarations. In
	// particular, the fields can't be final because they're modified
	// in the clone() method. Also, the rows field has to be declared
	// as a Linked list (rather than a List) because Cloneable is made
	// public at the LinkedList level. If you declare it as a list,
	// you'll get an error message because clone()---for reasons that
	// are mysterious to me---is declared protected in Object.
	//
	// Be sure to change the clone() method if you modify anything about
	// any of these fields.

	private LinkedList rowSet = new LinkedList();
	private String[] columnNames;
	private String tableName;

	private transient boolean isDirty = false;
	private transient LinkedList transactionStack = new LinkedList();

	/**********************************************************************
	 * Create a table with the given name and columns.
	 * 
	 * @param tableName the name of the table.
	 * @param an        array of Strings that specify the column names.
	 */
	public ConcreteTable(String tableName, String[] columnNames) {
		this.tableName = tableName;
		this.columnNames = (String[]) columnNames.clone();
	}

	/**********************************************************************
	 * Return the index of the named column. Throw an IndexOutOfBoundsException if
	 * the column doesn't exist.
	 */
	private int indexOf(String columnName) {
		for (int i = 0; i < columnNames.length; ++i)
			if (columnNames[i].equals(columnName))
				return i;

		throw new IndexOutOfBoundsException("Column (" + columnName + ") doesn't exist in " + tableName);
	}

	// @simple-construction-end
	//
	/**********************************************************************
	 * Create a table using an importer. See {@link CSVImporter} for an example.
	 */
	public ConcreteTable(Table.Importer importer) throws IOException {
		importer.startTable();

		tableName = importer.loadTableName();
		int width = importer.loadWidth();
		Iterator columns = importer.loadColumnNames();

		this.columnNames = new String[width];
		for (int i = 0; columns.hasNext();)
			columnNames[i++] = (String) columns.next();

		while ((columns = importer.loadRow()) != null) {
			Object[] current = new Object[width];
			for (int i = 0; columns.hasNext();)
				current[i++] = columns.next();
			this.insert(current);
		}
		importer.endTable();
	}

	// ----------------------------------------------------------------------
	public void export(Table.Exporter exporter) throws IOException {
		exporter.startTable();
		exporter.storeMetadata(tableName, columnNames.length, rowSet.size(), new ArrayIterator(columnNames));

		for (Iterator i = rowSet.iterator(); i.hasNext();)
			exporter.storeRow(new ArrayIterator((Object[]) i.next()));

		exporter.endTable();
		isDirty = false;
	}

	// @import-export-end
	// ----------------------------------------------------------------------
	// Inserting
	//
	public int insert(String[] intoTheseColumns, Object[] values) {
		assert (intoTheseColumns.length == values.length) : "There must be exactly one value for "
				+ "each specified column";

		Object[] newRow = new Object[width()];

		for (int i = 0; i < intoTheseColumns.length; ++i)
			newRow[indexOf(intoTheseColumns[i])] = values[i];

		doInsert(newRow);
		return 1;
	}

	// ----------------------------------------------------------------------
	public int insert(Collection intoTheseColumns, Collection values) {
		assert (intoTheseColumns.size() == values.size()) : "There must be exactly one value for "
				+ "each specified column";

		Object[] newRow = new Object[width()];

		Iterator v = values.iterator();
		Iterator c = intoTheseColumns.iterator();
		while (c.hasNext() && v.hasNext())
			newRow[indexOf((String) c.next())] = v.next();

		doInsert(newRow);
		return 1;
	}

	// ----------------------------------------------------------------------
	public int insert(Map row) { // A map is considered to be "ordered," with the order defined
									// as the order in which an iterator across a "view" returns
									// values. My reading of this statement is that the iterator
									// across the keySet() visits keys in the same order as the
									// iterator across the values() visits the values.

		return insert(row.keySet(), row.values());
	}

	// ----------------------------------------------------------------------
	public int insert(Object[] values) {
		assert values.length == width() : "Values-array length (" + values.length + ") "
				+ "is not the same as table width (" + width() + ")";

		doInsert((Object[]) values.clone());
		return 1;
	}

	// ----------------------------------------------------------------------
	public int insert(Collection values) {
		return insert(values.toArray());
	}

	// ----------------------------------------------------------------------
	private void doInsert(Object[] newRow) {
		rowSet.add(newRow);
		registerInsert(newRow);
		isDirty = true;
	}

	// @insert-end
	// ----------------------------------------------------------------------
	// Traversing and cursor-based Updating and Deleting
	//
	public Cursor rows() {
		return new Results();
	}

	// ----------------------------------------------------------------------
	private final class Results implements Cursor {
		private final Iterator rowIterator = rowSet.iterator();
		private Object[] row = null;

		public String tableName() {
			return ConcreteTable.this.tableName;
		}

		public boolean advance() {
			if (rowIterator.hasNext()) {
				row = (Object[]) rowIterator.next();
				return true;
			}
			return false;
		}

		public int columnCount() {
			return columnNames.length;
		}

		public String columnName(int index) {
			return columnNames[index];
		}

		public Object column(String columnName) {
			return row[indexOf(columnName)];
		}

		public Iterator columns() {
			return new ArrayIterator(row);
		}

		public boolean isTraversing(Table t) {
			return t == ConcreteTable.this;
		}

		// This method is for use by the outer class only, and is not part
		// of the Cursor interface.
		private Object[] cloneRow() {
			return (Object[]) (row.clone());
		}

		public Object update(String columnName, Object newValue) {
			int index = indexOf(columnName);

			// The following test is required for undo to work correctly.
			if (row[index] == newValue)
				throw new IllegalArgumentException("May not replace object with itself");

			Object oldValue = row[index];
			row[index] = newValue;
			isDirty = true;

			registerUpdate(row, index, oldValue);
			return oldValue;
		}

		public void delete() {
			Object[] oldRow = row;
			rowIterator.remove();
			isDirty = true;

			registerDelete(oldRow);
		}
	}

	// @cursor-end
	// ----------------------------------------------------------------------
	// Undo subsystem.
	//
	private interface Undo {
		void execute();
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private class UndoInsert implements Undo {
		private final Object[] insertedRow;

		public UndoInsert(Object[] insertedRow) {
			this.insertedRow = insertedRow;
		}

		public void execute() {
			rowSet.remove(insertedRow);
		}
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private class UndoDelete implements Undo {
		private final Object[] deletedRow;

		public UndoDelete(Object[] deletedRow) {
			this.deletedRow = deletedRow;
		}

		public void execute() {
			rowSet.add(deletedRow);
		}
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private class UndoUpdate implements Undo {
		private Object[] row;
		private int cell;
		private Object oldContents;

		public UndoUpdate(Object[] row, int cell, Object oldContents) {
			this.row = row;
			this.cell = cell;
			this.oldContents = oldContents;
		}

		public void execute() {
			row[cell] = oldContents;
		}
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public void begin() {
		transactionStack.addLast(new LinkedList());
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private void register(Undo op) {
		((LinkedList) transactionStack.getLast()).addLast(op);
	}

	private void registerUpdate(Object[] row, int cell, Object oldContents) {
		if (!transactionStack.isEmpty())
			register(new UndoUpdate(row, cell, oldContents));
	}

	private void registerDelete(Object[] oldRow) {
		if (!transactionStack.isEmpty())
			register(new UndoDelete(oldRow));
	}

	private void registerInsert(Object[] newRow) {
		if (!transactionStack.isEmpty())
			register(new UndoInsert(newRow));
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public void commit(boolean all) throws IllegalStateException {
		if (transactionStack.isEmpty())
			throw new IllegalStateException("No BEGIN for COMMIT");
		do {
			LinkedList currentLevel = (LinkedList) transactionStack.removeLast();

			if (!transactionStack.isEmpty())
				((LinkedList) transactionStack.getLast()).addAll(currentLevel);

		} while (all && !transactionStack.isEmpty());
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public void rollback(boolean all) throws IllegalStateException {
		if (transactionStack.isEmpty())
			throw new IllegalStateException("No BEGIN for ROLLBACK");
		do {
			LinkedList currentLevel = (LinkedList) transactionStack.removeLast();

			while (!currentLevel.isEmpty())
				((Undo) currentLevel.removeLast()).execute();

		} while (all && !transactionStack.isEmpty());
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// @undo-end
	// -----------------------------------------------------------------
	public int update(Selector where) {
		Results currentRow = (Results) rows();
		Cursor[] envelope = new Cursor[] { currentRow };
		int updated = 0;

		while (currentRow.advance()) {
			if (where.approve(envelope)) {
				where.modify(currentRow);
				++updated;
			}
		}

		return updated;
	}

	// ----------------------------------------------------------------------
	public int delete(Selector where) {
		int deleted = 0;

		Results currentRow = (Results) rows();
		Cursor[] envelope = new Cursor[] { currentRow };

		while (currentRow.advance()) {
			if (where.approve(envelope)) {
				currentRow.delete();
				++deleted;
			}
		}
		return deleted;
	}

	// Housekeeping stuff
	//
	public String name() {
		return tableName;
	}

	public void rename(String s) {
		tableName = s;
	}

	public boolean isDirty() {
		return isDirty;
	}

	private int width() {
		return columnNames.length;
	}

	// ----------------------------------------------------------------------
	public Object clone() throws CloneNotSupportedException {
		ConcreteTable copy = (ConcreteTable) super.clone();
		copy.rowSet = (LinkedList) rowSet.clone();
		copy.columnNames = (String[]) columnNames.clone();
		copy.tableName = tableName;
		return copy;
	}

	// ----------------------------------------------------------------------
	public String toString() {
		StringBuffer out = new StringBuffer();

		out.append(tableName == null ? "<anonymous>" : tableName);
		out.append("\n");

		for (int i = 0; i < columnNames.length; ++i)
			out.append(columnNames[i] + "\t");
		out.append("\n----------------------------------------\n");

		for (Cursor i = rows(); i.advance();) {
			Iterator columns = i.columns();
			while (columns.hasNext()) {
				Object next = columns.next();
				if (next == null)
					out.append("null\t");
				else
					out.append(next.toString() + "\t");
			}
			out.append('\n');
		}
		return out.toString();
	}

	// ----------------------------------------------------------------------
	public final static class Test {
		public static void main(String[] args) {
			new Test().test();
		}

		Table people = TableFactory.create("people", new String[] { "last", "first", "addrId" });

		Table address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });

		public void report(Throwable t, String message) {
			System.out.println(message + " FAILED with exception toss");
			t.printStackTrace();
			System.exit(1);
		}

		public void test() {
			try {
				testInsert();
			} catch (Throwable t) {
				report(t, "Insert");
			}
			try {
				testUpdate();
			} catch (Throwable t) {
				report(t, "Update");
			}
			try {
				testDelete();
			} catch (Throwable t) {
				report(t, "Delete");
			}
			try {
				testStore();
			} catch (Throwable t) {
				report(t, "Store/Load");
			}
			try {
				testJoin();
			} catch (Throwable t) {
				report(t, "Join");
			}
			try {
				testUndo();
			} catch (Throwable t) {
				report(t, "Undo");
			}
		}

		public void testInsert() {
			people.insert(new Object[] { "Holub", "Allen", "1" });
			people.insert(new Object[] { "Flintstone", "Wilma", "2" });
			people.insert(new String[] { "addrId", "first", "last" }, new Object[] { "2", "Fred", "Flintstone" });

			address.insert(new Object[] { "1", "123 MyStreet", "Berkeley", "CA", "99999" });

			List l = new ArrayList();
			l.add("2");
			l.add("123 Quarry Ln.");
			l.add("Bedrock ");
			l.add("XX");
			l.add("12345");
			assert (address.insert(l) == 1);

			l.clear();
			l.add("3");
			l.add("Bogus");
			l.add("Bad");
			l.add("XX");
			l.add("12345");

			List c = new ArrayList();
			c.add("addrId");
			c.add("street");
			c.add("city");
			c.add("state");
			c.add("zip");
			assert (address.insert(c, l) == 1);

			System.out.println(people.toString());
			System.out.println(address.toString());

//			try {
//				people.insert(new Object[] { "x" });
//				throw new AssertionError("insert wrong number of fields test failed");
//			} catch (Throwable t) {
//				/* Failed correctly, do nothing */ }
//
//			try {
//				people.insert(new String[] { "?" }, new Object[] { "y" });
//				throw new AssertionError("insert-nonexistent-field test failed");
//			} catch (Exception t) {
//				/* Failed correctly, do nothing */ }
		}

		public void testUpdate() {
			System.out.println("update set state='YY' where state='XX'");
			int updated = address.update(new Selector() {
				public boolean approve(Cursor[] tables) {
					return tables[0].column("state").equals("XX");
				}

				public void modify(Cursor current) {
					current.update("state", "YY");
				}
			});
			print(address);
			System.out.println(updated + " rows affected\n");
		}

		public void testDelete() {
			System.out.println("delete where street='Bogus'");
			int deleted = address.delete(new Selector.Adapter() {
				public boolean approve(Cursor[] tables) {
					return tables[0].column("street").equals("Bogus");
				}
			});
			print(address);
			System.out.println(deleted + " rows affected\n");
		}

		public void testStore() throws IOException, ClassNotFoundException { // Flush the table to disk, then reread it.
																				// Subsequent tests that use the
																				// "people" table will
																				// fail if this operation fails.

			Writer out = new FileWriter("people");
			people.export(new CSVExporter(out));
			out.close();

			Reader in = new FileReader("people");
			people = new ConcreteTable(new CSVImporter(in));
			in.close();
		}

		public void testJoin() {
			// First test a two-way join

			System.out.println("\nSELECT first,last,street,city,state,zip" + " FROM people, address"
					+ " WHERE people.addrId = address.addrId");

			// Collection version chains to String[] version,
			// so this code tests both:
			List columns = new ArrayList();
			columns.add("first");
			columns.add("last");
			columns.add("street");
			columns.add("city");
			columns.add("state");
			columns.add("zip");

			List tables = new ArrayList();
			tables.add(address);

			SelectAlgorithm selectAlgorithm = new DefaultSelect(people, tables, new Selector.Adapter() {
				public boolean approve(Cursor[] tables) {
					return tables[0].column("addrId").equals(tables[1].column("addrId"));
				}
			});
			Table result = selectAlgorithm.doSelect(); // WHERE people.addrID = address.addrID

			print(result);
			System.out.println("");

			// Now test a three-way join
			//
			System.out.println("\nSELECT first,last, state, text" + " FROM people, address, third"
					+ " WHERE (people.addrId = address.addrId)" + " AND (people.addrId = third.addrId)");

			Table third = TableFactory.create("third", new String[] { "addrId", "text" });
			third.insert(new Object[] { "1", "addrId=1" });
			third.insert(new Object[] { "2", "addrId=2" });
			
			selectAlgorithm = new ColumnsSelect(
				new DefaultSelect(people, new ArrayList() { { add(address); add(third); } } , 
					new Selector.Adapter() {
						public boolean approve(Cursor[] tables) {
							return (tables[0].column("addrId").equals(tables[1].column("addrId"))
									&& tables[0].column("addrId").equals(tables[2].column("addrId")));
						}
			}), new ArrayList() { { add("last"); add("first"); add("state"); add("text"); } });
			result = selectAlgorithm.doSelect();

			System.out.println(result.toString() + "\n");
		}

		public void testUndo() {
			// Verify that commit works properly
			people.begin();
			System.out.println("begin/insert into people (Solo, Han, 5)");

			people.insert(new Object[] { "Solo", "Han", "5" });
			System.out.println(people.toString());

			people.begin();
			System.out.println("begin/insert into people (Lea, Princess, 6)");

			people.insert(new Object[] { "Lea", "Princess", "6" });
			System.out.println(people.toString());

			System.out.println("commit(THIS_LEVEL)\n" + "rollback(Table.THIS_LEVEL)\n");
			people.commit(Table.THIS_LEVEL);
			people.rollback(Table.THIS_LEVEL);
			System.out.println(people.toString());

			// Now test that nested transactions work correctly.

			System.out.println(people.toString());

			System.out.println("begin/insert into people (Vader,Darth, 4)");
			people.begin();
			people.insert(new Object[] { "Vader", "Darth", "4" });
			System.out.println(people.toString());

			System.out.println("begin/update people set last=Skywalker where last=Vader");

			people.begin();
			people.update(new Selector() {
				public boolean approve(Cursor[] tables) {
					return tables[0].column("last").equals("Vader");
				}

				public void modify(Cursor current) {
					current.update("last", "Skywalker");
				}
			});
			System.out.println(people.toString());

			System.out.println("delete from people where last=Skywalker");
			people.delete(new Selector.Adapter() {
				public boolean approve(Cursor[] tables) {
					return tables[0].column("last").equals("Skywalker");
				}
			});
			System.out.println(people.toString());

			System.out.println("rollback(Table.THIS_LEVEL) the delete and update");
			people.rollback(Table.THIS_LEVEL);
			System.out.println(people.toString());

			System.out.println("rollback(Table.THIS_LEVEL) insert");
			people.rollback(Table.THIS_LEVEL);
			System.out.println(people.toString());
		}

		public void print(Table t) { // tests the table iterator
			Cursor current = t.rows();
			while (current.advance()) {
				for (Iterator columns = current.columns(); columns.hasNext();)
					System.out.print((String) columns.next() + " ");
				System.out.println("");
			}
		}
	}
	
}
