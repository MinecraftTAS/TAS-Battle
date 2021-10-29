package de.pfannekuchen.skywars.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import de.pfannekuchen.skywars.exceptions.ParseException;

public class LocationUtils {

	/**
	 * Safely parses a location from a string
	 * @param w (Nullable) World for the location. Uses first world if not given
	 * @param locationString String to parse as a location
	 * @return Location parsed from locationString
	 * @throws ParseException Throws whenever an invalid string is given
	 */
	public static Location parseLocation(@Nullable World w, String locationString) throws ParseException {
		// 1-3 numbers required and last two are optional
		String num1 = null;
		String num2 = null;
		String num3 = null;
		String num4 = null;
		String num5 = null;
		/* Try to split the locationString into the number variables */
		try {
			String[] numbers = locationString.split(" ");
			num1 = numbers[0];
			num2 = numbers[1];
			num3 = numbers[2];
			if (numbers.length > 3) {
				num4 = numbers[3];
				num5 = numbers[4];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ParseException("Unable to split positions from location string: " + locationString, e);
		}
		/* Create a Location from the given numbers */
		try {
			double x = Double.parseDouble(num1);
			double y = Double.parseDouble(num2);
			double z = Double.parseDouble(num3);
			if (num5 != null) {
				float yaw = Float.parseFloat(num4);
				float pitch = Float.parseFloat(num5);
				return new Location(w, x, y, z, yaw, pitch);
			}
			return new Location(w, x, y, z);
		} catch (NumberFormatException e) {
			throw new ParseException("Unable to parse number from location string: " + locationString, e);
		}
	}
	
}
