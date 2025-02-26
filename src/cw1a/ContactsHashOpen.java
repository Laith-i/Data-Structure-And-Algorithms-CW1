package cw1a;

/**
 *
 * @author DL 2025-01
 */
public class ContactsHashOpen implements IContactDB {  
    private final int initialTableCapacity = 991;
    private Contact[] table;
    private int tableCapacity;
    private int numEntries;
    private int totalVisited = 0;
    private static final Contact Deleted_Entry = new Contact("Removed", "Removed");
    private static final double maxLoadFactor = 50.0;
    public int getNumEntries(){return numEntries;}
    public void resetTotalVisited() {totalVisited = 0;}
    public int getTotalVisited() {return totalVisited;}

    public ContactsHashOpen() {
        System.out.println("Hash Table with open addressing");
        this.tableCapacity = initialTableCapacity;
        table = new Contact[tableCapacity];
        clearDB();
    }

    /**
     * Empties the database.
     * note : anagram
     * @pre true
     */
    public void clearDB() {
        for (int i = 0; i != table.length; i++) {
            table[i] = null;
        }
        numEntries = 0;
    }

    private int hash(String s) {

        int hashing_Value = 0;
        for (int i = 0; i < s.length(); i++) {
            hashing_Value = (hashing_Value << 17) ^ s.charAt(i);
        }
        return Math.abs(hashing_Value) % table.length;
        
    }
    /**
     * Helper for the newly implemented remove function
     */
    private int locateIndexForLookup(String name) {
        int hashedIndex = hash(name);
        int currentIndex = hashedIndex;
        int probeStep = 1;
        int visitedCount = 0; // Local counter for debugging

        while (table[currentIndex] != null) {
            visitedCount++;
            System.out.println("Visited bucket " + currentIndex + ": " + table[currentIndex]);
            if (table[currentIndex] != Deleted_Entry && table[currentIndex].getName().equals(name)) {
                System.out.println("Total buckets visited in search: " + visitedCount);
                return currentIndex;
            }
            currentIndex = (hashedIndex + probeStep * probeStep) % table.length;
            probeStep++;
            if (probeStep == table.length) break;
        }
        System.out.println("Total buckets visited in search (not found): " + visitedCount);
        return currentIndex;
    }

    /**
     * Helper for the newly implemented remove function
     */
    private int locateIndexForInsertion(String name) {
        int initialIndex = hash(name);
        int currentIndex = initialIndex;
        int probeStep = 1;
        int firstDeletedIndex = -1;
        int visitedCount = 0;

        while (table[currentIndex] != null) {
            visitedCount++;
            System.out.println("Visited bucket " + currentIndex + ": " + table[currentIndex]);
            if (table[currentIndex] == Deleted_Entry) {
                if (firstDeletedIndex == -1) {
                    firstDeletedIndex = currentIndex;
                }
            } else if (table[currentIndex].getName().equals(name)) {
                totalVisited += visitedCount;
                return currentIndex;
            }
            currentIndex = (initialIndex + probeStep * probeStep) % table.length;
            probeStep++;
            if (probeStep == table.length) break;
        }
        totalVisited += visitedCount;
        return (firstDeletedIndex != -1) ? firstDeletedIndex : currentIndex;
    }


    public double loadFactor() {
        /**double loadFactorValue = (double) numEntries / table.length * 100.0;
        System.out.printf("Current load factor: %.2f%%\n", loadFactorValue);
        return loadFactorValue;

         note: load factor Debuger
         */
        return (double) numEntries / table.length * 100.0;
    }

    private int findPos(String name) {
        assert name != null && !name.trim().equals("");
        int intialpPos = hash(name);
        int pos = intialpPos;
        int numVisited = 1;
        System.out.println("finding " + pos + ": " + name);
        while (table[pos] != null && !name.equals(table[pos].getName())) {
            System.out.println("Visiting bucket " + pos + ": " + table[pos]);
            numVisited++;
            pos = (intialpPos + (numVisited - 1) * (numVisited - 1)) % table.length;
            if (pos < 0) {
                pos += table.length;
            }
        }
        System.out.println("number of buckets visited = " + numVisited);
        totalVisited += numVisited;
        assert table[pos] == null || name.equals(table[pos].getName());
        return pos;
    }

    /**
     * Determines whether a Contact name exists as a key inside the database
     *
     * @pre name not null or empty string
     * @param name the Contact name (key) to locate
     * @return true iff the name exists as a key in the database
     */
    public boolean containsName(String name) {
        assert name != null && !name.equals("");
        int pos = findPos(name);
        return get(name) != null;
    }

