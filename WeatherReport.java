import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Comparator;

/**
 * @author Toate Sika
 * Date: Nov 24, 2026
 * Holds a linked list of Temperature objects and can check/sort it by city
 * or by high temperature using Collections.sort() or a custom merge sort.
 */
public class WeatherReport {
	private LinkedList<Temperature> temps = new LinkedList<Temperature>();

	/** Builds a small test list (9 objects) that is not sorted by city or high. */
	public WeatherReport() {
		temps.add(new Temperature("Phoenix", "Arizona", 55, 78));
		temps.add(new Temperature("Denver", "Colorado", 20, 45));
		temps.add(new Temperature("Boston", "Massachusetts", 25, 38));
		temps.add(new Temperature("Miami", "Florida", 68, 84));
		temps.add(new Temperature("Seattle", "Washington", 40, 50));
		temps.add(new Temperature("Atlanta", "Georgia", 45, 63));
		temps.add(new Temperature("Chicago", "Illinois", 15, 30));
		temps.add(new Temperature("Dallas", "Texas", 50, 70));
		temps.add(new Temperature("Albany", "New York", 18, 33));
	}

	/** Builds the list from a CSV file with a header row. */
	public WeatherReport(String fileName) throws FileNotFoundException {
		Scanner in = new Scanner(new File(fileName));
		try {
			if (in.hasNextLine()) {
				in.nextLine(); // skip header
			}
			while (in.hasNextLine()) {
				String line = in.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				// Columns: Average,City,Code,Direction,Full,Maximum,Minimum,Month,
				//          Precipitation,WindSpeed,State,Day,Year
				String[] p = line.split(",");
				String city = p[1];
				int high = Integer.parseInt(p[5].trim());
				int low = Integer.parseInt(p[6].trim());
				String state = p[10];
				temps.add(new Temperature(city, state, low, high));
			}
		} finally {
			in.close();
		}
	}

	public int size() {
		return temps.size();
	}

	private static Comparator<Temperature> comparatorFor(String by) {
		if (by.equalsIgnoreCase("City")) {
			return new SortByCity();
		} else if (by.equalsIgnoreCase("High")) {
			return new SortByHigh();
		}
		throw new IllegalArgumentException("Sort by must be \"City\" or \"High\", not: " + by);
	}

	private boolean isSorted(Comparator<Temperature> c) {
		Temperature prev = null;
		for (Temperature t : temps) {
			if (prev != null && c.compare(prev, t) > 0) {
				return false;
			}
			prev = t;
		}
		return true;
	}

	public boolean isSortedByCity() {
		return isSorted(new SortByCity());
	}

	public boolean isSortedByHigh() {
		return isSorted(new SortByHigh());
	}

	public void sortWithCollections(String by) {
		Collections.sort(temps, comparatorFor(by));
	}

	public void sortWithMerge(String by) {
		temps = mergeSort(temps, comparatorFor(by));
	}

	/** Recursive merge sort: split in half, sort each half, merge. */
	private static LinkedList<Temperature> mergeSort(LinkedList<Temperature> list, Comparator<Temperature> c) {
		if (list.size() <= 1) {
			return list;
		}
		LinkedList<Temperature> left = new LinkedList<Temperature>();
		LinkedList<Temperature> right = new LinkedList<Temperature>();
		int mid = list.size() / 2;
		int i = 0;
		for (Temperature t : list) { // iterator walk keeps this O(n) on a linked list
			if (i++ < mid) {
				left.add(t);
			} else {
				right.add(t);
			}
		}
		return merge(mergeSort(left, c), mergeSort(right, c), c);
	}

	private static LinkedList<Temperature> merge(LinkedList<Temperature> a, LinkedList<Temperature> b, Comparator<Temperature> c) {
		LinkedList<Temperature> result = new LinkedList<Temperature>();
		while (!a.isEmpty() && !b.isEmpty()) {
			// <= 0 takes from the left on ties, so the sort is stable
			if (c.compare(a.getFirst(), b.getFirst()) <= 0) {
				result.add(a.removeFirst());
			} else {
				result.add(b.removeFirst());
			}
		}
		result.addAll(a);
		result.addAll(b);
		return result;
	}

	public void print(int max) {
		int n = 0;
		for (Temperature t : temps) {
			if (n++ >= max) break;
			System.out.println("  " + t);
		}
	}

	/** Creates 4 identical reports, sorts two by the given key with each method, and times them. */
	private static void runTrial(String fileName, String by) throws FileNotFoundException {
		WeatherReport r1 = new WeatherReport(fileName);
		WeatherReport r2 = new WeatherReport(fileName);
		WeatherReport r3 = new WeatherReport(fileName);
		WeatherReport r4 = new WeatherReport(fileName);
		WeatherReport[] all = { r1, r2, r3, r4 };

		System.out.println("=== Sorting by " + by + " (" + r1.size() + " records) ===");
		for (int i = 0; i < all.length; i++) {
			boolean sorted = by.equals("City") ? all[i].isSortedByCity() : all[i].isSortedByHigh();
			System.out.println("List " + (i + 1) + " sorted before sort? " + sorted);
		}

		long start = System.nanoTime();
		r1.sortWithCollections(by);
		long collNs = System.nanoTime() - start;

		start = System.nanoTime();
		r2.sortWithMerge(by);
		long mergeNs = System.nanoTime() - start;

		boolean s1 = by.equals("City") ? r1.isSortedByCity() : r1.isSortedByHigh();
		boolean s2 = by.equals("City") ? r2.isSortedByCity() : r2.isSortedByHigh();
		System.out.println("Collections.sort: " + collNs / 1000000.0 + " ms, sorted? " + s1);
		System.out.println("Merge sort:       " + mergeNs / 1000000.0 + " ms, sorted? " + s2);
		System.out.println("First 3 after merge sort:");
		r2.print(3);
		System.out.println();
	}

	public static void main(String[] args) {
		String fileName = "weather.txt";
		try {
			// Default constructor check
			WeatherReport small = new WeatherReport();
			System.out.println("Default list sorted by city? " + small.isSortedByCity());
			System.out.println("Default list sorted by high? " + small.isSortedByHigh());
			small.sortWithMerge("City");
			System.out.println("After merge sort by city: " + small.isSortedByCity());
			System.out.println();

			runTrial(fileName, "City");
			runTrial(fileName, "High");
		} catch (FileNotFoundException e) {
			System.out.println("Can't find file " + fileName);
		} catch (Exception e) {
			System.out.println("Other error: " + e.getMessage());
		}
	}
}

/*
 * Comparator to sort by the city stored in a Temperature object
 */
class SortByCity implements Comparator<Temperature> {
    // Used for sorting in ascending order of City
    public int compare(Temperature t1, Temperature t2)
    {
        return t1.getCity().compareTo(t2.getCity());
    }
}

/*
 * Comparator to sort by the high temperature stored in a Temperature object
 */
class SortByHigh implements Comparator<Temperature> {
    // Used for sorting in ascending order of high temperature
    public int compare(Temperature t1, Temperature t2)
    {
        return t1.getHigh() - t2.getHigh();
    }
}