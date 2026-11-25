/**
 * @author Toate Sika
 *
 * Stores low and high temperature data for a city/state 
 */
public class Temperature {
	private String myName = "John";
	private String city;
	private String state;
	private int low;
	private int high;

	public Temperature(String city, String state, int low, int high) {
		this.city = city;
		this.state = state;
		this.low = low;
		this.high = high;
	}

	/** Positive difference between the high and low temperature. */
	public int Differential() {
		return Math.abs(high - low);
	}

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public String getState() { return state; }
	public void setState(String state) { this.state = state; }

	public int getLow() { return low; }
	public void setLow(int low) { this.low = low; }

	public int getHigh() { return high; }
	public void setHigh(int high) { this.high = high; }

	@Override
	public String toString() {
		return city + ", " + state + " (low " + low + ", high " + high + ")";
	}
}