    /**
     * Returns a Contact object mapped to the supplied name.
     *
     * @pre name not null or empty string
     * @param name The Contact name (key) to locate
     * @return the Contact object mapped to the key name if the name exists as
     * key in the database, otherwise null
     */
    @Override
    public Contact get(String name) {
        assert name != null && !name.trim().equals("");
        int pos = locateIndexForLookup(name);
        if (table[pos] != null && table[pos] != Deleted_Entry && table[pos].getName().equals(name)) {
            return table[pos];
        }
        return null;
    }

    /**
     * Returns the number of contacts in the database
     *
     * @pre true
     * @return number of contacts in the database. 0 if empty
     */
    public int size() {return numEntries; }

    /**
     * Determines if the database is empty or not.
     *
     * @pre true
     * @return true iff the database is empty
     */
    @Override
    public boolean isEmpty() {return numEntries == 0; }

    
    private Contact putWithoutResizing(Contact contact) {
      String name = contact.getName();
      int pos = findPos(name);
      Contact previous;
      assert table[pos] == null || name.equals(table[pos].getName());
      previous = table[pos]; // old value
      if (previous == null) { // new entry
         table[pos] = contact;
         numEntries++;
      } else {
         table[pos] = contact;
      }
      return previous;
   }
    
    /**
     * Inserts a contact object into the database, with the key of the supplied
     * contact's name. Note: If the name already exists as a key, then then the
     * original entry is overwritten. This method should return the previous
     * associated value if one exists, otherwise null
     *
     * @pre contact not null or empty string
     */
    public Contact put(Contact contact) {
        assert contact != null;
        String name = contact.getName();
        assert name != null && !name.trim().equals("");

        int pos = locateIndexForInsertion(name);
        Contact previous = null;
        if (table[pos] == null || table[pos] == Deleted_Entry) {
            table[pos] = contact;
            numEntries++;
        } else {
            previous = table[pos];
            table[pos] = contact;
        }

        if (previous == null && loadFactor() > maxLoadFactor) {
            resizeTable();
        }
        return previous;
    }
    /**
     * Removes and returns a contact from the database, with the key the
     * supplied name.
     *
     * @param name The name (key) to remove.
     * @pre name not null or empty string
     * @return the removed contact object mapped to the name, or null if the
     * name does not exist.
     * note: use two arrays and set it to a boolean all true, when it is deleted then set it as false or set it as deleted entry and remove it
     */
    public Contact remove(String name) {
        assert name != null && !name.trim().equals("");
        int pos = findPos(name);
        if (table[pos] == null || table[pos] == Deleted_Entry) {
            return null;
        }
        Contact removedContact = table[pos];
        table[pos] = Deleted_Entry;
        numEntries--;
        return removedContact;
    }

    /**
     * Prints the names and IDs of all the contacts in the database in
     * alphabetic order.
     *
     * @pre true
     */
    public void displayDB() {
        // not yet ordered
        double currentLoad = (double) numEntries / table.length * 100.0;
        System.out.println("capacity " + table.length + " size " + numEntries
                + " Load factor " + String.format("%.2f%%", currentLoad));
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null || table[i] == Deleted_Entry) {
                System.out.println(i + " " + "_____");
            } else {
                System.out.println(i + " " + table[i].toString());
            }
        }


        Contact[] validContacts = new Contact[numEntries]; // OK to use Array.sort
        int j = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && table[i] != Deleted_Entry) {
                validContacts[j] = table[i];
                j++;
            }
        }


        quicksort(validContacts, 0, j - 1);
        for (int i = 0; i < j; i++) {
            System.out.println(i + " " + validContacts[i].toString());
        }
    }

    private void quicksort(Contact[] a, int low, int high) {
        assert a != null && 0 <= low && low <= high && high < a.length;
        int i = low, j = high;
        Contact temp;
        if (high >= 0) { // can't get pivot for empty sequence
            String pivot = a[(low + high) / 2].getName();
            while (i <= j) {
                while (a[i].getName().compareTo(pivot) < 0) i++;
                while (a[j].getName().compareTo(pivot) > 0) j--;
                // forall k :low ..i -1: a[k] < pivot && 
                // forall k: j+1 .. high: a[k] > pivot &&
                // a[i] >= pivot && a[j] <= pivot
                if (i <= j) {
                    temp = a[i]; a[i] = a[j]; a[j] = temp;
                    i++; j--;
                }
                if (low < j) quicksort(a, low, j); // recursive call 
                if (i < high) quicksort(a, i, high); // recursive call 
            }
        }
    }

    private void resizeTable() {
        Contact[] oldTable = table;
        int oldCapacity = table.length;
        int newCapacity = nextPrime(2 * oldCapacity);
        table = new Contact[newCapacity];
        numEntries = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable[i] != null && oldTable[i] != Deleted_Entry) {
                put(oldTable[i]);
            }
        }
    }

    // Helper method: returns the next prime number >= n.
    private int nextPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }

    // Simple prime check.
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
} 

